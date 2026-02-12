package engine.collision;

public class BoundingBox {
    private double minX, minY, maxX, maxY;
    
    public BoundingBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }
    
    public double getMinX() { return minX; }
    public double getMinY() { return minY; }
    public double getMaxX() { return maxX; }
    public double getMaxY() { return maxY; }
    
    public double getWidth() { return maxX - minX; }
    public double getHeight() { return maxY - minY; }
    
    public boolean intersects(BoundingBox other) {
        return !(maxX < other.minX || minX > other.maxX || 
                 maxY < other.minY || minY > other.maxY);
    }
    
    public boolean containsPoint(Vector2D point) {
        return point.getX() >= minX && point.getX() <= maxX &&
               point.getY() >= minY && point.getY() <= maxY;
    }
    
    public BoundingBox union(BoundingBox other) {
        return new BoundingBox(
            Math.min(minX, other.minX),
            Math.min(minY, other.minY),
            Math.max(maxX, other.maxX),
            Math.max(maxY, other.maxY)
        );
    }
    
    public BoundingBox expand(double amount) {
        return new BoundingBox(
            minX - amount,
            minY - amount,
            maxX + amount,
            maxY + amount
        );
    }
}