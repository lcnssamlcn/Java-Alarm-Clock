import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Insets;

/* Scene Graph:
                             Pane
                          MainWindow
                              |
         --------------------------------------------------=====================================
         |                   |             |              |                |                   |
       StackPane         ImageView     ImageView      ImageView        ImageView           ImageView
   clockFaceContainer  Clock.hrHand  Clock.minHand  Clock.secHand  AlarmClock.hrHand  AlarmClock.minHand
         |
     ImageView
     clockFace
 */

public class MainWindow extends Pane {
    public static final double MARGIN = 20;
    public static final double WINDOW_WIDTH = Clock.FACE_WIDTH + MARGIN * 2;
    public static final double WINDOW_HEIGHT = Clock.FACE_HEIGHT + MARGIN * 2;

    private StackPane clockFaceContainer;

    private Clock clock;

    private Thread clockMainThread;
    private Thread clockAlarmThread;

    public MainWindow() {
        this.clock = new Clock();
        this.init();
    }

    public MainWindow(String hhmm) {
        this.clock = new AlarmClock(new Integer(hhmm.substring(0, 2)), new Integer(hhmm.substring(2, 4)));
        this.init();
        this.getChildren().addAll(((AlarmClock) clock).getAlarmHrHand(), ((AlarmClock) clock).getAlarmMinHand());
    }

    // init GUI components and build a scene graph
    private void init() {
        this.setPrefSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        clockFaceContainer = new StackPane();
        clockFaceContainer.setPadding(new Insets(MARGIN));
        clockFaceContainer.getChildren().add(clock.getFace());

        this.getChildren().add(clockFaceContainer);

        this.getChildren().addAll(clock.getHrHand(), clock.getMinHand(), clock.getSecHand());
    }

    public void runClock() {
        clockMainThread = new Thread(() -> clock.ticktock());
        clockMainThread.start();

        if (Main.getMode() == Main.Mode.ALARM) {
            clockAlarmThread = new Thread(() -> ((AlarmClock) clock).run());
            clockAlarmThread.start();
        }
    }

    public void interruptClock() {
        clock.stop();
    }
}
