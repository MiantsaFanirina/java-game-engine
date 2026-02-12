package engine.input;

import engine.collision.Vector2D;
import java.util.ArrayList;
import java.util.List;

public class InputManager {
    private static InputManager instance;
    private Keyboard keyboard;
    private Mouse mouse;
    private InputEventDispatcher dispatcher;
    private List<InputEventListener> listeners;
    private boolean initialized;
    
    private InputManager() {
        this.keyboard = Keyboard.getInstance();
        this.mouse = Mouse.getInstance();
        this.dispatcher = InputEventDispatcher.getInstance();
        this.listeners = new ArrayList<>();
        this.initialized = false;
    }
    
    public static InputManager getInstance() {
        if (instance == null) {
            instance = new InputManager();
        }
        return instance;
    }
    
    public void initialize() {
        if (initialized) {
            return;
        }
        
        // Reset all input states
        keyboard.reset();
        mouse.reset();
        
        initialized = true;
    }
    
    public void update() {
        if (!initialized) {
            return;
        }
        
        // Update input states (clear just pressed/released states)
        keyboard.update();
        mouse.update();
    }
    
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        // Clear all listeners
        dispatcher.clearListeners();
        listeners.clear();
        
        // Reset input states
        keyboard.reset();
        mouse.reset();
        
        initialized = false;
    }
    
    // Listener management
    public void addListener(InputEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            dispatcher.addListener(listener);
        }
    }
    
    public void removeListener(InputEventListener listener) {
        listeners.remove(listener);
        dispatcher.removeListener(listener);
    }
    
    public void clearListeners() {
        listeners.clear();
        dispatcher.clearListeners();
    }
    
    // Access to input devices
    public Keyboard getKeyboard() {
        return keyboard;
    }
    
    public Mouse getMouse() {
        return mouse;
    }
    
    // Event dispatching
    public void dispatchEvent(InputEvent event) {
        dispatcher.dispatchEvent(event);
    }
    
    public void dispatchKeyEvent(KeyEvent event) {
        dispatcher.dispatchKeyEvent(event);
    }
    
    public void dispatchMouseEvent(MouseEvent event) {
        dispatcher.dispatchMouseEvent(event);
    }
    
    // Convenience methods for keyboard
    public boolean isKeyPressed(KeyEvent.KeyCode key) {
        return keyboard.isKeyPressed(key);
    }
    
    public boolean isKeyJustPressed(KeyEvent.KeyCode key) {
        return keyboard.isKeyJustPressed(key);
    }
    
    public boolean isKeyJustReleased(KeyEvent.KeyCode key) {
        return keyboard.isKeyJustReleased(key);
    }
    
    // Convenience methods for mouse
    public Vector2D getMousePosition() {
        return mouse.getPosition();
    }
    
    public boolean isMouseButtonPressed(MouseEvent.Button button) {
        return mouse.isButtonPressed(button);
    }
    
    public boolean isMouseButtonJustPressed(MouseEvent.Button button) {
        return mouse.isButtonJustPressed(button);
    }
    
    public boolean isMouseButtonJustReleased(MouseEvent.Button button) {
        return mouse.isButtonJustReleased(button);
    }
    
    public Vector2D getMouseDelta() {
        return mouse.getDelta();
    }
    
    // State information
    public boolean isInitialized() {
        return initialized;
    }
    
    public int getListenerCount() {
        return listeners.size();
    }
    
    // Utility methods
    public boolean hasAnyInput() {
        return hasAnyKeyPress() || hasAnyMouseInput();
    }
    
    public boolean hasAnyKeyPress() {
        // This is a simplified check - in practice you might want to track this differently
        return false; // Would need to be implemented in Keyboard class
    }
    
    public boolean hasAnyMouseInput() {
        return mouse.hasMoved() || mouse.isAnyButtonPressed() || 
               mouse.isAnyButtonJustPressed() || mouse.isAnyButtonJustReleased();
    }
    
    // Input mapping support
    public interface InputAction {
        void execute();
    }
    
    private class KeyMapping {
        KeyEvent.KeyCode keyCode;
        InputAction action;
        
        KeyMapping(KeyEvent.KeyCode keyCode, InputAction action) {
            this.keyCode = keyCode;
            this.action = action;
        }
        
        boolean checkAndExecute() {
            if (keyboard.isKeyJustPressed(keyCode)) {
                action.execute();
                return true;
            }
            return false;
        }
    }
    
    private List<KeyMapping> keyMappings = new ArrayList<>();
    
    public void mapKey(KeyEvent.KeyCode key, InputAction action) {
        keyMappings.add(new KeyMapping(key, action));
    }
    
    public void mapKeys(KeyEvent.KeyCode[] keys, InputAction action) {
        for (KeyEvent.KeyCode key : keys) {
            mapKey(key, action);
        }
    }
    
    public void clearKeyMappings() {
        keyMappings.clear();
    }
    
    public void updateKeyMappings() {
        for (KeyMapping mapping : keyMappings) {
            mapping.checkAndExecute();
        }
    }
    
    // Update method that includes key mappings
    public void updateWithMappings() {
        update();
        updateKeyMappings();
    }
    
    @Override
    public String toString() {
        return String.format("InputManager{initialized=%s, listeners=%d, keyboard=%s, mouse=%s}",
            initialized, listeners.size(), keyboard, mouse);
    }
}