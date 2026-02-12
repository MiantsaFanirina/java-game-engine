package engine.collision;

public class CollisionResult {
    private boolean colliding;
    private Vector2D penetrationVector;
    private double penetrationDepth;
    private Vector2D contactPoint;
    
    public CollisionResult(boolean colliding) {
        this.colliding = colliding;
        this.penetrationVector = new Vector2D(0, 0);
        this.penetrationDepth = 0;
        this.contactPoint = null;
    }
    
    public CollisionResult(boolean colliding, Vector2D penetrationVector, 
                          double penetrationDepth, Vector2D contactPoint) {
        this.colliding = colliding;
        this.penetrationVector = penetrationVector;
        this.penetrationDepth = penetrationDepth;
        this.contactPoint = contactPoint;
    }
    
    public boolean isColliding() { return colliding; }
    public Vector2D getPenetrationVector() { return penetrationVector; }
    public double getPenetrationDepth() { return penetrationDepth; }
    public Vector2D getContactPoint() { return contactPoint; }
    
    public void setPenetrationVector(Vector2D penetrationVector) { 
        this.penetrationVector = penetrationVector; 
    }
    
    public void setPenetrationDepth(double penetrationDepth) { 
        this.penetrationDepth = penetrationDepth; 
    }
    
    public void setContactPoint(Vector2D contactPoint) { 
        this.contactPoint = contactPoint; 
    }
}