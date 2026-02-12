package engine.collision;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShapeFactory {
    
    // Basic Shapes
    public static Circle createCircle(Vector2D center, double radius) {
        return new Circle(center, radius);
    }
    
    public static Rectangle createRectangle(Vector2D center, double width, double height) {
        return new Rectangle(center, width, height);
    }
    
    public static Ellipse createEllipse(Vector2D center, double radiusX, double radiusY) {
        return new Ellipse(center, radiusX, radiusY);
    }
    
    public static Ellipse createEllipse(Vector2D center, double radiusX, double radiusY, double rotation) {
        return new Ellipse(center, radiusX, radiusY, rotation);
    }
    
    public static Polygon createTriangle(Vector2D p1, Vector2D p2, Vector2D p3) {
        return new Polygon(new Vector2D[]{p1, p2, p3});
    }
    
    public static Polygon createRegularPolygon(Vector2D center, double radius, int sides) {
        return Polygon.createRegularPolygon(center, radius, sides);
    }
    
    // Curved Shapes
    public static Curve createBezierCurve(Vector2D[] controlPoints) {
        return new Curve(controlPoints);
    }
    
    public static Curve createBezierCurve(Vector2D[] controlPoints, int resolution) {
        return new Curve(controlPoints, resolution);
    }
    
    public static Curve createSineWave(Vector2D start, Vector2D end, double amplitude, int cycles) {
        Vector2D[] controlPoints = generateSineWavePoints(start, end, amplitude, cycles);
        return new Curve(controlPoints, 100);
    }
    
    public static Curve createSpiral(Vector2D center, double startRadius, double endRadius, int turns) {
        Vector2D[] controlPoints = generateSpiralPoints(center, startRadius, endRadius, turns);
        return new Curve(controlPoints, 200);
    }
    
    public static Curve createWave(Vector2D start, Vector2D end, double amplitude, double frequency) {
        Vector2D direction = end.subtract(start);
        double length = direction.magnitude();
        Vector2D unitDir = direction.normalize();
        Vector2D perp = new Vector2D(-unitDir.getY(), unitDir.getX());
        
        List<Vector2D> points = new ArrayList<>();
        int numPoints = 50;
        
        for (int i = 0; i <= numPoints; i++) {
            double t = (double) i / numPoints;
            Vector2D basePoint = start.add(unitDir.multiply(length * t));
            double offset = amplitude * Math.sin(frequency * 2 * Math.PI * t);
            points.add(basePoint.add(perp.multiply(offset)));
        }
        
        return new Curve(points.toArray(new Vector2D[0]), 50);
    }
    
    // Complex Shapes
    public static FreeForm createHeart(Vector2D center, double size) {
        return FreeForm.createHeart(center, size);
    }
    
    public static FreeForm createStar(Vector2D center, double outerRadius, double innerRadius, int points) {
        return FreeForm.createStarShape(center, outerRadius, innerRadius, points);
    }
    
    public static FreeForm createBlob(Vector2D center, double baseRadius, int numBlobs, double variation) {
        return FreeForm.createBlob(center, baseRadius, numBlobs, variation);
    }
    
    public static FreeForm createLightning(Vector2D start, Vector2D end, int segments, double chaos) {
        List<Vector2D> points = new ArrayList<>();
        points.add(start);
        
        Vector2D direction = end.subtract(start);
        double length = direction.magnitude();
        Vector2D unitDir = direction.normalize();
        Vector2D perp = new Vector2D(-unitDir.getY(), unitDir.getX());
        
        Random random = new Random();
        
        for (int i = 1; i < segments; i++) {
            double t = (double) i / segments;
            Vector2D basePoint = start.add(unitDir.multiply(length * t));
            
            // Add random deviation
            double deviation = (random.nextDouble() - 0.5) * chaos * length / segments;
            basePoint = basePoint.add(perp.multiply(deviation));
            
            points.add(basePoint);
        }
        
        points.add(end);
        
        return new FreeForm(points, false); // false for outline only
    }
    
    public static FreeForm createCloud(Vector2D center, double size, int numCircles) {
        List<Vector2D> cloudPoints = new ArrayList<>();
        Random random = new Random();
        
        // Create overlapping circles for cloud effect
        for (int i = 0; i < numCircles; i++) {
            double angle = 2 * Math.PI * i / numCircles;
            double radius = size * (0.3 + random.nextDouble() * 0.7);
            double distance = size * 0.3;
            double x = center.getX() + Math.cos(angle) * distance;
            double y = center.getY() + Math.sin(angle) * distance;
            
            Vector2D circleCenter = new Vector2D(x, y);
            int pointsPerCircle = 16;
            
            for (int j = 0; j < pointsPerCircle; j++) {
                double circleAngle = 2 * Math.PI * j / pointsPerCircle;
                double px = x + radius * Math.cos(circleAngle);
                double py = y + radius * Math.sin(circleAngle);
                cloudPoints.add(new Vector2D(px, py));
            }
        }
        
        return new FreeForm(cloudPoints, true);
    }
    
    // Composite Shapes
    public static CompositeShape createCapsule(Vector2D start, Vector2D end, double radius) {
        return CompositeShape.createCapsule(start, end, radius);
    }
    
    public static CompositeShape createRoundedRectangle(Vector2D center, double width, double height, double cornerRadius) {
        return CompositeShape.createRoundedRectangle(center, width, height, cornerRadius);
    }
    
    public static CompositeShape createHollowCircle(Vector2D center, double outerRadius, double innerRadius) {
        return CompositeShape.createHollowCircle(center, outerRadius, innerRadius);
    }
    
    public static CompositeShape createGear(Vector2D center, double outerRadius, double innerRadius, int teeth, double toothHeight) {
        CompositeShape gear = new CompositeShape(true);
        
        // Main body circle
        Circle body = new Circle(center, innerRadius);
        gear.addShape(body);
        
        // Teeth
        double anglePerTooth = 2 * Math.PI / teeth;
        for (int i = 0; i < teeth; i++) {
            double angle = i * anglePerTooth;
            double toothX = center.getX() + Math.cos(angle) * (innerRadius + toothHeight/2);
            double toothY = center.getY() + Math.sin(angle) * (innerRadius + toothHeight/2);
            
            Rectangle tooth = new Rectangle(
                new Vector2D(toothX, toothY),
                toothHeight,
                toothHeight * 0.3
            );
            gear.addShape(tooth);
        }
        
        return gear;
    }
    
    // Procedural Shapes
    public static FreeForm createTerrain(Vector2D start, Vector2D end, int segments, double maxHeight) {
        List<Vector2D> terrainPoints = new ArrayList<>();
        Random random = new Random();
        
        Vector2D direction = end.subtract(start);
        double length = direction.magnitude();
        Vector2D unitDir = direction.normalize();
        
        // Generate terrain heights using simple noise
        double[] heights = new double[segments + 1];
        for (int i = 0; i <= segments; i++) {
            heights[i] = random.nextDouble() * maxHeight;
        }
        
        // Smooth the heights
        for (int smooth = 0; smooth < 3; smooth++) {
            double[] smoothed = new double[heights.length];
            for (int i = 0; i < heights.length; i++) {
                double sum = heights[i];
                int count = 1;
                if (i > 0) { sum += heights[i-1]; count++; }
                if (i < heights.length - 1) { sum += heights[i+1]; count++; }
                smoothed[i] = sum / count;
            }
            heights = smoothed;
        }
        
        // Create terrain points
        for (int i = 0; i <= segments; i++) {
            double t = (double) i / segments;
            Vector2D basePoint = start.add(unitDir.multiply(length * t));
            double height = heights[i];
            terrainPoints.add(new Vector2D(basePoint.getX(), basePoint.getY() - height));
        }
        
        return new FreeForm(terrainPoints, false); // Outline only
    }
    
    public static CompositeShape createAsteroid(Vector2D center, double baseRadius, int vertices, double variation) {
        List<Vector2D> asteroidPoints = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < vertices; i++) {
            double angle = 2 * Math.PI * i / vertices;
            double radius = baseRadius * (1 + (random.nextDouble() - 0.5) * variation);
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            asteroidPoints.add(new Vector2D(x, y));
        }
        
        FreeForm asteroidShape = new FreeForm(asteroidPoints, true);
        
        CompositeShape asteroid = new CompositeShape(true);
        asteroid.addShape(asteroidShape);
        
        return asteroid;
    }
    
    // Utility methods
    private static Vector2D[] generateSineWavePoints(Vector2D start, Vector2D end, double amplitude, int cycles) {
        Vector2D direction = end.subtract(start);
        double length = direction.magnitude();
        Vector2D unitDir = direction.normalize();
        Vector2D perp = new Vector2D(-unitDir.getY(), unitDir.getX());
        
        List<Vector2D> points = new ArrayList<>();
        int numPoints = 20;
        
        for (int i = 0; i <= numPoints; i++) {
            double t = (double) i / numPoints;
            Vector2D basePoint = start.add(unitDir.multiply(length * t));
            double offset = amplitude * Math.sin(cycles * 2 * Math.PI * t);
            points.add(basePoint.add(perp.multiply(offset)));
        }
        
        return points.toArray(new Vector2D[0]);
    }
    
    private static Vector2D[] generateSpiralPoints(Vector2D center, double startRadius, double endRadius, int turns) {
        List<Vector2D> points = new ArrayList<>();
        int numPoints = 200;
        
        for (int i = 0; i <= numPoints; i++) {
            double t = (double) i / numPoints;
            double angle = turns * 2 * Math.PI * t;
            double radius = startRadius + (endRadius - startRadius) * t;
            
            double x = center.getX() + radius * Math.cos(angle);
            double y = center.getY() + radius * Math.sin(angle);
            
            points.add(new Vector2D(x, y));
        }
        
        return points.toArray(new Vector2D[0]);
    }
    
    // Advanced Procedural Generation
    public static FreeForm createFractalTree(Vector2D base, Vector2D top, int depth, double spreadAngle) {
        if (depth <= 0) return null;
        
        Vector2D direction = top.subtract(base);
        Vector2D perp = new Vector2D(-direction.getY(), direction.getX()).normalize();
        
        double length = direction.magnitude() * 0.7;
        double leftAngle = -spreadAngle;
        double rightAngle = spreadAngle;
        
        // Create branch points
        Vector2D leftDir = rotate(direction, leftAngle);
        Vector2D rightDir = rotate(direction, rightAngle);
        Vector2D leftTop = top.add(leftDir.normalize().multiply(length));
        Vector2D rightTop = top.add(rightDir.normalize().multiply(length));
        
        List<Vector2D> treePoints = new ArrayList<>();
        treePoints.add(base);
        treePoints.add(top);
        
        // Recursively add branches
        FreeForm leftBranch = createFractalTree(top, leftTop, depth - 1, spreadAngle * 0.8);
        FreeForm rightBranch = createFractalTree(top, rightTop, depth - 1, spreadAngle * 0.8);
        
        if (leftBranch != null) treePoints.addAll(leftBranch.getPoints());
        if (rightBranch != null) treePoints.addAll(rightBranch.getPoints());
        
        return new FreeForm(treePoints, false);
    }
    
    private static Vector2D rotate(Vector2D vector, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(
            vector.getX() * cos - vector.getY() * sin,
            vector.getX() * sin + vector.getY() * cos
        );
    }
}