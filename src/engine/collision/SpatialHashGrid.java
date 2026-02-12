package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class SpatialHashGrid {
    private double cellSize;
    private int[][] grid;
    private List<Geometry> objects;
    private int width;
    private int height;
    
    public SpatialHashGrid(double cellSize, int gridWidth, int gridHeight) {
        this.cellSize = cellSize;
        this.width = gridWidth;
        this.height = gridHeight;
        this.grid = new int[gridWidth][gridHeight];
        this.objects = new ArrayList<>();
    }
    
    public void addObject(Geometry obj) {
        objects.add(obj);
    }
    
    public void removeObject(Geometry obj) {
        objects.remove(obj);
    }
    
    public void clear() {
        objects.clear();
        grid = new int[width][height];
    }
    
    public void update() {
        grid = new int[width][height];
        for (int i = 0; i < objects.size(); i++) {
            insertObject(objects.get(i), i);
        }
    }
    
    private void insertObject(Geometry obj, int index) {
        BoundingBox bounds = obj.getBoundingBox();
        int startX = Math.max(0, (int) (bounds.getMinX() / cellSize));
        int endX = Math.min(width - 1, (int) (bounds.getMaxX() / cellSize));
        int startY = Math.max(0, (int) (bounds.getMinY() / cellSize));
        int endY = Math.min(height - 1, (int) (bounds.getMaxY() / cellSize));
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                grid[x][y] = index + 1;
            }
        }
    }
    
    public List<Geometry> getPotentialCollisions(Geometry obj) {
        List<Geometry> potentials = new ArrayList<>();
        BoundingBox bounds = obj.getBoundingBox();
        
        int startX = Math.max(0, (int) (bounds.getMinX() / cellSize));
        int endX = Math.min(width - 1, (int) (bounds.getMaxX() / cellSize));
        int startY = Math.max(0, (int) (bounds.getMinY() / cellSize));
        int endY = Math.min(height - 1, (int) (bounds.getMaxY() / cellSize));
        
        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                int index = grid[x][y] - 1;
                if (index >= 0 && index < objects.size()) {
                    Geometry candidate = objects.get(index);
                    if (candidate != obj && !potentials.contains(candidate)) {
                        potentials.add(candidate);
                    }
                }
            }
        }
        
        return potentials;
    }
    
    public List<CollisionResult> checkAllCollisions() {
        List<CollisionResult> collisions = new ArrayList<>();
        update();
        
        for (int i = 0; i < objects.size(); i++) {
            Geometry obj1 = objects.get(i);
            List<Geometry> potentials = getPotentialCollisions(obj1);
            
            for (Geometry obj2 : potentials) {
                CollisionResult result = CollisionDetector.checkCollision(obj1, obj2);
                if (result.isColliding()) {
                    collisions.add(result);
                }
            }
        }
        
        return collisions;
    }
}