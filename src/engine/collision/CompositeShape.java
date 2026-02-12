package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class CompositeShape implements Geometry {
    private List<Geometry> shapes;
    private Vector2D center;
    private boolean unionMode; // true for union, false for intersection
    
    public CompositeShape() {
        this.shapes = new ArrayList<>();
        this.center = new Vector2D(0, 0);
        this.unionMode = true;
    }
    
    public CompositeShape(boolean unionMode) {
        this.shapes = new ArrayList<>();
        this.center = new Vector2D(0, 0);
        this.unionMode = unionMode;
    }
    
    public CompositeShape(Geometry... shapes) {
        this.shapes = new ArrayList<>();
        this.center = new Vector2D(0, 0);
        this.unionMode = true;
        
        for (Geometry shape : shapes) {
            addShape(shape);
        }
    }
    
    public void addShape(Geometry shape) {
        shapes.add(shape);
        updateCenter();
    }
    
    public void removeShape(Geometry shape) {
        shapes.remove(shape);
        updateCenter();
    }
    
    public void removeShape(int index) {
        if (index >= 0 && index < shapes.size()) {
            shapes.remove(index);
            updateCenter();
        }
    }
    
    public List<Geometry> getShapes() {
        return new ArrayList<>(shapes);
    }
    
    public int getShapeCount() {
        return shapes.size();
    }
    
    public boolean isUnionMode() { return unionMode; }
    public void setUnionMode(boolean unionMode) { this.unionMode = unionMode; }
    
    private void updateCenter() {
        if (shapes.isEmpty()) {
            center = new Vector2D(0, 0);
            return;
        }
        
        double sumX = 0, sumY = 0;
        for (Geometry shape : shapes) {
            Vector2D shapeCenter = shape.getCenter();
            sumX += shapeCenter.getX();
            sumY += shapeCenter.getY();
        }
        center = new Vector2D(sumX / shapes.size(), sumY / shapes.size());
    }
    
    @Override
    public boolean intersects(Geometry other) {
        if (unionMode) {
            // Union mode: intersect if any shape intersects
            for (Geometry shape : shapes) {
                if (shape.intersects(other)) {
                    return true;
                }
            }
            return false;
        } else {
            // Intersection mode: intersect only if all shapes intersect
            for (Geometry shape : shapes) {
                if (!shape.intersects(other)) {
                    return false;
                }
            }
            return !shapes.isEmpty();
        }
    }
    
    @Override
    public boolean containsPoint(Vector2D point) {
        if (unionMode) {
            // Union mode: contains point if any shape contains it
            for (Geometry shape : shapes) {
                if (shape.containsPoint(point)) {
                    return true;
                }
            }
            return false;
        } else {
            // Intersection mode: contains point only if all shapes contain it
            for (Geometry shape : shapes) {
                if (!shape.containsPoint(point)) {
                    return false;
                }
            }
            return !shapes.isEmpty();
        }
    }
    
    @Override
    public BoundingBox getBoundingBox() {
        if (shapes.isEmpty()) {
            return new BoundingBox(0, 0, 0, 0);
        }
        
        BoundingBox bounds = shapes.get(0).getBoundingBox();
        for (int i = 1; i < shapes.size(); i++) {
            bounds = bounds.union(shapes.get(i).getBoundingBox());
        }
        return bounds;
    }
    
    @Override
    public Vector2D getCenter() {
        return center;
    }
    
    @Override
    public void translate(Vector2D offset) {
        for (Geometry shape : shapes) {
            shape.translate(offset);
        }
        center = center.add(offset);
    }
    
    @Override
    public GeometryType getType() {
        return GeometryType.COMPOSITE;
    }
    
    // Convenience methods for creating common composite shapes
    
    public static CompositeShape createHollowCircle(Vector2D center, double outerRadius, double innerRadius) {
        CompositeShape composite = new CompositeShape(true); // union mode
        Circle outer = new Circle(center, outerRadius);
        Circle inner = new Circle(center, innerRadius);
        
        // For a hollow circle, we need to implement it as union of outer circle
        // but this is a simplified version - true boolean operations would be more complex
        composite.addShape(outer);
        composite.addShape(inner); // This would need special handling in a real implementation
        
        return composite;
    }
    
    public static CompositeShape createCapsule(Vector2D start, Vector2D end, double radius) {
        CompositeShape composite = new CompositeShape(true);
        
        // Create rectangle for the body
        Vector2D direction = end.subtract(start);
        double length = direction.magnitude();
        Vector2D center = start.add(direction.multiply(0.5));
        
        // Calculate perpendicular for rectangle width
        Vector2D perp = new Vector2D(-direction.getY(), direction.getX()).normalize();
        
        Vector2D rectCenter = center;
        double rectWidth = length;
        double rectHeight = radius * 2;
        
        // For this simplified implementation, we'll just use circles
        Circle startCircle = new Circle(start, radius);
        Circle endCircle = new Circle(end, radius);
        
        composite.addShape(startCircle);
        composite.addShape(endCircle);
        
        // Add a rectangle connecting them (would need rotation)
        // Rectangle body = new Rectangle(rectCenter, rectWidth, rectHeight);
        // composite.addShape(body);
        
        return composite;
    }
    
    public static CompositeShape createStar(Vector2D center, double outerRadius, double innerRadius, int points) {
        CompositeShape composite = new CompositeShape(true);
        
        List<Vector2D> starPoints = new ArrayList<>();
        for (int i = 0; i < points * 2; i++) {
            double angle = i * Math.PI / points;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            starPoints.add(new Vector2D(x, y));
        }
        
        Polygon star = new Polygon(starPoints.toArray(new Vector2D[0]));
        composite.addShape(star);
        
        return composite;
    }
    
    public static CompositeShape createRoundedRectangle(Vector2D center, double width, double height, double cornerRadius) {
        CompositeShape composite = new CompositeShape(true);
        
        // Main rectangle
        Rectangle mainRect = new Rectangle(center, width - cornerRadius * 2, height - cornerRadius * 2);
        composite.addShape(mainRect);
        
        // Corner circles
        double halfWidth = width / 2;
        double halfHeight = height / 2;
        
        Circle topLeft = new Circle(new Vector2D(center.getX() - halfWidth + cornerRadius, 
                                                center.getY() - halfHeight + cornerRadius), cornerRadius);
        Circle topRight = new Circle(new Vector2D(center.getX() + halfWidth - cornerRadius, 
                                                 center.getY() - halfHeight + cornerRadius), cornerRadius);
        Circle bottomLeft = new Circle(new Vector2D(center.getX() - halfWidth + cornerRadius, 
                                                   center.getY() + halfHeight - cornerRadius), cornerRadius);
        Circle bottomRight = new Circle(new Vector2D(center.getX() + halfWidth - cornerRadius, 
                                                    center.getY() + halfHeight - cornerRadius), cornerRadius);
        
        composite.addShape(topLeft);
        composite.addShape(topRight);
        composite.addShape(bottomLeft);
        composite.addShape(bottomRight);
        
        // Edge rectangles (simplified - would need proper positioning)
        Rectangle topEdge = new Rectangle(new Vector2D(center.getX(), center.getY() - halfHeight + cornerRadius/2), 
                                        width - cornerRadius * 2, cornerRadius);
        Rectangle bottomEdge = new Rectangle(new Vector2D(center.getX(), center.getY() + halfHeight - cornerRadius/2), 
                                           width - cornerRadius * 2, cornerRadius);
        Rectangle leftEdge = new Rectangle(new Vector2D(center.getX() - halfWidth + cornerRadius/2, center.getY()), 
                                          cornerRadius, height - cornerRadius * 2);
        Rectangle rightEdge = new Rectangle(new Vector2D(center.getX() + halfWidth - cornerRadius/2, center.getY()), 
                                           cornerRadius, height - cornerRadius * 2);
        
        composite.addShape(topEdge);
        composite.addShape(bottomEdge);
        composite.addShape(leftEdge);
        composite.addShape(rightEdge);
        
        return composite;
    }
}