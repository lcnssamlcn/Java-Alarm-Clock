// import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

public class AlarmClockHand extends ClockHand {
    public AlarmClockHand(final Hand HAND, String imageURL, double scaledPivotX, double scaledPivotY) {
        super(HAND, imageURL, scaledPivotX, scaledPivotY);
    }

    // rotate clock hands manually by time
    // used by alarm clock
    // hr: [0, 24)  min: [0, 60)
    public void rotate(final int hr, final int min) {
        double newAngle = 0;
        switch (HAND) {
            case HR:
                newAngle = new Integer(hr % 12).doubleValue() / 12 * 360 + 
                           new Integer(min).doubleValue() / 60 * 360 / 12;
                break;
            case MIN:
                newAngle = new Integer(min).doubleValue() / 60 * 360;
                break;
            case SEC:
                throw new RuntimeException("Rotation of second hand is unavailable for alarm clock");
        }

        if (newAngle - angle != 0) {
            // final double NEW_ANGLE = newAngle;
            // Platform.runLater(() -> image.getTransforms().add(new Rotate(NEW_ANGLE - angle, pivotX, pivotY)));
            image.getTransforms().add(new Rotate(newAngle - angle, pivotX, pivotY));
            angle = newAngle;
        }
    }
}
