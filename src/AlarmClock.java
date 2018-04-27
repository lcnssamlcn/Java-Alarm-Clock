import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;

import java.util.Calendar;

public class AlarmClock extends Clock {
    private AlarmClockHand hrHand;
    private AlarmClockHand minHand;

    private static final double HR_HAND_ROT_X = 8 * Clock.SCALING_FACTOR;
    private static final double HR_HAND_ROT_Y = 84 * Clock.SCALING_FACTOR;
    private static final double MIN_HAND_ROT_X = 8 * Clock.SCALING_FACTOR;
    private static final double MIN_HAND_ROT_Y = 122 * Clock.SCALING_FACTOR;

    private AudioClip normalPacedSound;
    private AudioClip fastPacedSound;

    // alarm
    private int hr;   // [0, 24)
    private int min;  // [0, 60)

    private volatile boolean stopped = true;
    private volatile boolean snoozed = false;

    private static final int SNOOZE_PERIOD = 3;  // in min

    private Thread ringTask;
    // private Thread keyListeningTask;
    private Thread snoozeTask;

    public AlarmClock(int hr, int min) {
        super();

        this.hr = hr; this.min = min;
        this.hrHand = new AlarmClockHand(ClockHand.Hand.HR, this.getClass().getResource("alarm_hour_red.png").toString(), 
                                         AlarmClock.HR_HAND_ROT_X, AlarmClock.HR_HAND_ROT_Y);
        this.minHand = new AlarmClockHand(ClockHand.Hand.MIN, this.getClass().getResource("alarm_minute_red.png").toString(), 
                                          AlarmClock.MIN_HAND_ROT_X, AlarmClock.MIN_HAND_ROT_Y);
        this.hrHand.rotate(this.hr, this.min); this.minHand.rotate(this.hr, this.min);

        this.normalPacedSound = new AudioClip(this.getClass().getResource("normal.wav").toString());
        this.fastPacedSound = new AudioClip(this.getClass().getResource("fast.wav").toString());
    }

    // main task
    public void run() {
        this.stopped = false;

        while (super.running) {
            if (this.isTime()) {
                this.ringTask = new Thread(() -> this.ring());
                // this.keyListeningTask = new Thread(() -> this.keyListen());

                ringTask.start(); // keyListeningTask.start();

                while (super.running && !this.stopped);

                this.stopSound();

                break;
            }

            if (!update(Clock.UPDATE_INTERVAL))
                break;
        }

        System.out.println("finish");
        this.hrHand.setImage(this.getClass().getResource("alarm_hour.png").toString(), AlarmClock.HR_HAND_ROT_X, AlarmClock.HR_HAND_ROT_Y, true);
        this.minHand.setImage(this.getClass().getResource("alarm_minute.png").toString(), AlarmClock.MIN_HAND_ROT_X, AlarmClock.MIN_HAND_ROT_Y, true);
    }

    @Override
    public void stop() {
        synchronized (super.LOCK) { 
            super.running = false; 
            this.stopped = true;

            this.stopSound();
        }
    }

    public void stopSound() {
        synchronized (super.LOCK) {
            if (normalPacedSound.isPlaying()) normalPacedSound.stop();
            if (fastPacedSound.isPlaying()) fastPacedSound.stop();
        }
    }

    // check whether it is time to .ring() the alarm
    private boolean isTime() {
        Calendar currentTime = Calendar.getInstance();

        return (hr == currentTime.get(Calendar.HOUR_OF_DAY) && min == currentTime.get(Calendar.MINUTE));
    }

    // ring the alarm bell with fade-in effect when .isTime()
    private void ring() {
        this.snoozed = false;

        synchronized (super.LOCK) {
            normalPacedSound.setCycleCount(8);
            normalPacedSound.play();
            this.keyListen();
        }

        while (super.running && !this.stopped) {
            synchronized (super.LOCK) {
                if (this.snoozed)
                    break;
                else if (!normalPacedSound.isPlaying()) {
                    fastPacedSound.setCycleCount(AudioClip.INDEFINITE);
                    fastPacedSound.play();

                    break;
                }
            }
        }
    }

    // listen keyboard event when .ring()-ing
    private void keyListen() {
        Platform.runLater(() -> super.face.requestFocus());

        super.face.setOnKeyTyped(event -> {
            if (!this.snoozed && (normalPacedSound.isPlaying() || fastPacedSound.isPlaying())) {
                switch (event.getCharacter()) {
                    case " ":
                        snoozeTask = new Thread(() -> this.snooze()); snoozeTask.start();
                        break;
                    case "\r":
                        this.stopped = true;
                        this.stopSound();
                        break;
                }
            }
        });
    }

    // snooze the alarm clock
    // ring again after SNOOZE_PERIOD minutes
    private void snooze() {
        synchronized (super.LOCK) {
            this.snoozed = true;
            this.stopSound();
        }

        // int toMinute = this.hr * 60 + this.min;
        Calendar currentTime = Calendar.getInstance();
        int toMinute = currentTime.get(Calendar.HOUR_OF_DAY) * 60 + currentTime.get(Calendar.MINUTE);
        toMinute += SNOOZE_PERIOD;

        this.hr = (toMinute / 60) % 24;
        this.min = toMinute % 60;

        this.hrHand.rotate(this.hr, this.min);
        this.minHand.rotate(this.hr, this.min);

        while (super.running && !this.stopped) {
            if (this.isTime()) {
                ringTask = new Thread(() -> this.ring()); ringTask.start();
                break;
            }
        }
    }

    // check whether passing an invalid time as argument
    // HH: [00, 24)  MM: [00, 60)
    public static boolean invalidTime(String hhmm) {
        if (hhmm.length() != 4)
            return true;

        int hr = 0; int min = 0;
        try {
            hr = new Integer(hhmm.substring(0, 2));
            min = new Integer(hhmm.substring(2, 4));
        }
        catch (NumberFormatException e) {
            return true;
        }

        return (hr >= 24 || min >= 60);
    }

    public ImageView getAlarmHrHand() { return this.hrHand.getNode(); }
    public ImageView getAlarmMinHand() { return this.minHand.getNode(); }
}
