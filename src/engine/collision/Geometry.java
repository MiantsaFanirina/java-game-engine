package engine.collision;

public interface Geometry {
    boolean intersects(Geometry other);
    boolean containsPoint(Vector2D point);
    BoundingBox getBoundingBox();
    Vector2D getCenter();
    void translate(Vector2D offset);
    GeometryType getType();
}