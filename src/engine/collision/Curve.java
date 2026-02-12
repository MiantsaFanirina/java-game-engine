package engine.collision;

import java.util.List;

public class Curve implements Geometry {
    private CurveMath.BezierCurve bezierCurve;
    private Vector2D center;
    private List<Vector2D> samplePoints;
    private int resolution;
    
    public Curve(Vector2D[] controlPoints) {
        this.bezierCurve = new CurveMath.BezierCurve(controlPoints);
        this.samplePoints = bezierCurve.samplePoints(50);
        this.resolution = 50;
        this.center = calculateCenter();
    }
    
    public Curve(Vector2D[] controlPoints, int resolution) {
        this.bezierCurve = new CurveMath.BezierCurve(controlPoints);
        this.resolution = Math.max(10, resolution);
        this.samplePoints = bezierCurve.samplePoints(this.resolution);
        this.center = calculateCenter();
    }
    
    private Vector2D calculateCenter() {
        double sumX = 0, sumY = 0;
        for (Vector2D point : samplePoints) {
            sumX += point.getX();
            sumY += point.getY();
        }
        return new Vector2D(sumX / samplePoints.size(), sumY / samplePoints.size());
    }
    
    public Vector2D getPoint(double t) {
        return bezierCurve.getPoint(t);
    }
    
    public Vector2D getTangent(double t) {
        return bezierCurve.getTangent(t);
    }
    
    public double getCurvature(double t) {
        return bezierCurve.getCurvature(t);
    }
    
    public List<Vector2D> getSamplePoints() {
        return samplePoints;
    }
    
    public int getResolution() { return resolution; }
    
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
    
    private boolean intersectsCircle(Circle circle) {
        // Sample the curve and check for intersection
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment = new Line(p1, p2);
            if (circle.intersectsLine(segment)) {
                return true;
            }
        }
        
        // Also check if any control points are inside the circle
        for (Vector2D point : samplePoints) {
            if (circle.containsPoint(point)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsRectangle(Rectangle rectangle) {
        // Sample the curve and check each segment against rectangle
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment = new Line(p1, p2);
            if (rectangle.intersectsLine(segment)) {
                return true;
            }
        }
        
        // Check if any sample points are inside rectangle
        for (Vector2D point : samplePoints) {
            if (rectangle.containsPoint(point)) {
                return true;
            }
        }
        
        return false;
    }
    
    boolean intersectsPolygon(Polygon polygon) {
        // Sample the curve and check each segment against polygon edges
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment = new Line(p1, p2);
            if (polygon.intersectsLine(segment)) {
                return true;
            }
        }
        
        // Check if any sample points are inside polygon
        for (Vector2D point : samplePoints) {
            if (polygon.containsPoint(point)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsLine(Line line) {
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment = new Line(p1, p2);
            if (segment.intersects(line)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean intersectsCurve(Curve other) {
        // Sample both curves and check for intersections
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment1 = new Line(p1, p2);
            
            List<Vector2D> otherPoints = other.getSamplePoints();
            for (int j = 0; j < otherPoints.size() - 1; j++) {
                Vector2D p3 = otherPoints.get(j);
                Vector2D p4 = otherPoints.get(j + 1);
                Line segment2 = new Line(p3, p4);
                
                if (segment1.intersects(segment2)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    boolean intersectsEllipse(Ellipse ellipse) {
        // Sample the curve and check each point against ellipse
        for (Vector2D point : samplePoints) {
            if (ellipse.containsPoint(point)) {
                return true;
            }
        }
        
        // Check line segments
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            Line segment = new Line(p1, p2);
            if (ellipse.intersectsLine(segment)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        // For a curve, we check if the point is very close to the curve
        double threshold = 2.0; // pixels/tolerance
        for (int i = 0; i < samplePoints.size() - 1; i++) {
            Vector2D p1 = samplePoints.get(i);
            Vector2D p2 = samplePoints.get(i + 1);
            
            double distance = pointToLineDistance(point, p1, p2);
            if (distance <= threshold) {
                return true;
            }
        }
        return false;
    }
    
    private double pointToLineDistance(Vector2D point, Vector2D lineStart, Vector2D lineEnd) {
        Vector2D line = lineEnd.subtract(lineStart);
        double lineLength = line.magnitude();
        if (lineLength == 0) return point.distanceTo(lineStart);
        
        Vector2D pointToStart = point.subtract(lineStart);
        double t = Math.max(0, Math.min(1, pointToStart.dot(line) / (lineLength * lineLength)));
        Vector2D projection = lineStart.add(line.multiply(t));
        
        return point.distanceTo(projection);
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        return bezierCurve.getBoundingBox();
    }
    
    @Override
    public Vector2D getCenter() {
        return center;
    }
    
    @Override
    public void translate(Vector2D offset) {
        center = center.add(offset);
        // Update bezier curve control points (would need to modify BezierCurve class)
        // For now, we'll recalculate the curve points
        samplePoints = bezierCurve.samplePoints(resolution);
        center = calculateCenter();
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.CURVE;
    }
}