package engine;

import engine.collision.*;
import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String name;
    private List<GameObject> gameObjects;
    private CollisionManager collisionManager;
    private boolean active;
    
    public Scene(String name) {
        this.name = name;
        this.gameObjects = new ArrayList<>();
        this.collisionManager = new CollisionManager(CollisionManager.SpatialIndexingMethod.QUADTREE);
        this.active = true;
    }
    
    public String getName() { return name; }
    public List<GameObject> getGameObjects() { return new ArrayList<>(gameObjects); }
    public CollisionManager getCollisionManager() { return collisionManager; }
    public boolean isActive() { return active; }
    
    public void setActive(boolean active) { this.active = active; }
    
    public void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);
        if (gameObject.getCollider() != null) {
            collisionManager.addObject(gameObject.getCollider());
        }
    }
    
    public void removeGameObject(GameObject gameObject) {
        gameObjects.remove(gameObject);
        if (gameObject.getCollider() != null) {
            collisionManager.removeObject(gameObject.getCollider());
        }
    }
    
    public GameObject findGameObjectById(String id) {
        for (GameObject gameObject : gameObjects) {
            if (gameObject.getId().equals(id)) {
                return gameObject;
            }
        }
        return null;
    }
    
    public List<GameObject> findGameObjectsByTag(String tag) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject gameObject : gameObjects) {
            TagComponent tagComponent = gameObject.getComponent(TagComponent.class);
            if (tagComponent != null && tagComponent.hasTag(tag)) {
                result.add(gameObject);
            }
        }
        return result;
    }
    
    public List<CollisionResult> getAllCollisions() {
        return collisionManager.checkAllCollisions();
    }
    
    public List<GameObject> getCollisionsWith(GameObject gameObject) {
        List<GameObject> collidingObjects = new ArrayList<>();
        for (GameObject other : gameObjects) {
            if (other != gameObject && gameObject.collidesWith(other)) {
                collidingObjects.add(other);
            }
        }
        return collidingObjects;
    }
    
    public void update(double deltaTime) {
        if (!active) return;
        
        for (GameObject gameObject : gameObjects) {
            gameObject.update(deltaTime);
        }
        
        for (GameObject gameObject : gameObjects) {
            PhysicsComponent physics = gameObject.getComponent(PhysicsComponent.class);
            if (physics != null) {
                physics.updateCollisions(this);
            }
        }
    }
    
    public void clear() {
        gameObjects.clear();
        collisionManager.clear();
    }
    
    public int getGameObjectCount() {
        return gameObjects.size();
    }
}