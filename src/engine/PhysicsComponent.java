package engine;

import engine.collision.*;
import java.util.ArrayList;
import java.util.List;

public class PhysicsComponent extends Component {
    private double mass;
    private double friction;
    private double restitution;
    private boolean useGravity;
    private Vector2D gravity;
    private boolean isStatic;
    
    public PhysicsComponent() {
        this.mass = 1.0;
        this.friction = 0.1;
        this.restitution = 0.5;
        this.useGravity = true;
        this.gravity = new Vector2D(0, 9.81);
        this.isStatic = false;
    }
    
    public double getMass() { return mass; }
    public double getFriction() { return friction; }
    public double getRestitution() { return restitution; }
    public boolean isUseGravity() { return useGravity; }
    public Vector2D getGravity() { return gravity; }
    public boolean isStatic() { return isStatic; }
    
    public void setMass(double mass) { this.mass = mass; }
    public void setFriction(double friction) { this.friction = friction; }
    public void setRestitution(double restitution) { this.restitution = restitution; }
    public void setUseGravity(boolean useGravity) { this.useGravity = useGravity; }
    public void setGravity(Vector2D gravity) { this.gravity = gravity; }
    public void setStatic(boolean isStatic) { this.isStatic = isStatic; }
    
    @Override
    public void update(double deltaTime) {
        if (gameObject == null || isStatic) return;
        
        Vector2D currentVelocity = gameObject.getVelocity();
        
        if (useGravity) {
            Vector2D gravityForce = gravity.multiply(mass);
            Vector2D acceleration = gravityForce.multiply(1.0 / mass);
            currentVelocity = currentVelocity.add(acceleration.multiply(deltaTime));
        }
        
        Vector2D frictionForce = currentVelocity.multiply(-friction);
        currentVelocity = currentVelocity.add(frictionForce.multiply(deltaTime));
        
        gameObject.setVelocity(currentVelocity);
    }
    
    public void updateCollisions(Scene scene) {
        if (gameObject == null || gameObject.getCollider() == null) return;
        
        List<GameObject> collidingObjects = scene.getCollisionsWith(gameObject);
        
        for (GameObject other : collidingObjects) {
            CollisionResult collision = gameObject.getCollisionWith(other);
            if (collision.isColliding()) {
                resolveCollision(other, collision);
            }
        }
    }
    
    private void resolveCollision(GameObject other, CollisionResult collision) {
        if (isStatic) return;
        
        PhysicsComponent otherPhysics = other.getComponent(PhysicsComponent.class);
        
        Vector2D penetration = collision.getPenetrationVector();
        if (penetration != null && (penetration.getX() != 0 || penetration.getY() != 0)) {
            gameObject.setPosition(gameObject.getPosition().add(penetration));
        }
        
        if (otherPhysics == null || otherPhysics.isStatic()) {
            Vector2D velocity = gameObject.getVelocity();
            Vector2D normal = collision.getPenetrationVector().normalize();
            
            double dotProduct = velocity.dot(normal);
            Vector2D reflection = normal.multiply(-2 * dotProduct * restitution);
            gameObject.setVelocity(velocity.add(reflection));
        } else {
            Vector2D relativeVelocity = gameObject.getVelocity().subtract(other.getVelocity());
            Vector2D normal = collision.getPenetrationVector().normalize();
            double velocityAlongNormal = relativeVelocity.dot(normal);
            
            if (velocityAlongNormal > 0) return;
            
            double e = Math.min(restitution, otherPhysics.getRestitution());
            double j = -(1 + e) * velocityAlongNormal;
            j /= 1/mass + 1/otherPhysics.getMass();
            
            Vector2D impulse = normal.multiply(j);
            gameObject.setVelocity(gameObject.getVelocity().add(impulse.multiply(1/mass)));
            
            if (!otherPhysics.isStatic()) {
                other.setVelocity(other.getVelocity().subtract(impulse.multiply(1/otherPhysics.getMass())));
            }
        }
    }
    
    @Override
    public void start() {
        // Physics initialization if needed
    }
    
    @Override
    public void destroy() {
        // Physics cleanup if needed
    }
}