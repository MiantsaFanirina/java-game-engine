package engine.input;

public class InputEvent {
    public enum Type {
        KEY_PRESSED,
        KEY_RELEASED,
        KEY_TYPED,
        MOUSE_PRESSED,
        MOUSE_RELEASED,
        MOUSE_CLICKED,
        MOUSE_MOVED,
        MOUSE_DRAGGED,
        MOUSE_WHEEL_MOVED
    }
    
    private Type type;
    private long timestamp;
    private boolean consumed;
    
    public InputEvent(Type type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
        this.consumed = false;
    }
    
    public Type getType() { return type; }
    public long getTimestamp() { return timestamp; }
    public boolean isConsumed() { return consumed; }
    
    public void consume() { consumed = true; }
    
    @Override
    public String toString() {
        return String.format("InputEvent{type=%s, timestamp=%d, consumed=%s}", 
            type, timestamp, consumed);
    }
}