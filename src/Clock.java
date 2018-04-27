// import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class Clock {
    protected final Object LOCK = new Object();

    protected ImageView face;
    private ClockHand hrHand;
    private ClockHand minHand;
    private ClockHand secHand;

    // scale all clock components by SCALING_FACTOR
    public static final double SCALING_FACTOR = 1.5;

    public static final double FACE_WIDTH = 350 * SCALING_FACTOR;
    public static final double FACE_HEIGHT = 350 * SCALING_FACTOR;

    private static final double HR_HAND_ROT_X  = 14 * SCALING_FACTOR;
    private static final double HR_HAND_ROT_Y  = 84 * SCALING_FACTOR;
    private static final double MIN_HAND_ROT_X = 11 * SCALING_FACTOR;
    private static final double MIN_HAND_ROT_Y = 146 * SCALING_FACTOR;
    private static final double SEC_HAND_ROT_X = 10 * SCALING_FACTOR;
    private static final double SEC_HAND_ROT_Y = 160 * SCALING_FACTOR;

    protected static final long UPDATE_INTERVAL = 300L;  // in ms

    protected volatile boolean running = false;

    // default clock outlook
    public Clock() {
        this.face = new ImageView(this.getClass().getResource("face.gif").toString());
        this.face.setFitWidth(FACE_WIDTH);
        this.face.setFitHeight(FACE_HEIGHT);
        this.face.setPreserveRatio(false);

        this.hrHand = new ClockHand(ClockHand.Hand.HR, this.getClass().getResource("hour.png").toString(), 
                                    Clock.HR_HAND_ROT_X, Clock.HR_HAND_ROT_Y);
        this.minHand = new ClockHand(ClockHand.Hand.MIN, this.getClass().getResource("minute.png").toString(), 
                                     Clock.MIN_HAND_ROT_X, Clock.MIN_HAND_ROT_Y);
        this.secHand = new ClockHand(ClockHand.Hand.SEC, this.getClass().getResource("second.png").toString(), 
                                     Clock.SEC_HAND_ROT_X, Clock.SEC_HAND_ROT_Y);
    }

    // main task
    public void ticktock() {
        this.running = true;

        while (running) {
            synchronized (this.LOCK) {
                hrHand.rotate(); minHand.rotate(); secHand.rotate();
            }

            if (!this.update(Clock.UPDATE_INTERVAL))
                break;
        }
    }

    public void stop() { synchronized (this.LOCK) { running = false; } }

    // update the Clock between interval, in ms
    protected boolean update(long interval) {
        try { 
            Thread.sleep(interval); 
            return true; 
        }
        catch (InterruptedException e) { 
            e.printStackTrace(); 
            return false; 
        }
    }

    public ImageView getFace() { return face; }
    public ImageView getHrHand() { return hrHand.getNode(); }
    public ImageView getMinHand() { return minHand.getNode(); }
    public ImageView getSecHand() { return secHand.getNode(); }
}
