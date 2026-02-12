package engine;

import engine.collision.*;
import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String id;
    private Vector2D position;
    private Vector2D velocity;
    private boolean active;
    private Geometry collider;
    private List<Component> components;
    
    public GameObject(String id) {
        this.id = id;
        this.position = new Vector2D(0, 0);
        this.velocity = new Vector2D(0, 0);
        this.active = true;
        this.components = new ArrayList<>();
    }
    
    public GameObject(String id, Geometry collider) {
        this(id);
        this.collider = collider;
    }
    
    public String getId() { return id; }
    public Vector2D getPosition() { return position; }
    public Vector2D getVelocity() { return velocity; }
    public boolean isActive() { return active; }
    public Geometry getCollider() { return collider; }
    public List<Component> getComponents() { return new ArrayList<>(components); }
    
    public void setPosition(Vector2D position) { 
        this.position = position;
        if (collider != null) {
            updateColliderPosition();
        }
    }
    
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }
    public void setActive(boolean active) { this.active = active; }
    public void setCollider(Geometry collider) { 
        this.collider = collider;
        updateColliderPosition();
    }
    
    public void addComponent(Component component) {
        components.add(component);
        component.setGameObject(this);
    }
    
    public void removeComponent(Component component) {
        components.remove(component);
        component.setGameObject(null);
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> type) {
        for (Component component : components) {
            if (type.isInstance(component)) {
                return (T) component;
            }
        }
        return null;
    }
    
    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }
    
    public void update(double deltaTime) {
        if (!active) return;
        
        for (Component component : components) {
            component.update(deltaTime);
        }
        
        position = position.add(velocity.multiply(deltaTime));
        updateColliderPosition();
    }
    
    private void updateColliderPosition() {
        if (collider != null) {
            Vector2D centerOffset = position.subtract(collider.getCenter());
            collider.translate(centerOffset);
        }
    }
    
    public boolean collidesWith(GameObject other) {
        if (!active || !other.active || collider == null || other.collider == null) {
            return false;
        }
        return collider.intersects(other.collider);
    }
    
    public CollisionResult getCollisionWith(GameObject other) {
        if (!active || !other.active || collider == null || other.collider == null) {
            return new CollisionResult(false);
        }
        return CollisionDetector.checkCollision(collider, other.collider);
    }
    
    @Override
    public String toString() {
        return String.format("GameObject{id='%s', pos=%s, active=%s}", id, position, active);
    }
}