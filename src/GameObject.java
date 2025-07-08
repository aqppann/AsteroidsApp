import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;

public abstract class GameObject {
    private final Node view;
    private Point2D velocity = new Point2D(0, 0);
    private boolean alive = true;

    public GameObject(Node view) {
        this.view = view;
    }

    public Node getView() {
        return view;
    }

    public void setVelocity(Point2D velocity) {
        this.velocity = velocity;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void update() {
        view.setTranslateX(view.getTranslateX() + velocity.getX());
        view.setTranslateY(view.getTranslateY() + velocity.getY());
    }

    public void rotateLeft() {
        view.setRotate(view.getRotate() - 5);
        velocity = new Point2D(Math.cos(Math.toRadians(view.getRotate())),
                Math.sin(Math.toRadians(view.getRotate())));
    }

    public void rotateRight() {
        view.setRotate(view.getRotate() + 5);
        velocity = new Point2D(Math.cos(Math.toRadians(view.getRotate())),
                Math.sin(Math.toRadians(view.getRotate())));
    }

    public boolean isColliding(GameObject other) {
        Bounds a = view.getBoundsInParent();
        Bounds b = other.getView().getBoundsInParent();
        return a.intersects(b);
    }

    public boolean isDead() {
        return !alive;
    }

    public void setAlive(boolean value) {
        alive = value;
    }
}

