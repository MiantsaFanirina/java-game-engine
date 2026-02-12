package engine.input;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InputEventDispatcher {
    private List<InputEventListener> listeners;
    private static InputEventDispatcher instance;
    
    private InputEventDispatcher() {
        this.listeners = new CopyOnWriteArrayList<>();
    }
    
    public static InputEventDispatcher getInstance() {
        if (instance == null) {
            instance = new InputEventDispatcher();
        }
        return instance;
    }
    
    public void addListener(InputEventListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
            // Sort by priority (higher priority = earlier execution)
            listeners.sort(Comparator.comparingInt(InputEventListener::getPriority).reversed());
        }
    }
    
    public void removeListener(InputEventListener listener) {
        listeners.remove(listener);
    }
    
    public void clearListeners() {
        listeners.clear();
    }
    
    public void dispatchEvent(InputEvent event) {
        if (event == null || event.isConsumed()) {
            return;
        }
        
        for (InputEventListener listener : listeners) {
            if (event.isConsumed()) {
                break;
            }
            
            if (listener.canHandle(event)) {
                try {
                    listener.onInputEvent(event);
                } catch (Exception e) {
                    System.err.println("Error in input event listener: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void dispatchKeyEvent(KeyEvent event) {
        dispatchEvent(event);
    }
    
    public void dispatchMouseEvent(MouseEvent event) {
        dispatchEvent(event);
    }
    
    public List<InputEventListener> getListeners() {
        return new ArrayList<>(listeners);
    }
    
    public int getListenerCount() {
        return listeners.size();
    }
}