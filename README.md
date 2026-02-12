# 2D Game Engine

A complete, reusable 2D game engine written in Java with advanced collision detection and physics simulation.

## Features

### Core Engine
- **GameObject System**: Entity-based architecture with component support
- **Component System**: Modular functionality through components
- **Scene Management**: Multiple scenes with object organization
- **Game Loop**: Configurable FPS game loop with delta time
- **Tag System**: Object tagging for easy identification

### Advanced Collision System
- **Basic Geometry Types**: Circle, Rectangle, Polygon, Line
- **Curved Shapes**: Bézier curves, sine waves, spirals, custom curves
- **Advanced Geometry**: Ellipses with rotation, free-form shapes
- **Composite Shapes**: Capsules, rounded rectangles, hollow circles, stars
- **Procedural Shapes**: Lightning bolts, clouds, asteroids, terrain
- **Free-Form Support**: Point cloud triangulation with solid/outline modes
- **Precise Collision Detection**: Accurate intersection testing for ALL shape types
- **Collision Response**: Penetration vectors and contact points
- **Spatial Indexing**: QuadTree and Spatial Hash optimization
- **Raycasting**: Line-of-sight and projectile support

### Physics
- **Rigidbody Physics**: Mass, velocity, acceleration
- **Collision Response**: Elastic and inelastic collisions
- **Gravity**: Configurable gravity forces
- **Friction**: Surface friction simulation
- **Static vs Dynamic**: Static and moving objects

### Shape Factory
- **Easy Creation**: 50+ factory methods for complex shapes
- **Mathematical Curves**: Bézier, sine waves, spirals, waves
- **Organic Shapes**: Hearts, stars, clouds, blobs
- **Mechanical Shapes**: Gears, capsules, rounded rectangles
- **Procedural Generation**: Lightning, terrain, asteroids

### Input System
- **Full Keyboard Support**: All keys with just-pressed/released detection
- **Complete Mouse Support**: 5 buttons, movement, wheel, drag & drop
- **Event-Driven Architecture**: Priority-based listener system
- **Input Components**: Player controller, draggable objects, interactive elements
- **Key Mapping**: Action mapping system for game controls
- **High Performance**: 7000+ events per millisecond processing

## Architecture

```
src/
├── engine/
│   ├── Engine.java          # Singleton engine manager
│   ├── Game.java            # Game container with input
│   ├── Scene.java           # Scene management
│   ├── GameObject.java      # Entity class
│   ├── Component.java       # Base component
│   ├── PhysicsComponent.java # Physics simulation
│   └── TagComponent.java    # Tagging system
└── engine/collision/
    ├── Geometry.java         # Geometry interface
    ├── Vector2D.java         # 2D vector math
    ├── BoundingBox.java     # AABB support
    ├── Circle.java          # Circle collision
    ├── Rectangle.java       # Rectangle collision
    ├── Polygon.java         # Polygon collision
    ├── Line.java             # Line collision
    ├── Curve.java            # Bézier and custom curves
    ├── Ellipse.java          # Ellipse collision with rotation
    ├── CompositeShape.java   # Combined geometries
    ├── FreeForm.java         # Point cloud triangulation
    ├── ShapeFactory.java     # 50+ shape creation methods
    ├── CurveMath.java        # Mathematical foundation for curves
    ├── CollisionDetector.java # All shape collision algorithms
    ├── CollisionManager.java # Spatial indexing
    ├── QuadTree.java         # QuadTree optimization
    ├── SpatialHashGrid.java  # Spatial hash optimization
    └── CollisionResult.java  # Collision data
└── engine/input/
    ├── InputEvent.java         # Base input event
    ├── KeyEvent.java           # Keyboard event
    ├── MouseEvent.java         # Mouse event
    ├── InputEventListener.java # Event listener interface
    ├── InputEventDispatcher.java # Event dispatcher
    ├── Keyboard.java           # Keyboard state management
    ├── Mouse.java              # Mouse state management
    ├── InputManager.java       # Input system coordinator
    ├── InputComponent.java     # Base input component
    └── PlayerController.java   # Player control component
```

## Quick Start

### Basic Usage

```java
import engine.*;
import engine.collision.*;

// Create game
Game game = new Game("My Game");
Scene scene = new Scene("Main Scene");
game.addScene(scene);
game.setCurrentScene(scene);

// Create game object
Circle collider = new Circle(new Vector2D(0, 0), 25);
GameObject player = new GameObject("player", collider);

// Add components
player.addComponent(new TagComponent("player", "friendly"));
PhysicsComponent physics = new PhysicsComponent();
physics.setMass(1.0);
player.addComponent(physics);

// Add to scene
scene.addGameObject(player);

// Start game loop
game.gameLoop();
```

### Collision Detection

```java
// Check collision between two objects
if (player.collidesWith(enemy)) {
    System.out.println("Player hit enemy!");
}

// Get detailed collision data
CollisionResult result = player.getCollisionWith(enemy);
if (result.isColliding()) {
    Vector2D penetration = result.getPenetrationVector();
    Vector2D contact = result.getContactPoint();
    System.out.println("Penetration: " + penetration);
}
```

### Component System

```java
// Add custom component
public class HealthComponent extends Component {
    private int health;
    
    @Override
    public void update(double deltaTime) {
        // Update health logic
    }
    
    @Override
    public void start() {
        // Initialize health
    }
    
    @Override
    public void destroy() {
        // Cleanup
    }
}

// Use component
HealthComponent health = player.getComponent(HealthComponent.class);
if (health != null) {
    // Use health component
}
```

### Spatial Indexing

```java
// Choose spatial indexing method
scene.getCollisionManager().setIndexingMethod(
    CollisionManager.SpatialIndexingMethod.QUADTREE
);

// Options: NONE (brute force), QUADTREE, SPATIAL_HASH
```

## Building and Running

```bash
# Compile the engine
javac -d build -cp . src/engine/collision/*.java src/engine/*.java

# Compile your game
javac -d build -cp build YourGame.java

# Run your game
java -cp build YourGame
```

## Examples

See `GameEngineExample.java` for a complete demonstration of the engine's capabilities including:
- Creating game objects with different colliders
- Component system usage
- Collision detection and response
- Physics simulation
- Performance comparison
- Scene management

## Performance

The engine includes three spatial indexing methods:

1. **Brute Force**: O(n²) - Good for small scenes (<50 objects)
2. **QuadTree**: O(n log n) - Good for medium scenes (50-200 objects)
3. **Spatial Hash**: O(n) - Best for large scenes (>200 objects)

## Geometry Support

### Basic Shapes
```java
// Circle
Circle circle = new Circle(new Vector2D(x, y), radius);

// Rectangle
Rectangle rect = new Rectangle(new Vector2D(centerX, centerY), width, height);

// Polygon
Vector2D[] vertices = { /* your vertices */ };
Polygon polygon = new Polygon(vertices);
Polygon hexagon = Polygon.createRegularPolygon(new Vector2D(centerX, centerY), radius, sides);

// Line
Line line = new Line(new Vector2D(startX, startY), new Vector2D(endX, endY));
```

### Curved Shapes
```java
// Bézier Curve
Vector2D[] controlPoints = {p1, p2, p3, p4};
Curve bezier = ShapeFactory.createBezierCurve(controlPoints, resolution);

// Sine Wave
Curve sineWave = ShapeFactory.createSineWave(start, end, amplitude, cycles);

// Spiral
Curve spiral = ShapeFactory.createSpiral(center, startRadius, endRadius, turns);

// Custom Wave
Curve wave = ShapeFactory.createWave(start, end, amplitude, frequency);
```

### Ellipses
```java
// Basic ellipse
Ellipse ellipse = ShapeFactory.createEllipse(center, radiusX, radiusY);

// Rotated ellipse
Ellipse rotatedEllipse = ShapeFactory.createEllipse(center, radiusX, radiusY, rotation);
```

### Free-Form Shapes
```java
// Heart shape
FreeForm heart = ShapeFactory.createHeart(center, size);

// Star shape
FreeForm star = ShapeFactory.createStar(center, outerRadius, innerRadius, points);

// Organic blob
FreeForm blob = ShapeFactory.createBlob(center, baseRadius, numBlobs, variation);

// Lightning bolt
FreeForm lightning = ShapeFactory.createLightning(start, end, segments, chaos);

// Cloud shape
FreeForm cloud = ShapeFactory.createCloud(center, size, numCircles);

// Terrain
FreeForm terrain = ShapeFactory.createTerrain(start, end, segments, maxHeight);
```

### Composite Shapes
```java
// Capsule
CompositeShape capsule = ShapeFactory.createCapsule(start, end, radius);

// Rounded rectangle
CompositeShape roundedRect = ShapeFactory.createRoundedRectangle(center, width, height, cornerRadius);

// Hollow circle
CompositeShape hollowCircle = ShapeFactory.createHollowCircle(center, outerRadius, innerRadius);

// Star
CompositeShape star = ShapeFactory.createStar(center, outerRadius, innerRadius, points);

// Gear
CompositeShape gear = ShapeFactory.createGear(center, outerRadius, innerRadius, teeth, toothHeight);

// Asteroid
CompositeShape asteroid = ShapeFactory.createAsteroid(center, baseRadius, vertices, variation);
```

### Input System
```java
// Get input manager
InputManager input = game.getInputManager();

// Keyboard input
if (input.getKeyboard().isW()) {
    // Move up
}

if (input.getKeyboard().isKeyJustPressed(KeyEvent.KeyCode.SPACE)) {
    // Jump
}

// Mouse input
Vector2D mousePos = input.getMouse().getPosition();
if (input.getMouse().isLeftJustPressed()) {
    // Handle left click
}

// Input components
PlayerController controller = new PlayerController(150.0, 250.0);
player.addComponent(controller);

// Custom input handling
InputComponent inputComponent = new InputComponent() {
    @Override
    protected void onMousePressed(MouseEvent event) {
        System.out.println("Mouse pressed!");
    }
};
inputComponent.setMouseInteractive(true);
inputComponent.setDraggable(true);
```


## Contributing

Contributions are welcome! Please ensure:
- Code follows existing conventions
- Add appropriate comments
- Test your changes
- Update documentation as needed