import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy extends GameObject {
    public Enemy() {
        super(new Circle(15, Color.RED));
        setVelocity(new javafx.geometry.Point2D(Math.random() - 0.5, Math.random() - 0.5).normalize().multiply(1.5)); // випадковий напрямок руху
    }
}
