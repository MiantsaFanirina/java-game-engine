package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class FreeForm implements Geometry {
    private List<Vector2D> points;
    private List<Triangle> triangles;
    private Vector2D center;
    private BoundingBox boundingBox;
    private boolean isSolid; // true for solid fill, false for outline only
    
    public FreeForm(List<Vector2D> points) {
        if (points.size() < 3) {
            throw new IllegalArgumentException("FreeForm needs at least 3 points");
        }
        this.points = new ArrayList<>(points);
        this.isSolid = true;
        this.triangles = triangulate();
        this.center = calculateCenter();
        this.boundingBox = calculateBoundingBox();
    }
    
    public FreeForm(List<Vector2D> points, boolean isSolid) {
        if (points.size() < 3) {
            throw new IllegalArgumentException("FreeForm needs at least 3 points");
        }
        this.points = new ArrayList<>(points);
        this.isSolid = isSolid;
        this.triangles = isSolid ? triangulate() : new ArrayList<>();
        this.center = calculateCenter();
        this.boundingBox = calculateBoundingBox();
    }
    
    private static class Triangle {
        Vector2D p1, p2, p3;
        
        Triangle(Vector2D p1, Vector2D p2, Vector2D p3) {
            this.p1 = p1;
            this.p2 = p2;
            this.p3 = p3;
        }
        
        boolean containsPoint(Vector2D point) {
            // Barycentric coordinates method
            Vector2D v0 = p3.subtract(p1);
            Vector2D v1 = p2.subtract(p1);
            Vector2D v2 = point.subtract(p1);
            
            double dot00 = v0.dot(v0);
            double dot01 = v0.dot(v1);
            double dot02 = v0.dot(v2);
            double dot11 = v1.dot(v1);
            double dot12 = v1.dot(v2);
            
            double invDenominator = 1.0 / (dot00 * dot11 - dot01 * dot01);
            double u = (dot11 * dot02 - dot01 * dot12) * invDenominator;
            double v = (dot00 * dot12 - dot01 * dot02) * invDenominator;
            
            return (u >= 0) && (v >= 0) && (u + v <= 1);
        }
        
        Vector2D getCenter() {
            return new Vector2D(
                (p1.getX() + p2.getX() + p3.getX()) / 3,
                (p1.getY() + p2.getY() + p3.getY()) / 3
            );
        }
    }
    
    private List<Triangle> triangulate() {
        List<Triangle> triangles = new ArrayList<>();
        
        if (points.size() < 3) return triangles;
        
        // Simple ear clipping algorithm for convex polygons
        List<Vector2D> remainingPoints = new ArrayList<>(points);
        
        while (remainingPoints.size() > 3) {
            boolean earFound = false;
            
            for (int i = 0; i < remainingPoints.size() && !earFound; i++) {
                int prev = (i - 1 + remainingPoints.size()) % remainingPoints.size();
                int next = (i + 1) % remainingPoints.size();
                
                Vector2D a = remainingPoints.get(prev);
                Vector2D b = remainingPoints.get(i);
                Vector2D c = remainingPoints.get(next);
                
                if (isEar(a, b, c, remainingPoints)) {
                    triangles.add(new Triangle(a, b, c));
                    remainingPoints.remove(i);
                    earFound = true;
                }
            }
            
            if (!earFound) {
                // Fallback: fan triangulation from center
                Vector2D center = calculateCentroid(remainingPoints);
                for (int i = 0; i < remainingPoints.size(); i++) {
                    int next = (i + 1) % remainingPoints.size();
                    triangles.add(new Triangle(center, remainingPoints.get(i), remainingPoints.get(next)));
                }
                break;
            }
        }
        
        if (remainingPoints.size() == 3) {
            triangles.add(new Triangle(remainingPoints.get(0), remainingPoints.get(1), remainingPoints.get(2)));
        }
        
        return triangles;
    }
    
    private boolean isEar(Vector2D a, Vector2D b, Vector2D c, List<Vector2D> polygon) {
        // Check if triangle is clockwise (for proper orientation)
        double crossProduct = (c.getX() - a.getX()) * (b.getY() - a.getY()) - 
                              (c.getY() - a.getY()) * (b.getX() - a.getX());
        
        Triangle triangle = new Triangle(a, b, c);
        
        // Check if any other point is inside this triangle
        for (Vector2D point : polygon) {
            if (point != a && point != b && point != c && triangle.containsPoint(point)) {
                return false;
            }
        }
        
        return true;
    }
    
    private Vector2D calculateCentroid(List<Vector2D> pointList) {
        double sumX = 0, sumY = 0;
        for (Vector2D point : pointList) {
            sumX += point.getX();
            sumY += point.getY();
        }
        return new Vector2D(sumX / pointList.size(), sumY / pointList.size());
    }
    
    private Vector2D calculateCenter() {
        return calculateCentroid(points);
    }
    
    private BoundingBox calculateBoundingBox() {
        if (points.isEmpty()) {
            return new BoundingBox(0, 0, 0, 0);
        }
        
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        
        for (Vector2D point : points) {
            minX = Math.min(minX, point.getX());
            minY = Math.min(minY, point.getY());
            maxX = Math.max(maxX, point.getX());
            maxY = Math.max(maxY, point.getY());
        }
        
        return new BoundingBox(minX, minY, maxX, maxY);
    }
    
    public List<Vector2D> getPoints() { return new ArrayList<>(points); }
    public List<Triangle> getTriangles() { return new ArrayList<>(triangles); }
    public boolean isSolid() { return isSolid; }
    public void setSolid(boolean solid) { 
        this.isSolid = solid;
        if (solid && triangles.isEmpty()) {
            triangles = triangulate();
        } else if (!solid) {
            triangles.clear();
        }
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
        } else if (other.getType() == GeometryType.COMPOSITE) {
            return ((CompositeShape) other).intersects(this);
        }
        return false;
    }
    
    private boolean intersectsCircle(Circle circle) {
        if (isSolid) {
            // Check if any triangle contains the circle center
            for (Triangle triangle : triangles) {
                if (triangle.containsPoint(circle.getCenter())) {
                    return true;
                }
            }
            
            // Check if circle intersects any edge
            return intersectsEdges(circle);
        } else {
            // Outline only - check edge intersections
            return intersectsEdges(circle);
        }
    }
    
    private boolean intersectsEdges(Circle circle) {
        for (int i = 0; i < points.size(); i++) {
            Vector2D p1 = points.get(i);
            Vector2D p2 = points.get((i + 1) % points.size());
            Line edge = new Line(p1, p2);
            if (circle.intersectsLine(edge)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean intersectsRectangle(Rectangle rectangle) {
        if (isSolid) {
            // Check if any triangle contains rectangle center
            for (Triangle triangle : triangles) {
                if (triangle.containsPoint(rectangle.getCenter())) {
                    return true;
                }
            }
            
            // Check if rectangle contains any triangle center
            for (Triangle triangle : triangles) {
                if (rectangle.containsPoint(triangle.getCenter())) {
                    return true;
                }
            }
        }
        
        // Check edge intersections
        for (int i = 0; i < points.size(); i++) {
            Vector2D p1 = points.get(i);
            Vector2D p2 = points.get((i + 1) % points.size());
            Line edge = new Line(p1, p2);
            if (rectangle.intersectsLine(edge)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsPolygon(Polygon polygon) {
        if (isSolid) {
            // Check triangle-polygon intersections
            for (Triangle triangle : triangles) {
                Vector2D[] triangleVerts = {triangle.p1, triangle.p2, triangle.p3};
                Polygon trianglePoly = new Polygon(triangleVerts);
                if (trianglePoly.intersects(polygon)) {
                    return true;
                }
            }
        } else {
            // Outline only - check edge intersections
            for (int i = 0; i < points.size(); i++) {
                Vector2D p1 = points.get(i);
                Vector2D p2 = points.get((i + 1) % points.size());
                Line edge = new Line(p1, p2);
                if (polygon.intersectsLine(edge)) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    private boolean intersectsLine(Line line) {
        if (isSolid) {
            // Check if any triangle contains line start or end
            for (Triangle triangle : triangles) {
                if (triangle.containsPoint(line.getStart()) || triangle.containsPoint(line.getEnd())) {
                    return true;
                }
            }
        }
        
        // Check edge intersections
        for (int i = 0; i < points.size(); i++) {
            Vector2D p1 = points.get(i);
            Vector2D p2 = points.get((i + 1) % points.size());
            Line edge = new Line(p1, p2);
            if (edge.intersects(line)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsCurve(Curve curve) {
        if (isSolid) {
            // Check if any triangle contains curve sample points
            for (Vector2D curvePoint : curve.getSamplePoints()) {
                for (Triangle triangle : triangles) {
                    if (triangle.containsPoint(curvePoint)) {
                        return true;
                    }
                }
            }
        }
        
        // Check edge intersections with curve samples
        for (int i = 0; i < points.size(); i++) {
            Vector2D p1 = points.get(i);
            Vector2D p2 = points.get((i + 1) % points.size());
            Line edge = new Line(p1, p2);
            if (curve.intersects(edge)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean intersectsEllipse(Ellipse ellipse) {
        if (isSolid) {
            // Check if any triangle contains ellipse center
            for (Triangle triangle : triangles) {
                if (triangle.containsPoint(ellipse.getCenter())) {
                    return true;
                }
            }
            
            // Check if ellipse contains any triangle center
            for (Triangle triangle : triangles) {
                if (ellipse.containsPoint(triangle.getCenter())) {
                    return true;
                }
            }
        }
        
        // Check edge intersections
        for (int i = 0; i < points.size(); i++) {
            Vector2D p1 = points.get(i);
            Vector2D p2 = points.get((i + 1) % points.size());
            Line edge = new Line(p1, p2);
            if (ellipse.intersectsLine(edge)) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        if (!isSolid) {
            // For outline, check if point is very close to any edge
            double threshold = 2.0;
            for (int i = 0; i < points.size(); i++) {
                Vector2D p1 = points.get(i);
                Vector2D p2 = points.get((i + 1) % points.size());
                double distance = pointToLineDistance(point, p1, p2);
                if (distance <= threshold) {
                    return true;
                }
            }
            return false;
        } else {
            // For solid, check if any triangle contains the point
            for (Triangle triangle : triangles) {
                if (triangle.containsPoint(point)) {
                    return true;
                }
            }
            return false;
        }
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
        return boundingBox;
    }
    
    @Override
    public Vector2D getCenter() {
        return center;
    }
    
    @Override
    public void translate(Vector2D offset) {
        for (int i = 0; i < points.size(); i++) {
            points.set(i, points.get(i).add(offset));
        }
        
        for (Triangle triangle : triangles) {
            triangle.p1 = triangle.p1.add(offset);
            triangle.p2 = triangle.p2.add(offset);
            triangle.p3 = triangle.p3.add(offset);
        }
        
        center = center.add(offset);
        boundingBox = new BoundingBox(
            boundingBox.getMinX() + offset.getX(),
            boundingBox.getMinY() + offset.getY(),
            boundingBox.getMaxX() + offset.getX(),
            boundingBox.getMaxY() + offset.getY()
        );
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.FREEFORM;
    }
    
    // Static factory methods for common free-form shapes
    
    public static FreeForm createHeart(Vector2D center, double size) {
        List<Vector2D> points = new ArrayList<>();
        int numPoints = 32;
        
        for (int i = 0; i < numPoints; i++) {
            double t = 2 * Math.PI * i / numPoints;
            double x = 16 * Math.pow(Math.sin(t), 3);
            double y = 13 * Math.cos(t) - 5 * Math.cos(2*t) - 2 * Math.cos(3*t) - Math.cos(4*t);
            
            points.add(new Vector2D(
                center.getX() + x * size / 20,
                center.getY() - y * size / 20
            ));
        }
        
        return new FreeForm(points, true);
    }
    
    public static FreeForm createStarShape(Vector2D center, double outerRadius, double innerRadius, int points) {
        List<Vector2D> starPoints = new ArrayList<>();
        
        for (int i = 0; i < points * 2; i++) {
            double angle = i * Math.PI / points - Math.PI / 2;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            starPoints.add(new Vector2D(x, y));
        }
        
        return new FreeForm(starPoints, true);
    }
    
    public static FreeForm createBlob(Vector2D center, double baseRadius, int numBlobs, double variation) {
        List<Vector2D> blobPoints = new ArrayList<>();
        int numPoints = 64;
        
        for (int i = 0; i < numPoints; i++) {
            double angle = 2 * Math.PI * i / numPoints;
            double radius = baseRadius;
            
            // Add random variation
            for (int j = 0; j < numBlobs; j++) {
                double blobAngle = 2 * Math.PI * j / numBlobs;
                double blobFactor = Math.cos(angle - blobAngle) * 2 + 1;
                radius += Math.sin(blobFactor * angle) * variation;
            }
            
            blobPoints.add(new Vector2D(
                center.getX() + radius * Math.cos(angle),
                center.getY() + radius * Math.sin(angle)
            ));
        }
        
        return new FreeForm(blobPoints, true);
    }
}