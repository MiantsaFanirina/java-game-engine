package engine.collision;

public class Circle implements Geometry {
    private Vector2D center;
    private double radius;
    
    public Circle(Vector2D center, double radius) {
        this.center = center;
        this.radius = radius;
    }
    
    public Vector2D getCenter() { return center; }
    public double getRadius() { return radius; }
    
    public void setCenter(Vector2D center) { this.center = center; }
    public void setRadius(double radius) { this.radius = radius; }
    
    @Override
    public boolean intersects(Geometry other) {
        if (other.getType() == GeometryType.CIRCLE) {
            return intersectsCircle((Circle) other);
        } else if (other.getType() == GeometryType.RECTANGLE) {
            return intersectsRectangle((Rectangle) other);
        } else if (other.getType() == GeometryType.POLYGON) {
            return intersectsPolygon((Polygon) other);
        } else if (other.getType() == GeometryType.LINE) {
            return intersectsLine((Line) other);
        }
        return false;
    }
    
    boolean intersectsCircle(Circle other) {
        double distance = center.distanceTo(other.center);
        return distance <= radius + other.radius;
    }
    
    boolean intersectsRectangle(Rectangle rectangle) {
        Vector2D rectCenter = rectangle.getCenter();
        double rectWidth = rectangle.getWidth();
        double rectHeight = rectangle.getHeight();
        
        double closestX = Math.max(rectCenter.getX() - rectWidth/2, 
                                  Math.min(center.getX(), rectCenter.getX() + rectWidth/2));
        double closestY = Math.max(rectCenter.getY() - rectHeight/2, 
                                  Math.min(center.getY(), rectCenter.getY() + rectHeight/2));
        
        double distanceX = center.getX() - closestX;
        double distanceY = center.getY() - closestY;
        
        return (distanceX * distanceX + distanceY * distanceY) <= (radius * radius);
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
        
        Vector2D d = p2.subtract(p1);
        Vector2D f = p1.subtract(center);
        
        double a = d.dot(d);
        double b = 2 * f.dot(d);
        double c = f.dot(f) - radius * radius;
        
        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return false;
        
        discriminant = Math.sqrt(discriminant);
        double t1 = (-b - discriminant) / (2 * a);
        double t2 = (-b + discriminant) / (2 * a);
        
        return (t1 >= 0 && t1 <= 1) || (t2 >= 0 && t2 <= 1) || (t1 < 0 && t2 > 1);
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        return center.distanceTo(point) <= radius;
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(
            center.getX() - radius,
            center.getY() - radius,
            center.getX() + radius,
            center.getY() + radius
        );
    }
    
    @Override
    public void translate(Vector2D offset) {
        center = center.add(offset);
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.CIRCLE;
    }
}