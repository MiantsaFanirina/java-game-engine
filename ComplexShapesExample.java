import engine.*;
import engine.collision.*;
import java.util.List;

public class ComplexShapesExample {
    
    public static void main(String[] args) {
        System.out.println("=== Complex Shapes and Free-Form Collision System Demo ===\n");
        
        // Create game and scene
        Game game = new Game("Complex Shapes Demo");
        Scene scene = new Scene("Complex Shapes Scene");
        game.addScene(scene);
        game.setCurrentScene(scene);
        
        System.out.println("Creating complex shapes...\n");
        
        // 1. Bezier Curve
        Vector2D[] bezierPoints = {
            new Vector2D(50, 100),
            new Vector2D(100, 50),
            new Vector2D(150, 150),
            new Vector2D(200, 100)
        };
        GameObject curveObject = createGameObject("bezier_curve", 
            ShapeFactory.createBezierCurve(bezierPoints, 50));
        scene.addGameObject(curveObject);
        System.out.println("✓ Created Bézier curve with 4 control points");
        
        // 2. Sine Wave
        GameObject sineWaveObject = createGameObject("sine_wave",
            ShapeFactory.createSineWave(new Vector2D(50, 200), new Vector2D(250, 200), 30, 3));
        scene.addGameObject(sineWaveObject);
        System.out.println("✓ Created sine wave with amplitude 30 and 3 cycles");
        
        // 3. Ellipse
        GameObject ellipseObject = createGameObject("ellipse",
            ShapeFactory.createEllipse(new Vector2D(350, 100), 60, 40, Math.PI/6));
        scene.addGameObject(ellipseObject);
        System.out.println("✓ Created ellipse with rotation (30 degrees)");
        
        // 4. Heart Shape
        GameObject heartObject = createGameObject("heart",
            ShapeFactory.createHeart(new Vector2D(500, 150), 30));
        scene.addGameObject(heartObject);
        System.out.println("✓ Created heart-shaped free-form geometry");
        
        // 5. Star Shape
        GameObject starObject = createGameObject("star",
            ShapeFactory.createStar(new Vector2D(100, 350), 50, 20, 8));
        scene.addGameObject(starObject);
        System.out.println("✓ Created 8-pointed star with composite geometry");
        
        // 6. Cloud Shape
        GameObject cloudObject = createGameObject("cloud",
            ShapeFactory.createCloud(new Vector2D(300, 350), 40, 6));
        scene.addGameObject(cloudObject);
        System.out.println("✓ Created cloud from overlapping circles");
        
        // 7. Lightning Bolt
        GameObject lightningObject = createGameObject("lightning",
            ShapeFactory.createLightning(new Vector2D(400, 300), new Vector2D(450, 400), 8, 20));
        scene.addGameObject(lightningObject);
        System.out.println("✓ Created procedural lightning bolt");
        
        // 8. Capsule Shape
        GameObject capsuleObject = createGameObject("capsule",
            ShapeFactory.createCapsule(new Vector2D(550, 350), new Vector2D(650, 380), 20));
        scene.addGameObject(capsuleObject);
        System.out.println("✓ Created capsule from circles and rectangle");
        
        // 9. Rounded Rectangle
        GameObject roundedRectObject = createGameObject("rounded_rect",
            ShapeFactory.createRoundedRectangle(new Vector2D(150, 500), 120, 60, 15));
        scene.addGameObject(roundedRectObject);
        System.out.println("✓ Created rounded rectangle");
        
        // 10. Blob Shape
        GameObject blobObject = createGameObject("blob",
            ShapeFactory.createBlob(new Vector2D(400, 500), 35, 4, 15));
        scene.addGameObject(blobObject);
        System.out.println("✓ Created organic blob shape");
        
        // 11. Spiral Curve
        GameObject spiralObject = createGameObject("spiral",
            ShapeFactory.createSpiral(new Vector2D(550, 500), 10, 50, 3));
        scene.addGameObject(spiralObject);
        System.out.println("✓ Created 3-turn spiral curve");
        
        // 12. Asteroid
        GameObject asteroidObject = createGameObject("asteroid",
            ShapeFactory.createAsteroid(new Vector2D(650, 150), 30, 12, 0.4));
        scene.addGameObject(asteroidObject);
        System.out.println("✓ Created procedural asteroid with 12 vertices");
        
        System.out.println("\nTotal objects in scene: " + scene.getGameObjectCount());
        
        // Test collisions between different shape types
        System.out.println("\n=== Collision Tests ===");
        
        // Add some basic shapes for collision testing
        Circle testCircle = new Circle(new Vector2D(350, 100), 25);
        GameObject testCircleObject = createGameObject("test_circle", testCircle);
        scene.addGameObject(testCircleObject);
        
        Rectangle testRect = new Rectangle(new Vector2D(500, 150), 60, 40);
        GameObject testRectObject = createGameObject("test_rect", testRect);
        scene.addGameObject(testRectObject);
        
        System.out.println("\n--- Circle vs Complex Shapes ---");
        testCollision(testCircleObject, heartObject);
        testCollision(testCircleObject, starObject);
        testCollision(testCircleObject, lightningObject);
        
        System.out.println("\n--- Rectangle vs Complex Shapes ---");
        testCollision(testRectObject, ellipseObject);
        testCollision(testRectObject, cloudObject);
        testCollision(testRectObject, blobObject);
        
        System.out.println("\n--- Complex Shapes vs Complex Shapes ---");
        testCollision(heartObject, starObject);
        testCollision(curveObject, sineWaveObject);
        testCollision(capsuleObject, roundedRectObject);
        
        // Test shape factory variety
        System.out.println("\n=== Shape Factory Variety Test ===");
        testShapeFactoryMethods(scene);
        
        // Performance test with many complex shapes
        System.out.println("\n=== Performance Test ===");
        performanceTest(scene);
        
        // Physics simulation
        System.out.println("\n=== Physics Simulation ===");
        runPhysicsSimulation(scene);
        
        System.out.println("\n=== Complex Shapes Demo Complete ===");
        System.out.println("The collision system now supports:");
        System.out.println("• Bézier curves with arbitrary control points");
        System.out.println("• Sine waves, spirals, and custom curves");
        System.out.println("• Ellipses with rotation");
        System.out.println("• Free-form shapes from point clouds");
        System.out.println("• Composite shapes (capsules, rounded rects, stars)");
        System.out.println("• Procedural generation (lightning, clouds, asteroids)");
        System.out.println("• All combinations of collision detection");
    }
    
    private static GameObject createGameObject(String name, Geometry geometry) {
        GameObject obj = new GameObject(name, geometry);
        TagComponent tag = new TagComponent("complex_shape");
        obj.addComponent(tag);
        return obj;
    }
    
    private static void testCollision(GameObject obj1, GameObject obj2) {
        CollisionResult result = obj1.getCollisionWith(obj2);
        System.out.printf("%s vs %s: %s", 
            obj1.getId(), obj2.getId(), 
            result.isColliding() ? "COLLISION" : "No collision");
        
        if (result.isColliding()) {
            System.out.printf(" (depth: %.2f)", result.getPenetrationDepth());
        }
        System.out.println();
    }
    
    private static void testShapeFactoryMethods(Scene scene) {
        System.out.println("Testing various shape factory methods:");
        
        // Wave shape
        GameObject waveObject = createGameObject("wave",
            ShapeFactory.createWave(new Vector2D(50, 600), new Vector2D(150, 600), 20, 2));
        scene.addGameObject(waveObject);
        System.out.println("✓ Created wave shape");
        
        // Hollow circle
        GameObject hollowCircleObject = createGameObject("hollow_circle",
            ShapeFactory.createHollowCircle(new Vector2D(250, 600), 30, 15));
        scene.addGameObject(hollowCircleObject);
        System.out.println("✓ Created hollow circle");
        
        // Gear shape
        GameObject gearObject = createGameObject("gear",
            ShapeFactory.createGear(new Vector2D(350, 600), 25, 15, 8, 8));
        scene.addGameObject(gearObject);
        System.out.println("✓ Created gear with 8 teeth");
        
        // Terrain
        GameObject terrainObject = createGameObject("terrain",
            ShapeFactory.createTerrain(new Vector2D(450, 600), new Vector2D(550, 600), 10, 25));
        scene.addGameObject(terrainObject);
        System.out.println("✓ Created procedural terrain");
    }
    
    private static void performanceTest(Scene scene) {
        System.out.println("Running performance test with " + scene.getGameObjectCount() + " objects...");
        
        long startTime, endTime;
        
        // Test different spatial indexing methods
        CollisionManager.SpatialIndexingMethod[] methods = {
            CollisionManager.SpatialIndexingMethod.NONE,
            CollisionManager.SpatialIndexingMethod.QUADTREE,
            CollisionManager.SpatialIndexingMethod.SPATIAL_HASH
        };
        
        String[] methodNames = {"Brute Force", "QuadTree", "Spatial Hash"};
        
        for (int i = 0; i < methods.length; i++) {
            scene.getCollisionManager().setIndexingMethod(methods[i]);
            
            startTime = System.nanoTime();
            List<CollisionResult> collisions = scene.getAllCollisions();
            endTime = System.nanoTime();
            
            double duration = (endTime - startTime) / 1_000_000.0;
            System.out.printf("%s: %.2f ms (%d collisions)\n", 
                methodNames[i], duration, collisions.size());
        }
    }
    
    private static void runPhysicsSimulation(Scene scene) {
        System.out.println("Running 5 frames of physics simulation...");
        
        // Add physics to some objects
        List<GameObject> objects = scene.getGameObjects();
        for (GameObject obj : objects) {
            if (obj.getId().contains("test_")) {
                PhysicsComponent physics = new PhysicsComponent();
                physics.setMass(1.0);
                physics.setUseGravity(true);
                obj.addComponent(physics);
                
                // Give initial velocity
                obj.setVelocity(new Vector2D(
                    (Math.random() - 0.5) * 20,
                    (Math.random() - 0.5) * 20
                ));
            }
        }
        
        // Run simulation
        for (int frame = 1; frame <= 5; frame++) {
            System.out.printf("Frame %d: ", frame);
            scene.update(0.016); // 60 FPS
            
            // Count active collisions
            List<CollisionResult> collisions = scene.getAllCollisions();
            System.out.printf("%d active collisions\n", collisions.size());
        }
    }
}