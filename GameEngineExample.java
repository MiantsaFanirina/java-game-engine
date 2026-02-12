import engine.*;
import engine.collision.*;
import java.util.List;

public class GameEngineExample {
    
    public static void main(String[] args) {
        System.out.println("=== Game Engine Example ===\n");
        
        // Create a game
        Game game = new Game("My 2D Game");
        game.setTargetFPS(60);
        
        // Create a scene
        Scene scene = new Scene("Main Scene");
        game.addScene(scene);
        game.setCurrentScene(scene);
        
        System.out.println("Created game: " + game.getTitle());
        System.out.println("Current scene: " + scene.getName());
        
        // Create game objects with colliders
        GameObject player = createPlayer();
        GameObject enemy = createEnemy();
        GameObject platform = createPlatform();
        GameObject collectible = createCollectible();
        
        // Add objects to scene
        scene.addGameObject(player);
        scene.addGameObject(enemy);
        scene.addGameObject(platform);
        scene.addGameObject(collectible);
        
        System.out.println("Added " + scene.getGameObjectCount() + " game objects to scene");
        
        // Test initial collisions
        System.out.println("\n=== Initial Collision Test ===");
        testCollisions(scene);
        
        // Move player and test collisions
        System.out.println("\n=== Moving Player ===");
        player.setPosition(new Vector2D(80, 50));
        testCollisions(scene);
        
        // Update physics for a few frames
        System.out.println("\n=== Physics Simulation ===");
        for (int frame = 1; frame <= 5; frame++) {
            System.out.printf("Frame %d: ", frame);
            scene.update(0.016); // 60 FPS
            System.out.printf("Player pos: %s\n", player.getPosition());
        }
        
        // Test component system
        System.out.println("\n=== Component System Test ===");
        TagComponent playerTag = player.getComponent(TagComponent.class);
        if (playerTag != null) {
            System.out.println("Player tags: " + playerTag.getTags());
        }
        
        PhysicsComponent playerPhysics = player.getComponent(PhysicsComponent.class);
        if (playerPhysics != null) {
            System.out.println("Player mass: " + playerPhysics.getMass());
            System.out.println("Player uses gravity: " + playerPhysics.isUseGravity());
        }
        
        // Test finding objects by tag
        System.out.println("\n=== Finding Objects by Tag ===");
        List<GameObject> enemies = scene.findGameObjectsByTag("enemy");
        System.out.println("Found " + enemies.size() + " enemies");
        
        List<GameObject> collectibles = scene.findGameObjectsByTag("collectible");
        System.out.println("Found " + collectibles.size() + " collectibles");
        
        // Test spatial indexing methods
        System.out.println("\n=== Performance Comparison ===");
        testPerformance(scene);
        
        // Start the game loop (just for demonstration)
        System.out.println("\n=== Game Loop Demo ===");
        game.start();
        System.out.println("Game is running: " + game.isRunning());
        game.stop();
        System.out.println("Game is running: " + game.isRunning());
        
        System.out.println("\n=== Game Engine Demo Complete ===");
        System.out.println("Total game objects across all scenes: " + game.getTotalGameObjectCount());
    }
    
    private static GameObject createPlayer() {
        Circle collider = new Circle(new Vector2D(50, 50), 20);
        GameObject player = new GameObject("player", collider);
        
        TagComponent tag = new TagComponent("player", "friendly");
        player.addComponent(tag);
        
        PhysicsComponent physics = new PhysicsComponent();
        physics.setMass(1.0);
        physics.setRestitution(0.8);
        player.addComponent(physics);
        
        player.setVelocity(new Vector2D(10, 0));
        
        return player;
    }
    
    private static GameObject createEnemy() {
        Circle collider = new Circle(new Vector2D(80, 50), 15);
        GameObject enemy = new GameObject("enemy", collider);
        
        TagComponent tag = new TagComponent("enemy", "hostile");
        enemy.addComponent(tag);
        
        PhysicsComponent physics = new PhysicsComponent();
        physics.setMass(0.5);
        physics.setStatic(true);
        enemy.addComponent(physics);
        
        return enemy;
    }
    
    private static GameObject createPlatform() {
        Rectangle collider = new Rectangle(new Vector2D(150, 100), 60, 40);
        GameObject platform = new GameObject("platform", collider);
        
        TagComponent tag = new TagComponent("platform", "solid");
        platform.addComponent(tag);
        
        PhysicsComponent physics = new PhysicsComponent();
        physics.setStatic(true);
        platform.addComponent(physics);
        
        return platform;
    }
    
    private static GameObject createCollectible() {
        Polygon collider = Polygon.createRegularPolygon(new Vector2D(200, 150), 15, 6);
        GameObject collectible = new GameObject("collectible", collider);
        
        TagComponent tag = new TagComponent("collectible", "item");
        collectible.addComponent(tag);
        
        return collectible;
    }
    
    private static void testCollisions(Scene scene) {
        List<CollisionResult> collisions = scene.getAllCollisions();
        System.out.println("Active collisions: " + collisions.size());
        
        for (int i = 0; i < Math.min(collisions.size(), 3); i++) {
            CollisionResult result = collisions.get(i);
            System.out.printf("  Collision %d: depth=%.2f\n", i + 1, result.getPenetrationDepth());
        }
    }
    
    private static void testPerformance(Scene scene) {
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
            for (int j = 0; j < 100; j++) {
                scene.getAllCollisions();
            }
            endTime = System.nanoTime();
            
            System.out.printf("%s: %.2f ms\n", methodNames[i], (endTime - startTime) / 1_000_000.0);
        }
    }
}