// import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

import java.util.Calendar;

public class ClockHand {
    enum Hand { HR, MIN, SEC };

    protected final Hand HAND;  // position of hand (HOUR, MINUTE or SECOND)
    protected ImageView image;
    protected double pivotX;     // x-coordinate of clock hand rotation point (scaled)
    protected double pivotY;     // y-coordinate of clock hand rotation point (scaled)
    protected double angle;      // rotation angle in degree

    public ClockHand(final Hand HAND, String imageURL, double scaledPivotX, double scaledPivotY) {
        this.HAND = HAND;

        this.setImage(imageURL, scaledPivotX, scaledPivotY, false);
    }

    // rotate clock hands according to current time
    public void rotate() {
        double newAngle = 0;
        Calendar currentTime = Calendar.getInstance();  // using default time zone
        switch (HAND) {
            case HR:
                newAngle = new Integer(currentTime.get(Calendar.HOUR)).doubleValue() / 12 * 360 + 
                           new Integer(currentTime.get(Calendar.MINUTE)).doubleValue() / 60 * 360 / 12;
                // System.out.println("HR: " + newAngle);
                break;

            case MIN:
                newAngle = new Integer(currentTime.get(Calendar.MINUTE)).doubleValue() / 60 * 360;
                // System.out.println("MIN: " + newAngle);
                break;

            case SEC:
                newAngle = new Integer(currentTime.get(Calendar.SECOND)).doubleValue() / 60 * 360;
                // System.out.println("SEC: " + newAngle);
                break;
        }

        if (newAngle - angle != 0) {
            // final double NEW_ANGLE = newAngle;
            // Platform.runLater(() -> image.getTransforms().add(new Rotate(NEW_ANGLE - angle, pivotX, pivotY)));
            image.getTransforms().add(new Rotate(newAngle - angle, pivotX, pivotY));
            angle = newAngle;
        }
    }

    // setup clock hand image, pivot [with previous rotation]
    public void setImage(String imageURL, double scaledPivotX, double scaledPivotY, boolean withPrevRotation) {
        this.image = new ImageView(imageURL);
        this.image.setFitWidth(image.getImage().getWidth() * Clock.SCALING_FACTOR);
        this.image.setFitHeight(image.getImage().getHeight() * Clock.SCALING_FACTOR);
        this.image.setPreserveRatio(false);

        this.pivotX = scaledPivotX; this.pivotY = scaledPivotY;

        this.image.setLayoutX(MainWindow.WINDOW_WIDTH / 2 - pivotX); this.image.setLayoutY(MainWindow.WINDOW_HEIGHT / 2 - pivotY);

        if (withPrevRotation)
            this.image.getTransforms().add(new Rotate(angle, pivotX, pivotY));
    }

    public ImageView getNode() { return image; }
}
