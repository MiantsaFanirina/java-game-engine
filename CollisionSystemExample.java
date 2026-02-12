import collision.*;
import java.util.List;

public class CollisionSystemExample {
    
    public static void main(String[] args) {
        System.out.println("=== 2D Collision System Example ===\n");
        
        // Create collision manager with spatial indexing
        CollisionManager manager = new CollisionManager(CollisionManager.SpatialIndexingMethod.QUADTREE);
        
        // Create various geometry objects
        Circle circle1 = new Circle(new Vector2D(50, 50), 20);
        Circle circle2 = new Circle(new Vector2D(80, 50), 25);
        Circle circle3 = new Circle(new Vector2D(200, 200), 30);
        
        Rectangle rect1 = new Rectangle(new Vector2D(150, 100), 60, 40);
        Rectangle rect2 = new Rectangle(new Vector2D(180, 120), 50, 50);
        
        Vector2D[] trianglePoints = {
            new Vector2D(300, 100),
            new Vector2D(350, 200),
            new Vector2D(250, 200)
        };
        Polygon triangle = new Polygon(trianglePoints);
        
        Polygon hexagon = Polygon.createRegularPolygon(new Vector2D(400, 300), 40, 6);
        
        Line line1 = new Line(new Vector2D(0, 0), new Vector2D(100, 100));
        Line line2 = new Line(new Vector2D(100, 0), new Vector2D(0, 100));
        
        // Add objects to collision manager
        manager.addObject(circle1);
        manager.addObject(circle2);
        manager.addObject(circle3);
        manager.addObject(rect1);
        manager.addObject(rect2);
        manager.addObject(triangle);
        manager.addObject(hexagon);
        manager.addObject(line1);
        manager.addObject(line2);
        
        System.out.println("Objects in scene: " + manager.getObjectCount());
        System.out.println("Spatial indexing: " + manager.getSpatialIndexingMethod());
        
        // Check all collisions
        List<CollisionResult> collisions = manager.checkAllCollisions();
        System.out.println("\nDetected collisions: " + collisions.size());
        
        for (int i = 0; i < collisions.size(); i++) {
            CollisionResult result = collisions.get(i);
            System.out.printf("Collision %d: Penetration depth=%.2f, Contact point=%s\n",
                i + 1, result.getPenetrationDepth(), result.getContactPoint());
        }
        
        // Test individual collision checks
        System.out.println("\n=== Individual Collision Tests ===");
        
        System.out.println("Circle1 and Circle2 collide: " + 
            manager.checkCollision(circle1, circle2).isColliding());
        
        System.out.println("Circle1 and Circle3 collide: " + 
            manager.checkCollision(circle1, circle3).isColliding());
        
        System.out.println("Rect1 and Rect2 collide: " + 
            manager.checkCollision(rect1, rect2).isColliding());
        
        System.out.println("Circle2 and Rect1 collide: " + 
            manager.checkCollision(circle2, rect1).isColliding());
        
        System.out.println("Triangle and Hexagon collide: " + 
            manager.checkCollision(triangle, hexagon).isColliding());
        
        System.out.println("Line1 and Line2 collide: " + 
            manager.checkCollision(line1, line2).isColliding());
        
        // Test point containment
        System.out.println("\n=== Point Containment Tests ===");
        Vector2D testPoint1 = new Vector2D(55, 55);
        Vector2D testPoint2 = new Vector2D(300, 150);
        
        System.out.println("Point " + testPoint1 + " in Circle1: " + circle1.containsPoint(testPoint1));
        System.out.println("Point " + testPoint2 + " in Triangle: " + triangle.containsPoint(testPoint2));
        
        // Test raycasting
        System.out.println("\n=== Raycasting Tests ===");
        System.out.println("Ray from origin to (500, 500) hits something: " + 
            manager.raycast(new Vector2D(0, 0), new Vector2D(1, 1), 1000));
        
        System.out.println("Ray from (1000, 1000) to (1100, 1100) hits something: " + 
            manager.raycast(new Vector2D(1000, 1000), new Vector2D(1, 1), 1000));
        
        // Test different spatial indexing methods
        System.out.println("\n=== Performance Comparison ===");
        
        long startTime, endTime;
        
        // Test brute force
        manager.setIndexingMethod(CollisionManager.SpatialIndexingMethod.NONE);
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            manager.checkAllCollisions();
        }
        endTime = System.nanoTime();
        System.out.printf("Brute force: %.2f ms\n", (endTime - startTime) / 1_000_000.0);
        
        // Test QuadTree
        manager.setIndexingMethod(CollisionManager.SpatialIndexingMethod.QUADTREE);
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            manager.checkAllCollisions();
        }
        endTime = System.nanoTime();
        System.out.printf("QuadTree: %.2f ms\n", (endTime - startTime) / 1_000_000.0);
        
        // Test Spatial Hash
        manager.setIndexingMethod(CollisionManager.SpatialIndexingMethod.SPATIAL_HASH);
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            manager.checkAllCollisions();
        }
        endTime = System.nanoTime();
        System.out.printf("Spatial Hash: %.2f ms\n", (endTime - startTime) / 1_000_000.0);
        
        // Demonstrate geometry manipulation
        System.out.println("\n=== Geometry Manipulation ===");
        System.out.println("Original Circle1 center: " + circle1.getCenter());
        circle1.translate(new Vector2D(10, 10));
        System.out.println("After translation: " + circle1.getCenter());
        
        System.out.println("Original Rect1 size: " + rect1.getWidth() + "x" + rect1.getHeight());
        System.out.println("Rect1 bounding box: " + rect1.getBoundingBox());
        
        // Test regular polygon creation
        System.out.println("\n=== Regular Polygon Creation ===");
        for (int sides = 3; sides <= 8; sides++) {
            Polygon poly = Polygon.createRegularPolygon(new Vector2D(600 + sides * 50, 100), 30, sides);
            System.out.printf("%d-sided polygon center: %s\n", sides, poly.getCenter());
        }
        
        System.out.println("\n=== Collision System Demo Complete ===");
    }
}