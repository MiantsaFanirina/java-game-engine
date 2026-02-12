import engine.*;
import engine.collision.*;
import engine.input.*;
import java.util.List;

public class InputSystemExample {
    
    public static void main(String[] args) {
        System.out.println("=== Input System Demo ===\n");
        
        // Create game with input support
        Game game = new Game("Input System Demo");
        Scene scene = new Scene("Input Scene");
        game.addScene(scene);
        game.setCurrentScene(scene);
        
        System.out.println("Creating interactive game objects...\n");
        
        // Create a controllable player
        GameObject player = createPlayer();
        scene.addGameObject(player);
        System.out.println("✓ Created WASD/Arrow controlled player (Space=Jump, E=Action)");
        
        // Create draggable objects
        GameObject draggableCircle = createDraggableObject("draggable_circle", 
            ShapeFactory.createCircle(new Vector2D(200, 200), 30));
        scene.addGameObject(draggableCircle);
        System.out.println("✓ Created draggable circle");
        
        GameObject draggableStar = createDraggableObject("draggable_star",
            ShapeFactory.createStar(new Vector2D(400, 200), 40, 15, 5));
        scene.addGameObject(draggableStar);
        System.out.println("✓ Created draggable star");
        
        // Create interactive hover objects
        GameObject hoverButton = createHoverButton();
        scene.addGameObject(hoverButton);
        System.out.println("✓ Created hover button");
        
        // Create custom input object
        GameObject customObject = createCustomInputObject();
        scene.addGameObject(customObject);
        System.out.println("✓ Created custom input handler");
        
        // Add global input listeners
        setupGlobalInputListeners(game);
        
        // Setup key mappings
        setupKeyMappings(game);
        
        System.out.println("\n=== Input System Features ===");
        demonstrateInputCapabilities(game);
        
        // Simulate some input for demonstration
        System.out.println("\n=== Simulated Input Demo ===");
        simulateInputEvents(game);
        
        // Test input state queries
        System.out.println("\n=== Input State Queries ===");
        testInputStateQueries(game);
        
        // Performance test
        System.out.println("\n=== Performance Test ===");
        performanceTest(game);
        
        System.out.println("\n=== Input System Demo Complete ===");
        System.out.println("The input system provides:");
        System.out.println("• Keyboard input with full key support");
        System.out.println("• Mouse input with buttons, movement, and wheel");
        System.out.println("• Event-driven architecture with listeners");
        System.out.println("• Input components for game objects");
        System.out.println("• Player controller with movement and actions");
        System.out.println("• Draggable and interactive objects");
        System.out.println("• Key mapping system");
        System.out.println("• High-performance input processing");
    }
    
    private static GameObject createPlayer() {
        Circle collider = ShapeFactory.createCircle(new Vector2D(100, 100), 25);
        GameObject player = new GameObject("player", collider);
        
        TagComponent tag = new TagComponent("player", "controllable");
        player.addComponent(tag);
        
        PhysicsComponent physics = new PhysicsComponent();
        physics.setMass(1.0);
        player.addComponent(physics);
        
        PlayerController controller = new PlayerController(150.0, 250.0);
        player.addComponent(controller);
        
        return player;
    }
    
    private static GameObject createDraggableObject(String name, Geometry shape) {
        GameObject obj = new GameObject(name, shape);
        
        TagComponent tag = new TagComponent("draggable", "interactive");
        obj.addComponent(tag);
        
        InputComponent input = new InputComponent();
        input.setMouseInteractive(true);
        input.setDraggable(true);
        input.setPriority(1); // Higher priority for draggable objects
        
        // Override mouse callbacks
        input = new InputComponent() {
            @Override
            protected void onMouseEnter() {
                System.out.println("Mouse entered " + name);
            }
            
            @Override
            protected void onMouseExit() {
                System.out.println("Mouse exited " + name);
            }
            
            @Override
            protected void onMousePressed(MouseEvent event) {
                System.out.println("Mouse pressed on " + name);
            }
            
            @Override
            protected void onMouseClicked(MouseEvent event) {
                System.out.println("Mouse clicked on " + name);
            }
        };
        input.setMouseInteractive(true);
        input.setDraggable(true);
        input.setPriority(1);
        
        obj.addComponent(input);
        return obj;
    }
    
    private static GameObject createHoverButton() {
        Geometry shape = ShapeFactory.createRoundedRectangle(new Vector2D(300, 350), 120, 40, 10);
        GameObject button = new GameObject("hover_button", shape);
        
        TagComponent tag = new TagComponent("button", "ui");
        button.addComponent(tag);
        
        InputComponent input = new InputComponent() {
            private boolean wasPressed = false;
            
            @Override
            protected void onMouseEnter() {
                System.out.println("Button: Mouse entered (hover effect would trigger)");
            }
            
            @Override
            protected void onMouseExit() {
                System.out.println("Button: Mouse exited (hover effect would end)");
            }
            
            @Override
            protected void onMousePressed(MouseEvent event) {
                wasPressed = true;
                System.out.println("Button: Pressed (visual press effect)");
            }
            
            @Override
            protected void onMouseReleased(MouseEvent event) {
                if (wasPressed && isMouseOver()) {
                    System.out.println("Button: Clicked! (Action triggered)");
                    // Here you would trigger the button's action
                }
                wasPressed = false;
            }
        };
        input.setMouseInteractive(true);
        input.setPriority(2); // High priority for UI elements
        
        button.addComponent(input);
        return button;
    }
    
    private static GameObject createCustomInputObject() {
        Geometry shape = ShapeFactory.createHeart(new Vector2D(500, 400), 25);
        GameObject obj = new GameObject("custom_input", shape);
        
        TagComponent tag = new TagComponent("custom", "special");
        obj.addComponent(tag);
        
        InputComponent input = new InputComponent() {
            @Override
            protected void onSpecificInputEvent(InputEvent event) {
                if (event.getType() == InputEvent.Type.KEY_PRESSED) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    if (keyEvent.is(KeyEvent.KeyCode.H)) {
                        System.out.println("Heart: Pressed H - Hello!");
                    } else if (keyEvent.is(KeyEvent.KeyCode.L)) {
                        System.out.println("Heart: Pressed L - Love!");
                    }
                }
            }
            
            @Override
            protected void onMouseClicked(MouseEvent event) {
                if (event.is(MouseEvent.Button.RIGHT)) {
                    System.out.println("Heart: Right-clicked - Spreading love!");
                }
            }
        };
        input.setMouseInteractive(true);
        
        obj.addComponent(input);
        return obj;
    }
    
    private static void setupGlobalInputListeners(Game game) {
        InputManager inputManager = game.getInputManager();
        
        // Add global keyboard listener
        inputManager.addListener(new InputEventListener() {
            @Override
            public void onInputEvent(InputEvent event) {
                if (event.getType() == InputEvent.Type.KEY_PRESSED) {
                    KeyEvent keyEvent = (KeyEvent) event;
                    
                    switch (keyEvent.getKeyCode()) {
                        case ESCAPE:
                            System.out.println("Global: ESC pressed - Would pause game");
                            break;
                        case F1:
                            System.out.println("Global: F1 pressed - Would show help");
                            break;
                        case I:
                            System.out.println("Global: I pressed - Would show inventory");
                            break;
                    }
                }
            }
            
            @Override
            public int getPriority() {
                return -1; // Low priority for global listeners
            }
        });
        
        // Add global mouse listener
        inputManager.addListener(new InputEventListener() {
            @Override
            public void onInputEvent(InputEvent event) {
                if (event.getType() == InputEvent.Type.MOUSE_WHEEL_MOVED) {
                    MouseEvent mouseEvent = (MouseEvent) event;
                    System.out.println("Global: Mouse wheel moved - " + mouseEvent.getWheelRotation());
                }
            }
        });
    }
    
    private static void setupKeyMappings(Game game) {
        InputManager inputManager = game.getInputManager();
        
        // Map some keys to actions
        inputManager.mapKey(KeyEvent.KeyCode.P, new InputManager.InputAction() {
            @Override
            public void execute() {
                System.out.println("Key Mapping: P key pressed - Would pause game");
            }
        });
        
        inputManager.mapKey(KeyEvent.KeyCode.R, new InputManager.InputAction() {
            @Override
            public void execute() {
                System.out.println("Key Mapping: R key pressed - Would restart level");
            }
        });
        
        // Map multiple keys to the same action
        inputManager.mapKeys(new KeyEvent.KeyCode[]{KeyEvent.KeyCode.NUM_1, KeyEvent.KeyCode.NUMPAD_1}, 
            new InputManager.InputAction() {
            @Override
            public void execute() {
                System.out.println("Key Mapping: 1 pressed - Would select weapon 1");
            }
        });
    }
    
    private static void demonstrateInputCapabilities(Game game) {
        InputManager inputManager = game.getInputManager();
        Keyboard keyboard = inputManager.getKeyboard();
        Mouse mouse = inputManager.getMouse();
        
        System.out.println("Keyboard Features:");
        System.out.println("• Full key support: " + KeyEvent.KeyCode.values().length + " keys");
        System.out.println("• Just pressed/released detection");
        System.out.println("• Modifier key tracking (Shift, Ctrl, Alt, Meta)");
        System.out.println("• Character input support");
        
        System.out.println("\nMouse Features:");
        System.out.println("• Position tracking: " + mouse.getPosition());
        System.out.println("• Button support: " + MouseEvent.Button.values().length + " buttons");
        System.out.println("• Movement delta and distance");
        System.out.println("• Click detection (single, double, triple)");
        System.out.println("• Drag and drop support");
        System.out.println("• Mouse wheel support");
        
        System.out.println("\nEvent System:");
        System.out.println("• Listener-based architecture");
        System.out.println("• Priority system for event handling");
        System.out.println("• Event consumption and propagation");
        System.out.println("• Type-safe event handling");
        
        System.out.println("\nInput Components:");
        System.out.println("• Player controller with movement");
        System.out.println("• Draggable objects");
        System.out.println("• Interactive hover states");
        System.out.println("• Custom input handling");
    }
    
    private static void simulateInputEvents(Game game) {
        InputManager inputManager = game.getInputManager();
        Keyboard keyboard = inputManager.getKeyboard();
        Mouse mouse = inputManager.getMouse();
        
        System.out.println("Simulating keyboard input...");
        keyboard.keyPressed(KeyEvent.KeyCode.W, 'w', false, false, false, false);
        keyboard.update();
        
        System.out.println("W key pressed: " + keyboard.isKeyPressed(KeyEvent.KeyCode.W));
        System.out.println("W key just pressed: " + keyboard.isKeyJustPressed(KeyEvent.KeyCode.W));
        
        System.out.println("\nSimulating mouse input...");
        mouse.mousePressed(250, 250, MouseEvent.Button.LEFT, false, false, false, false);
        mouse.update();
        
        System.out.println("Left mouse pressed: " + mouse.isLeftPressed());
        System.out.println("Left mouse just pressed: " + mouse.isLeftJustPressed());
        System.out.println("Mouse position: " + mouse.getPosition());
        
        // Simulate mouse movement
        mouse.mouseMoved(260, 260);
        mouse.update();
        
        System.out.println("Mouse delta: " + mouse.getDelta());
        System.out.println("Mouse moved: " + mouse.hasMoved());
    }
    
    private static void testInputStateQueries(Game game) {
        InputManager inputManager = game.getInputManager();
        Keyboard keyboard = inputManager.getKeyboard();
        Mouse mouse = inputManager.getMouse();
        
        System.out.println("Keyboard State:");
        System.out.println("• W key: " + (keyboard.isW() ? "PRESSED" : "NOT PRESSED"));
        System.out.println("• Shift: " + (keyboard.isShift() ? "PRESSED" : "NOT PRESSED"));
        System.out.println("• Space: " + (keyboard.isSpace() ? "PRESSED" : "NOT PRESSED"));
        System.out.println("• Any key just pressed: " + (keyboard.isKeyJustPressed(KeyEvent.KeyCode.A) || 
                                                      keyboard.isKeyJustPressed(KeyEvent.KeyCode.S) ||
                                                      keyboard.isKeyJustPressed(KeyEvent.KeyCode.D)));
        
        System.out.println("\nMouse State:");
        System.out.println("• Position: " + mouse.getPosition());
        System.out.println("• Left button: " + (mouse.isLeftPressed() ? "PRESSED" : "NOT PRESSED"));
        System.out.println("• Right button: " + (mouse.isRightPressed() ? "PRESSED" : "NOT PRESSED"));
        System.out.println("• Wheel rotation: " + mouse.getWheelRotation());
        System.out.println("• Is dragging: " + mouse.isDragging());
        
        System.out.println("\nInput Manager State:");
        System.out.println("• Initialized: " + inputManager.isInitialized());
        System.out.println("• Listeners: " + inputManager.getListenerCount());
        System.out.println("• Has any input: " + inputManager.hasAnyInput());
    }
    
    private static void performanceTest(Game game) {
        InputManager inputManager = game.getInputManager();
        
        System.out.println("Testing input system performance...");
        
        long startTime = System.nanoTime();
        
        // Simulate many input events
        for (int i = 0; i < 10000; i++) {
            KeyEvent event = new KeyEvent(InputEvent.Type.KEY_PRESSED, 
                KeyEvent.KeyCode.A, 'a', false, false, false, false);
            inputManager.dispatchEvent(event);
        }
        
        long endTime = System.nanoTime();
        double duration = (endTime - startTime) / 1_000_000.0;
        
        System.out.printf("Dispatched 10,000 events in %.2f ms (%.1f events/ms)\n", 
            duration, 10000.0 / duration);
        System.out.println("Input system is " + (duration < 100 ? "FAST" : "NEEDS OPTIMIZATION"));
    }
}