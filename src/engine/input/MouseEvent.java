package engine.input;

import engine.collision.Vector2D;

public class MouseEvent extends InputEvent {
    public enum Button {
        LEFT(1), MIDDLE(2), RIGHT(3), BACK(4), FORWARD(5);
        
        private final int buttonId;
        
        Button(int buttonId) {
            this.buttonId = buttonId;
        }
        
        public int getButtonId() {
            return buttonId;
        }
        
        public static Button fromId(int buttonId) {
            for (Button button : values()) {
                if (button.buttonId == buttonId) {
                    return button;
                }
            }
            return null;
        }
    }
    
    private Vector2D position;
    private Vector2D previousPosition;
    private Button button;
    private int clickCount;
    private boolean shiftPressed;
    private boolean ctrlPressed;
    private boolean altPressed;
    private boolean metaPressed;
    private double wheelRotation;
    
    public MouseEvent(Type type, Vector2D position) {
        super(type);
        this.position = position;
        this.previousPosition = position;
        this.clickCount = 0;
        this.wheelRotation = 0;
    }
    
    public MouseEvent(Type type, Vector2D position, Button button, 
                    boolean shiftPressed, boolean ctrlPressed, 
                    boolean altPressed, boolean metaPressed) {
        super(type);
        this.position = position;
        this.previousPosition = position;
        this.button = button;
        this.clickCount = 1;
        this.shiftPressed = shiftPressed;
        this.ctrlPressed = ctrlPressed;
        this.altPressed = altPressed;
        this.metaPressed = metaPressed;
        this.wheelRotation = 0;
    }
    
    public MouseEvent(Type type, Vector2D position, Vector2D previousPosition,
                    Button button, int clickCount,
                    boolean shiftPressed, boolean ctrlPressed, 
                    boolean altPressed, boolean metaPressed) {
        super(type);
        this.position = position;
        this.previousPosition = previousPosition;
        this.button = button;
        this.clickCount = clickCount;
        this.shiftPressed = shiftPressed;
        this.ctrlPressed = ctrlPressed;
        this.altPressed = altPressed;
        this.metaPressed = metaPressed;
        this.wheelRotation = 0;
    }
    
    public MouseEvent(Type type, Vector2D position, double wheelRotation,
                    boolean shiftPressed, boolean ctrlPressed, 
                    boolean altPressed, boolean metaPressed) {
        super(type);
        this.position = position;
        this.previousPosition = position;
        this.wheelRotation = wheelRotation;
        this.shiftPressed = shiftPressed;
        this.ctrlPressed = ctrlPressed;
        this.altPressed = altPressed;
        this.metaPressed = metaPressed;
    }
    
    // Getters
    public Vector2D getPosition() { return position; }
    public Vector2D getPreviousPosition() { return previousPosition; }
    public Button getButton() { return button; }
    public int getClickCount() { return clickCount; }
    public boolean isShiftPressed() { return shiftPressed; }
    public boolean isCtrlPressed() { return ctrlPressed; }
    public boolean isAltPressed() { return altPressed; }
    public boolean isMetaPressed() { return metaPressed; }
    public double getWheelRotation() { return wheelRotation; }
    
    // Setters
    public void setClickCount(int clickCount) { this.clickCount = clickCount; }
    
    // Convenience methods
    public boolean is(Button button) {
        return this.button == button;
    }
    
    public boolean isModifierPressed() {
        return shiftPressed || ctrlPressed || altPressed || metaPressed;
    }
    
    public Vector2D getDelta() {
        return position.subtract(previousPosition);
    }
    
    public double getDistance() {
        return getDelta().magnitude();
    }
    
    @Override
    public String toString() {
        return String.format("MouseEvent{type=%s, position=%s, button=%s, clickCount=%d, wheel=%.2f, modifiers=[shift=%s, ctrl=%s, alt=%s, meta=%s]}",
            getType(), position, button, clickCount, wheelRotation, shiftPressed, ctrlPressed, altPressed, metaPressed);
    }
}