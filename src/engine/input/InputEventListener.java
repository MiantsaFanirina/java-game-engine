package engine.input;

public interface InputEventListener {
    void onInputEvent(InputEvent event);
    
    default boolean canHandle(InputEvent event) {
        return true;
    }
    
    default int getPriority() {
        return 0;
    }
}