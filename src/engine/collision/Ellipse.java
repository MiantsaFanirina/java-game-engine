package engine.collision;

import java.util.List;

public class Ellipse implements Geometry {
    private CurveMath.Ellipse ellipse;
    private List<Vector2D> samplePoints;
    private int resolution;
    
    public Ellipse(Vector2D center, double radiusX, double radiusY) {
        this.ellipse = new CurveMath.Ellipse(center, radiusX, radiusY);
        this.resolution = 64;
        this.samplePoints = ellipse.samplePoints(this.resolution);
    }
    
    public Ellipse(Vector2D center, double radiusX, double radiusY, double rotation) {
        this.ellipse = new CurveMath.Ellipse(center, radiusX, radiusY, rotation);
        this.resolution = 64;
        this.samplePoints = ellipse.samplePoints(this.resolution);
    }
    
    public Ellipse(Vector2D center, double radiusX, double radiusY, double rotation, int resolution) {
        this.ellipse = new CurveMath.Ellipse(center, radiusX, radiusY, rotation);
        this.resolution = Math.max(8, resolution);
        this.samplePoints = ellipse.samplePoints(this.resolution);
    }
    
    public Vector2D getPoint(double angle) {
        return ellipse.getPoint(angle);
    }
    
    public Vector2D getNormal(double angle) {
        return ellipse.getNormal(angle);
    }
    
    public double getCurvature(double angle) {
        return ellipse.getCurvature(angle);
    }
    
    public List<Vector2D> getSamplePoints() {
        return samplePoints;
    }
    
    public Vector2D getCenter() { return ellipse.getCenter(); }
    public double getRadiusX() { return ellipse.getRadiusX(); }
    public double getRadiusY() { return ellipse.getRadiusY(); }
    public double getRotation() { return ellipse.getRotation(); }
    
    public void setCenter(Vector2D center) { 
        ellipse.setCenter(center);
        samplePoints = ellipse.samplePoints(resolution);
    }
    
    public void setRadiusX(double radiusX) { 
        ellipse.setRadiusX(radiusX);
        samplePoints = ellipse.samplePoints(resolution);
    }
    
    public void setRadiusY(double radiusY) { 
        ellipse.setRadiusY(radiusY);
        samplePoints = ellipse.samplePoints(resolution);
    }
    
    public void setRotation(double rotation) { 
        ellipse.setRotation(rotation);
        samplePoints = ellipse.samplePoints(resolution);
    }
    
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
        } else if (other.getType() == GeometryType.CURVE) {
            return intersectsCurve((Curve) other);
        } else if (other.getType() == GeometryType.ELLIPSE) {
            return intersectsEllipse((Ellipse) other);
        }
        return false;
    }
    
    private boolean intersectsCurve(Curve curve) {
        // Use curve's ellipse intersection method
        return curve.intersectsEllipse(this);
    }
    
    private boolean intersectsCircle(Circle circle) {
        // Transform circle to ellipse's local space
        Vector2D circleCenter = circle.getCenter();
        Vector2D ellipseCenter = ellipse.getCenter();
        Vector2D localCirclePos = circleCenter.subtract(ellipseCenter);
        
        // Rotate by negative ellipse rotation to align ellipse with axes
        double cosRot = Math.cos(-ellipse.getRotation());
        double sinRot = Math.sin(-ellipse.getRotation());
        double localX = localCirclePos.getX() * cosRot - localCirclePos.getY() * sinRot;
        double localY = localCirclePos.getX() * sinRot + localCirclePos.getY() * cosRot;
        
        // Scale the space to make ellipse a circle
        double scaledCircleRadiusX = circle.getRadius() / ellipse.getRadiusX();
        double scaledCircleRadiusY = circle.getRadius() / ellipse.getRadiusY();
        double maxScaledRadius = Math.max(scaledCircleRadiusX, scaledCircleRadiusY);
        
        // Check distance from center
        double distance = Math.sqrt(localX * localX / (ellipse.getRadiusX() * ellipse.getRadiusX()) + 
                                  localY * localY / (ellipse.getRadiusY() * ellipse.getRadiusY()));
        
        return distance <= 1 + maxScaledRadius;
    }
    
    private boolean intersectsRectangle(Rectangle rectangle) {
        // Sample rectangle corners and edges
        Vector2D[] corners = {
            new Vector2D(rectangle.getLeft(), rectangle.getTop()),
            new Vector2D(rectangle.getRight(), rectangle.getTop()),
            new Vector2D(rectangle.getRight(), rectangle.getBottom()),
            new Vector2D(rectangle.getLeft(), rectangle.getBottom())
        };
        
        // Check if any rectangle corner is inside ellipse
        for (Vector2D corner : corners) {
            if (containsPoint(corner)) {
                return true;
            }
        }
        
        // Check if ellipse center is inside rectangle
        if (rectangle.containsPoint(ellipse.getCenter())) {
            return true;
        }
        
        // Check ellipse edges against rectangle edges
        for (int i = 0; i < samplePoints.size(); i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get((i + 1) % samplePoints.size());
            Line ellipseEdge = new Line(p1, p2);
            
            if (rectangle.intersectsLine(ellipseEdge)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsPolygon(Polygon polygon) {
        // Check if any polygon vertex is inside ellipse
        for (Vector2D vertex : polygon.getVertices()) {
            if (containsPoint(vertex)) {
                return true;
            }
        }
        
        // Check if ellipse center is inside polygon
        if (polygon.containsPoint(ellipse.getCenter())) {
            return true;
        }
        
        // Check ellipse edges against polygon edges
        for (int i = 0; i < samplePoints.size(); i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get((i + 1) % samplePoints.size());
            Line ellipseEdge = new Line(p1, p2);
            
            if (polygon.intersectsLine(ellipseEdge)) {
                return true;
            }
        }
        
        return false;
    }
    
    public boolean intersectsLine(Line line) {
        // Check if line endpoints are inside ellipse
        if (containsPoint(line.getStart()) || containsPoint(line.getEnd())) {
            return true;
        }
        
        // Check line against ellipse edges
        for (int i = 0; i < samplePoints.size(); i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get((i + 1) % samplePoints.size());
            Line ellipseEdge = new Line(p1, p2);
            
            if (ellipseEdge.intersects(line)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsEllipse(Ellipse other) {
        // Approximate intersection by sampling both ellipses
        for (Vector2D point : samplePoints) {
            if (other.containsPoint(point)) {
                return true;
            }
        }
        
        for (Vector2D point : other.getSamplePoints()) {
            if (this.containsPoint(point)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        // Transform point to ellipse's local space
        Vector2D localPos = point.subtract(ellipse.getCenter());
        
        // Rotate by negative ellipse rotation to align ellipse with axes
        double cosRot = Math.cos(-ellipse.getRotation());
        double sinRot = Math.sin(-ellipse.getRotation());
        double localX = localPos.getX() * cosRot - localPos.getY() * sinRot;
        double localY = localPos.getX() * sinRot + localPos.getY() * cosRot;
        
        // Check if point is inside ellipse using ellipse equation
        double value = (localX * localX) / (ellipse.getRadiusX() * ellipse.getRadiusX()) +
                      (localY * localY) / (ellipse.getRadiusY() * ellipse.getRadiusY());
        
        return value <= 1.0;
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return ellipse.getBoundingBox();
    }
    
    @Override
    public void translate(Vector2D offset) {
        setCenter(ellipse.getCenter().add(offset));
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.ELLIPSE;
    }
}