package engine.collision;

public class Polygon implements Geometry {
    private Vector2D[] vertices;
    private Vector2D center;
    
    public Polygon(Vector2D[] vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");
        }
        this.vertices = vertices.clone();
        this.center = calculateCenter();
    }
    
    public Vector2D[] getVertices() { return vertices.clone(); }
    
    public void setVertices(Vector2D[] vertices) {
        if (vertices.length < 3) {
            throw new IllegalArgumentException("Polygon must have at least 3 vertices");
        }
        this.vertices = vertices.clone();
        this.center = calculateCenter();
    }
    
    private Vector2D calculateCenter() {
        double sumX = 0, sumY = 0;
        for (Vector2D vertex : vertices) {
            sumX += vertex.getX();
            sumY += vertex.getY();
        }
        return new Vector2D(sumX / vertices.length, sumY / vertices.length);
    }
    
    @Override
    public boolean intersects(Geometry other) {
        if (other.getType() == GeometryType.CIRCLE) {
            return ((Circle) other).intersectsPolygon(this);
        } else if (other.getType() == GeometryType.RECTANGLE) {
            return ((Rectangle) other).intersectsPolygon(this);
        } else if (other.getType() == GeometryType.POLYGON) {
            return intersectsPolygon((Polygon) other);
        } else if (other.getType() == GeometryType.LINE) {
            return intersectsLine((Line) other);
        }
        return false;
    }
    
    boolean intersectsPolygon(Polygon other) {
        Vector2D[] otherVertices = other.getVertices();
        
        for (Vector2D vertex : otherVertices) {
            if (containsPoint(vertex)) return true;
        }
        
        for (Vector2D vertex : vertices) {
            if (other.containsPoint(vertex)) return true;
        }
        
        for (int i = 0; i < vertices.length; i++) {
            Vector2D v1 = vertices[i];
            Vector2D v2 = vertices[(i + 1) % vertices.length];
            Line edge1 = new Line(v1, v2);
            
            for (int j = 0; j < otherVertices.length; j++) {
                Vector2D v3 = otherVertices[j];
                Vector2D v4 = otherVertices[(j + 1) % otherVertices.length];
                Line edge2 = new Line(v3, v4);
                
                if (edge1.intersects(edge2)) return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        boolean inside = false;
        int n = vertices.length;
        
        for (int i = 0, j = n - 1; i < n; j = i++) {
            Vector2D vi = vertices[i];
            Vector2D vj = vertices[j];
            
            if (((vi.getY() > point.getY()) != (vj.getY() > point.getY())) &&
                (point.getX() < (vj.getX() - vi.getX()) * (point.getY() - vi.getY()) / 
                 (vj.getY() - vi.getY()) + vi.getX())) {
                inside = !inside;
            }
        }
        
        return inside;
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        
        for (Vector2D vertex : vertices) {
            minX = Math.min(minX, vertex.getX());
            minY = Math.min(minY, vertex.getY());
            maxX = Math.max(maxX, vertex.getX());
            maxY = Math.max(maxY, vertex.getY());
        }
        
        return new BoundingBox(minX, minY, maxX, maxY);
    }
    
    @Override
    public Vector2D getCenter() {
        return center;
    }
    
    @Override
    public void translate(Vector2D offset) {
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = vertices[i].add(offset);
        }
        center = center.add(offset);
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.POLYGON;
    }
    
    public static Polygon createTriangle(Vector2D p1, Vector2D p2, Vector2D p3) {
        return new Polygon(new Vector2D[]{p1, p2, p3});
    }
    
    public static Polygon createRegularPolygon(Vector2D center, double radius, int sides) {
        if (sides < 3) {
            throw new IllegalArgumentException("Regular polygon must have at least 3 sides");
        }
        
        Vector2D[] vertices = new Vector2D[sides];
        double angleStep = 2 * Math.PI / sides;
        
        for (int i = 0; i < sides; i++) {
            double angle = i * angleStep;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            vertices[i] = new Vector2D(x, y);
        }
        
        return new Polygon(vertices);
    }
    
    public boolean intersectsLine(Line line) {
        // Check if line endpoints are inside polygon
        if (containsPoint(line.getStart()) || containsPoint(line.getEnd())) {
            return true;
        }
        
        // Check if line intersects any polygon edge
        for (int i = 0; i < vertices.length; i++) {
            Vector2D p1 = vertices[i];
            Vector2D p2 = vertices[(i + 1) % vertices.length];
            Line edge = new Line(p1, p2);
            
            if (edge.intersects(line)) {
                return true;
            }
        }
        
        return false;
    }
}