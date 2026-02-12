package engine.collision;

public class Line implements Geometry {
    private Vector2D start;
    private Vector2D end;
    
    public Line(Vector2D start, Vector2D end) {
        this.start = start;
        this.end = end;
    }
    
    public Vector2D getStart() { return start; }
    public Vector2D getEnd() { return end; }
    
    public void setStart(Vector2D start) { this.start = start; }
    public void setEnd(Vector2D end) { this.end = end; }
    
    public double getLength() {
        return start.distanceTo(end);
    }
    
    @Override
    public boolean intersects(Geometry other) {
        if (other.getType() == GeometryType.CIRCLE) {
            return ((Circle) other).intersectsLine(this);
        } else if (other.getType() == GeometryType.RECTANGLE) {
            return ((Rectangle) other).intersectsLine(this);
        } else if (other.getType() == GeometryType.POLYGON) {
            return intersectsPolygon((Polygon) other);
        } else if (other.getType() == GeometryType.LINE) {
            return intersects((Line) other);
        }
        return false;
    }
    
    boolean intersectsPolygon(Polygon polygon) {
        Vector2D[] vertices = polygon.getVertices();
        for (int i = 0; i < vertices.length; i++) {
            Vector2D v1 = vertices[i];
            Vector2D v2 = vertices[(i + 1) % vertices.length];
            Line edge = new Line(v1, v2);
            if (intersects(edge)) return true;
        }
        
        if (polygon.containsPoint(start) || polygon.containsPoint(end)) return true;
        
        return false;
    }
    
    public boolean intersects(Line other) {
        Vector2D p1 = start;
        Vector2D p2 = end;
        Vector2D p3 = other.start;
        Vector2D p4 = other.end;
        
        double denom = (p1.getX() - p2.getX()) * (p3.getY() - p4.getY()) - 
                      (p1.getY() - p2.getY()) * (p3.getX() - p4.getX());
        
        if (denom == 0) return false;
        
        double t = ((p1.getX() - p3.getX()) * (p3.getY() - p4.getY()) - 
                   (p1.getY() - p3.getY()) * (p3.getX() - p4.getX())) / denom;
        double u = -((p1.getX() - p2.getX()) * (p1.getY() - p3.getY()) - 
                    (p1.getY() - p2.getY()) * (p1.getX() - p3.getX())) / denom;
        
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        double tolerance = 0.0001;
        Vector2D line = end.subtract(start);
        Vector2D toPoint = point.subtract(start);
        
        double cross = line.cross(toPoint);
        if (Math.abs(cross) > tolerance) return false;
        
        double dot = line.dot(toPoint);
        if (dot < -tolerance) return false;
        
        double lenSquared = line.dot(line);
        if (dot > lenSquared + tolerance) return false;
        
        return true;
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return new BoundingBox(
            Math.min(start.getX(), end.getX()),
            Math.min(start.getY(), end.getY()),
            Math.max(start.getX(), end.getX()),
            Math.max(start.getY(), end.getY())
        );
    }
    
    @Override
    public Vector2D getCenter() {
        return new Vector2D(
            (start.getX() + end.getX()) / 2,
            (start.getY() + end.getY()) / 2
        );
    }
    
    @Override
    public void translate(Vector2D offset) {
        start = start.add(offset);
        end = end.add(offset);
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.LINE;
    }
}