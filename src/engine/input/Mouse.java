package engine.input;

import engine.collision.Vector2D;
import java.util.EnumSet;
import java.util.Set;

public class Mouse {
    private static Mouse instance;
    private Vector2D position;
    private Vector2D previousPosition;
    private Set<MouseEvent.Button> buttonsPressed;
    private Set<MouseEvent.Button> buttonsJustPressed;
    private Set<MouseEvent.Button> buttonsJustReleased;
    private double wheelRotation;
    private InputEventDispatcher dispatcher;
    private long lastClickTime;
    private MouseEvent.Button lastClickButton;
    private int clickCount;
    
    private Mouse() {
        this.position = new Vector2D(0, 0);
        this.previousPosition = new Vector2D(0, 0);
        this.buttonsPressed = EnumSet.noneOf(MouseEvent.Button.class);
        this.buttonsJustPressed = EnumSet.noneOf(MouseEvent.Button.class);
        this.buttonsJustReleased = EnumSet.noneOf(MouseEvent.Button.class);
        this.wheelRotation = 0;
        this.dispatcher = InputEventDispatcher.getInstance();
    }
    
    public static Mouse getInstance() {
        if (instance == null) {
            instance = new Mouse();
        }
        return instance;
    }
    
    public void mouseMoved(double x, double y) {
        previousPosition = position;
        position = new Vector2D(x, y);
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_MOVED, position, previousPosition,
            null, 0, false, false, false, false
        );
        dispatcher.dispatchMouseEvent(event);
    }
    
    public void mousePressed(double x, double y, MouseEvent.Button button,
                            boolean shift, boolean ctrl, boolean alt, boolean meta) {
        previousPosition = position;
        position = new Vector2D(x, y);
        
        if (!buttonsPressed.contains(button)) {
            buttonsJustPressed.add(button);
        }
        buttonsPressed.add(button);
        buttonsJustReleased.remove(button);
        
        // Handle double/triple click detection
        long currentTime = System.currentTimeMillis();
        if (button == lastClickButton && currentTime - lastClickTime < 500) {
            clickCount++;
        } else {
            clickCount = 1;
        }
        lastClickTime = currentTime;
        lastClickButton = button;
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_PRESSED, position, previousPosition,
            button, clickCount, shift, ctrl, alt, meta
        );
        event.setClickCount(clickCount);
        dispatcher.dispatchMouseEvent(event);
    }
    
    public void mouseReleased(double x, double y, MouseEvent.Button button,
                             boolean shift, boolean ctrl, boolean alt, boolean meta) {
        previousPosition = position;
        position = new Vector2D(x, y);
        
        buttonsPressed.remove(button);
        buttonsJustPressed.remove(button);
        buttonsJustReleased.add(button);
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_RELEASED, position, previousPosition,
            button, clickCount, shift, ctrl, alt, meta
        );
        dispatcher.dispatchMouseEvent(event);
        
        // Also dispatch click event
        MouseEvent clickEvent = new MouseEvent(
            InputEvent.Type.MOUSE_CLICKED, position, previousPosition,
            button, clickCount, shift, ctrl, alt, meta
        );
        dispatcher.dispatchMouseEvent(clickEvent);
    }
    
    public void mouseClicked(double x, double y, MouseEvent.Button button,
                           boolean shift, boolean ctrl, boolean alt, boolean meta) {
        position = new Vector2D(x, y);
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_CLICKED, position, previousPosition,
            button, clickCount, shift, ctrl, alt, meta
        );
        dispatcher.dispatchMouseEvent(event);
    }
    
    public void mouseDragged(double x, double y, MouseEvent.Button button,
                           boolean shift, boolean ctrl, boolean alt, boolean meta) {
        previousPosition = position;
        position = new Vector2D(x, y);
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_DRAGGED, position, previousPosition,
            button, clickCount, shift, ctrl, alt, meta
        );
        dispatcher.dispatchMouseEvent(event);
    }
    
    public void mouseWheelMoved(double x, double y, double wheelRotation,
                               boolean shift, boolean ctrl, boolean alt, boolean meta) {
        previousPosition = position;
        position = new Vector2D(x, y);
        this.wheelRotation = wheelRotation;
        
        MouseEvent event = new MouseEvent(
            InputEvent.Type.MOUSE_WHEEL_MOVED, position, wheelRotation,
            shift, ctrl, alt, meta
        );
        dispatcher.dispatchMouseEvent(event);
    }
    
    public void update() {
        // Clear just pressed and just released states
        buttonsJustPressed.clear();
        buttonsJustReleased.clear();
        wheelRotation = 0;
    }
    
    public void reset() {
        position = new Vector2D(0, 0);
        previousPosition = new Vector2D(0, 0);
        buttonsPressed.clear();
        buttonsJustPressed.clear();
        buttonsJustReleased.clear();
        wheelRotation = 0;
        clickCount = 0;
        lastClickTime = 0;
        lastClickButton = null;
    }
    
    // Getters
    public Vector2D getPosition() { return position; }
    public Vector2D getPreviousPosition() { return previousPosition; }
    public double getX() { return position.getX(); }
    public double getY() { return position.getY(); }
    public double getPreviousX() { return previousPosition.getX(); }
    public double getPreviousY() { return previousPosition.getY(); }
    public double getWheelRotation() { return wheelRotation; }
    public int getClickCount() { return clickCount; }
    
    // Button state methods
    public boolean isButtonPressed(MouseEvent.Button button) {
        return buttonsPressed.contains(button);
    }
    
    public boolean isButtonJustPressed(MouseEvent.Button button) {
        return buttonsJustPressed.contains(button);
    }
    
    public boolean isButtonJustReleased(MouseEvent.Button button) {
        return buttonsJustReleased.contains(button);
    }
    
    // Convenience methods for common buttons
    public boolean isLeftPressed() { return isButtonPressed(MouseEvent.Button.LEFT); }
    public boolean isRightPressed() { return isButtonPressed(MouseEvent.Button.RIGHT); }
    public boolean isMiddlePressed() { return isButtonPressed(MouseEvent.Button.MIDDLE); }
    
    public boolean isLeftJustPressed() { return isButtonJustPressed(MouseEvent.Button.LEFT); }
    public boolean isRightJustPressed() { return isButtonJustPressed(MouseEvent.Button.RIGHT); }
    public boolean isMiddleJustPressed() { return isButtonJustPressed(MouseEvent.Button.MIDDLE); }
    
    public boolean isLeftJustReleased() { return isButtonJustReleased(MouseEvent.Button.LEFT); }
    public boolean isRightJustReleased() { return isButtonJustReleased(MouseEvent.Button.RIGHT); }
    public boolean isMiddleJustReleased() { return isButtonJustReleased(MouseEvent.Button.MIDDLE); }
    
    // Movement methods
    public Vector2D getDelta() {
        return position.subtract(previousPosition);
    }
    
    public double getDeltaX() {
        return position.getX() - previousPosition.getX();
    }
    
    public double getDeltaY() {
        return position.getY() - previousPosition.getY();
    }
    
    public double getDistance() {
        return getDelta().magnitude();
    }
    
    public boolean hasMoved() {
        return !position.equals(previousPosition);
    }
    
    public boolean isDragging() {
        return !buttonsPressed.isEmpty() && hasMoved();
    }
    
    public boolean isDragging(MouseEvent.Button button) {
        return isButtonPressed(button) && hasMoved();
    }
    
    // Multi-click methods
    public boolean isDoubleClick() {
        return clickCount == 2;
    }
    
    public boolean isTripleClick() {
        return clickCount == 3;
    }
    
    // Button sets
    public Set<MouseEvent.Button> getPressedButtons() {
        return EnumSet.copyOf(buttonsPressed);
    }
    
    public Set<MouseEvent.Button> getJustPressedButtons() {
        return EnumSet.copyOf(buttonsJustPressed);
    }
    
    public Set<MouseEvent.Button> getJustReleasedButtons() {
        return EnumSet.copyOf(buttonsJustReleased);
    }
    
    public boolean isAnyButtonPressed() {
        return !buttonsPressed.isEmpty();
    }
    
    public boolean isAnyButtonJustPressed() {
        return !buttonsJustPressed.isEmpty();
    }
    
    public boolean isAnyButtonJustReleased() {
        return !buttonsJustReleased.isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("Mouse{position=%s, delta=%s, pressed=%s, justPressed=%s, justReleased=%s, wheel=%.2f}",
            position, getDelta(), buttonsPressed, buttonsJustPressed, buttonsJustReleased, wheelRotation);
    }
}