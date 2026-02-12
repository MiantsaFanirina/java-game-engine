package engine.input;

import engine.Component;
import engine.GameObject;
import engine.collision.Vector2D;

public class InputComponent extends Component implements InputEventListener {
    private boolean enabled;
    private int priority;
    private InputManager inputManager;
    
    // Movement settings
    private double moveSpeed;
    private boolean enableMovement;
    
    // Mouse interaction
    private boolean mouseInteractive;
    private boolean isMouseOver;
    private boolean isDraggable;
    private Vector2D dragOffset;
    
    public InputComponent() {
        this.enabled = true;
        this.priority = 0;
        this.moveSpeed = 100.0; // pixels per second
        this.enableMovement = false;
        this.mouseInteractive = false;
        this.isMouseOver = false;
        this.isDraggable = false;
        this.dragOffset = new Vector2D(0, 0);
    }
    
    @Override
    public void start() {
        this.inputManager = InputManager.getInstance();
        inputManager.addListener(this);
    }
    
    @Override
    public void destroy() {
        if (inputManager != null) {
            inputManager.removeListener(this);
        }
    }
    
    @Override
    public void update(double deltaTime) {
        if (!enabled || gameObject == null) {
            return;
        }
        
        if (enableMovement) {
            handleMovement(deltaTime);
        }
    }
    
    @Override
    public void onInputEvent(InputEvent event) {
        if (!enabled || gameObject == null) {
            return;
        }
        
        if (mouseInteractive && event instanceof MouseEvent) {
            handleMouseEvent((MouseEvent) event);
        }
        
        // Allow subclasses to handle specific events
        onSpecificInputEvent(event);
    }
    
    protected void onSpecificInputEvent(InputEvent event) {
        // Override in subclasses for specific behavior
    }
    
    private void handleMovement(double deltaTime) {
        Vector2D velocity = new Vector2D(0, 0);
        Keyboard keyboard = inputManager.getKeyboard();
        
        // Arrow keys or WASD movement
        if (keyboard.isUp() || keyboard.isW()) {
            velocity = velocity.add(new Vector2D(0, -1));
        }
        if (keyboard.isDown() || keyboard.isS()) {
            velocity = velocity.add(new Vector2D(0, 1));
        }
        if (keyboard.isLeft() || keyboard.isA()) {
            velocity = velocity.add(new Vector2D(-1, 0));
        }
        if (keyboard.isRight() || keyboard.isD()) {
            velocity = velocity.add(new Vector2D(1, 0));
        }
        
        // Apply speed and normalize
        if (velocity.magnitude() > 0) {
            velocity = velocity.normalize().multiply(moveSpeed);
            gameObject.setVelocity(velocity);
        } else {
            gameObject.setVelocity(new Vector2D(0, 0));
        }
    }
    
    private void handleMouseEvent(MouseEvent event) {
        Mouse mouse = inputManager.getMouse();
        Vector2D mousePos = mouse.getPosition();
        
        switch (event.getType()) {
            case MOUSE_MOVED:
                handleMouseMove(mousePos);
                break;
            case MOUSE_PRESSED:
                handleMousePressed(event);
                break;
            case MOUSE_RELEASED:
                handleMouseReleased(event);
                break;
            case MOUSE_CLICKED:
                handleMouseClicked(event);
                break;
            case MOUSE_DRAGGED:
                handleMouseDragged(mousePos);
                break;
        }
    }
    
    private void handleMouseMove(Vector2D mousePos) {
        boolean wasOver = isMouseOver;
        isMouseOver = isPointOverObject(mousePos);
        
        if (!wasOver && isMouseOver) {
            onMouseEnter();
        } else if (wasOver && !isMouseOver) {
            onMouseExit();
        } else if (isMouseOver) {
            onMouseHover(mousePos);
        }
    }
    
    private void handleMousePressed(MouseEvent event) {
        if (isMouseOver) {
            if (isDraggable && event.is(MouseEvent.Button.LEFT)) {
                dragOffset = gameObject.getPosition().subtract(event.getPosition());
            }
            onMousePressed(event);
        }
    }
    
    private void handleMouseReleased(MouseEvent event) {
        onMouseReleased(event);
        dragOffset = new Vector2D(0, 0);
    }
    
    private void handleMouseClicked(MouseEvent event) {
        if (isMouseOver) {
            onMouseClicked(event);
        }
    }
    
    private void handleMouseDragged(Vector2D mousePos) {
        if (isDraggable && inputManager.getMouse().isLeftPressed()) {
            Vector2D newPosition = mousePos.add(dragOffset);
            gameObject.setPosition(newPosition);
            onMouseDragged(mousePos);
        }
    }
    
    private boolean isPointOverObject(Vector2D point) {
        if (gameObject == null || gameObject.getCollider() == null) {
            return false;
        }
        
        return gameObject.getCollider().containsPoint(point);
    }
    
    // Mouse event callbacks (override in subclasses)
    protected void onMouseEnter() {}
    protected void onMouseExit() {}
    protected void onMouseHover(Vector2D mousePos) {}
    protected void onMousePressed(MouseEvent event) {}
    protected void onMouseReleased(MouseEvent event) {}
    protected void onMouseClicked(MouseEvent event) {}
    protected void onMouseDragged(Vector2D mousePos) {}
    
    // Getters and setters
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { 
        this.priority = priority;
        // Re-register with new priority
        if (inputManager != null) {
            inputManager.removeListener(this);
            inputManager.addListener(this);
        }
    }
    
    public double getMoveSpeed() { return moveSpeed; }
    public void setMoveSpeed(double moveSpeed) { this.moveSpeed = moveSpeed; }
    
    public boolean isMovementEnabled() { return enableMovement; }
    public void setMovementEnabled(boolean enableMovement) { this.enableMovement = enableMovement; }
    
    public boolean isMouseInteractive() { return mouseInteractive; }
    public void setMouseInteractive(boolean mouseInteractive) { this.mouseInteractive = mouseInteractive; }
    
    public boolean isMouseOver() { return isMouseOver; }
    
    public boolean isDraggable() { return isDraggable; }
    public void setDraggable(boolean draggable) { this.isDraggable = draggable; }
    
    @Override
    public boolean canHandle(InputEvent event) {
        if (!enabled) return false;
        
        // Only handle mouse events if mouse interactive
        if (event instanceof MouseEvent) {
            return mouseInteractive;
        }
        
        // Handle all keyboard events by default
        return event instanceof KeyEvent;
    }
    
}