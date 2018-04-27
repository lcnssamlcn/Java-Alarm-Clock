/**
  Wall Alarm Clock with command line args
  @author lcnssam

  java -cp &lt;CLASSPATH&gt; Main               // normal mode
  java -cp &lt;CLASSPATH&gt; Main --alarm=HHMM  // alarm mode
  */

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.util.Map;

public class Main extends Application {
    enum Mode { NORMAL, ALARM };

    private static Mode clockMode;

    private Scene mainScene;
    private MainWindow window;

    // print help manual for this program
    private static void help(boolean queryNormal, boolean queryAlarm) {
        System.err.println("------------------------------------------------");
        if (queryNormal) {
            System.err.println("Normal Clock");
            System.err.println("    java -cp <CLASSPATH> Main");
        }
        if (queryNormal && queryAlarm) System.err.println();
        if (queryAlarm) {
            System.err.println("Alarm Clock");
            System.err.println("    java -cp <CLASSPATH> Main --alarm=HHMM");
            System.err.println("where HH: [00-23]  MM: [00-59]");
        }
        System.err.println("------------------------------------------------");
    }

    public static Mode getMode() { return Main.clockMode; }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Clock");

        System.out.println((clockMode == Mode.NORMAL ? "Normal Mode" : "Alarm Mode"));
        if (clockMode == Mode.NORMAL)
            window = new MainWindow();
        else  // Mode.ALARM
            window = new MainWindow(this.getParameters().getNamed().get("alarm"));

        mainScene = new Scene(window);
        mainScene.setFill(Color.PALEGOLDENROD);
        stage.setScene(mainScene);

        stage.setResizable(false);

        stage.show();

        window.runClock();

        stage.setOnCloseRequest(e -> window.interruptClock());
    }

    // analyze command line args
    @Override
    public void init() throws Exception {
        Application.Parameters args = this.getParameters();

        if (args.getRaw().isEmpty()) {
            this.clockMode = Mode.NORMAL;
            return;
        }

        if (args.getRaw().get(0).equals("--help")) {
            Main.help(true, true);
            System.exit(0);
        }

        Map<String, String> namedArgsMap = args.getNamed();
        if (namedArgsMap.isEmpty() || namedArgsMap.get("alarm") == null) {
            System.err.println(this.getClass().getName() + ": Invalid argument");
            Main.help(false, true);
            System.exit(-1);
        }

        if (AlarmClock.invalidTime(namedArgsMap.get("alarm"))) {
            System.err.println(this.getClass().getName() + ": Invalid time format");
            Main.help(false, true);
            System.exit(-1);
        }

        this.clockMode = Mode.ALARM;
    }

    public static void main(String args[]) {
        if (args.length > 1) {
            System.err.println(Main.class.getName() + ": Invalid argument");
            Main.help(true, true);
            System.exit(-1);
        }

        Application.launch(args);
    }
}
