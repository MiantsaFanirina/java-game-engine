package engine.collision;

public class Rectangle implements Geometry {
    private Vector2D center;
    private double width;
    private double height;
    
    public Rectangle(Vector2D center, double width, double height) {
        this.center = center;
        this.width = width;
        this.height = height;
    }
    
    public Vector2D getCenter() { return center; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    
    public void setCenter(Vector2D center) { this.center = center; }
    public void setWidth(double width) { this.width = width; }
    public void setHeight(double height) { this.height = height; }
    
    public double getLeft() { return center.getX() - width / 2; }
    public double getRight() { return center.getX() + width / 2; }
    public double getTop() { return center.getY() - height / 2; }
    public double getBottom() { return center.getY() + height / 2; }
    
    @Override
    public boolean intersects(Geometry other) {
        if (other.getType() == GeometryType.CIRCLE) {
            return ((Circle) other).intersectsRectangle(this);
        } else if (other.getType() == GeometryType.RECTANGLE) {
            return intersectsRectangle((Rectangle) other);
        } else if (other.getType() == GeometryType.POLYGON) {
            return intersectsPolygon((Polygon) other);
        } else if (other.getType() == GeometryType.LINE) {
            return intersectsLine((Line) other);
        }
        return false;
    }
    
    private boolean intersectsRectangle(Rectangle other) {
        return !(getRight() < other.getLeft() || getLeft() > other.getRight() ||
                 getBottom() < other.getTop() || getTop() > other.getBottom());
    }
    
    boolean intersectsPolygon(Polygon polygon) {
        if (polygon.containsPoint(center)) return true;
        
        Vector2D[] vertices = polygon.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            Vector2D v1 = vertices[i];
            Vector2D v2 = vertices[(i + 1) % vertices.length];
            Line edge = new Line(v1, v2);
            if (intersectsLine(edge)) return true;
        }
        
        return false;
    }
    
    boolean intersectsLine(Line line) {
        Vector2D p1 = line.getStart();
        Vector2D p2 = line.getEnd();
        
        if (containsPoint(p1) || containsPoint(p2)) return true;
        
        Vector2D[] corners = getCorners();
        for (int i = 0; i < corners.length; i++) {
            Vector2D c1 = corners[i];
            Vector2D c2 = corners[(i + 1) % corners.length];
            Line edge = new Line(c1, c2);
            if (line.intersects(edge)) return true;
        }
        
        return false;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        return point.getX() >= getLeft() && point.getX() <= getRight() &&
               point.getY() >= getTop() && point.getY() <= getBottom();
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(getLeft(), getTop(), getRight(), getBottom());
    }
    
    @Override
    public void translate(Vector2D offset) {
        center = center.add(offset);
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.RECTANGLE;
    }
    
    private Vector2D[] getCorners() {
        double halfWidth = width / 2;
        double halfHeight = height / 2;
        
        return new Vector2D[] {
            new Vector2D(center.getX() - halfWidth, center.getY() - halfHeight),
            new Vector2D(center.getX() + halfWidth, center.getY() - halfHeight),
            new Vector2D(center.getX() + halfWidth, center.getY() + halfHeight),
            new Vector2D(center.getX() - halfWidth, center.getY() + halfHeight)
        };
    }
}