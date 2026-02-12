package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class CollisionManager {
    private List<Geometry> objects;
    private SpatialIndexingMethod indexingMethod;
    private Object spatialIndex;
    
    public enum SpatialIndexingMethod {
        NONE,
        QUADTREE,
        SPATIAL_HASH
    }
    
    public CollisionManager() {
        this.objects = new ArrayList<>();
        this.indexingMethod = SpatialIndexingMethod.NONE;
    }
    
    public CollisionManager(SpatialIndexingMethod method) {
        this.objects = new ArrayList<>();
        this.indexingMethod = method;
        initializeSpatialIndex();
    }
    
    private void initializeSpatialIndex() {
        switch (indexingMethod) {
            case QUADTREE:
                spatialIndex = new QuadTree(0, new BoundingBox(-1000, -1000, 1000, 1000));
                break;
            case SPATIAL_HASH:
                spatialIndex = new SpatialHashGrid(50, 40, 40);
                break;
            default:
                spatialIndex = null;
        }
    }
    
    public void addObject(Geometry geom) {
        objects.add(geom);
        if (spatialIndex != null) {
            switch (indexingMethod) {
                case QUADTREE:
                    ((QuadTree) spatialIndex).insert(geom);
                    break;
                case SPATIAL_HASH:
                    ((SpatialHashGrid) spatialIndex).addObject(geom);
                    break;
            }
        }
    }
    
    public void removeObject(Geometry geom) {
        objects.remove(geom);
        if (spatialIndex != null && indexingMethod == SpatialIndexingMethod.SPATIAL_HASH) {
            ((SpatialHashGrid) spatialIndex).removeObject(geom);
        }
    }
    
    public void clear() {
        objects.clear();
        if (spatialIndex != null) {
            switch (indexingMethod) {
                case QUADTREE:
                    ((QuadTree) spatialIndex).clear();
                    break;
                case SPATIAL_HASH:
                    ((SpatialHashGrid) spatialIndex).clear();
                    break;
            }
        }
    }
    
    public List<CollisionResult> checkAllCollisions() {
        switch (indexingMethod) {
            case QUADTREE:
                return ((QuadTree) spatialIndex).checkCollisions();
            case SPATIAL_HASH:
                if (spatialIndex != null) {
                    ((SpatialHashGrid) spatialIndex).update();
                }
                return ((SpatialHashGrid) spatialIndex).checkAllCollisions();
            default:
                return checkCollisionsBruteForce();
        }
    }
    
    private List<CollisionResult> checkCollisionsBruteForce() {
        List<CollisionResult> collisions = new ArrayList<>();
        
        for (int i = 0; i < objects.size(); i++) {
            for (int j = i + 1; j < objects.size(); j++) {
                CollisionResult result = CollisionDetector.checkCollision(objects.get(i), objects.get(j));
                if (result.isColliding()) {
                    collisions.add(result);
                }
            }
        }
        
        return collisions;
    }
    
    public List<Geometry> getPotentialCollisions(Geometry geom) {
        if (spatialIndex != null) {
            switch (indexingMethod) {
                case QUADTREE:
                    List<Geometry> potentials = new ArrayList<>();
                    ((QuadTree) spatialIndex).retrieve(potentials, geom);
                    return potentials;
                case SPATIAL_HASH:
                    return ((SpatialHashGrid) spatialIndex).getPotentialCollisions(geom);
            }
        }
        
        List<Geometry> allOthers = new ArrayList<>(objects);
        allOthers.remove(geom);
        return allOthers;
    }
    
    public CollisionResult checkCollision(Geometry geom1, Geometry geom2) {
        return CollisionDetector.checkCollision(geom1, geom2);
    }
    
    public boolean raycast(Vector2D origin, Vector2D direction, double maxDistance) {
        Line ray = new Line(origin, origin.add(direction.normalize().multiply(maxDistance)));
        
        for (Geometry geom : objects) {
            if (geom.intersects(ray)) {
                return true;
            }
        }
        
        return false;
    }
    
    public void setIndexingMethod(SpatialIndexingMethod method) {
        if (this.indexingMethod != method) {
            this.indexingMethod = method;
            initializeSpatialIndex();
            
            for (Geometry geom : objects) {
                switch (method) {
                    case QUADTREE:
                        ((QuadTree) spatialIndex).insert(geom);
                        break;
                    case SPATIAL_HASH:
                        ((SpatialHashGrid) spatialIndex).addObject(geom);
                        break;
                }
            }
        }
    }
    
    public int getObjectCount() {
        return objects.size();
    }
    
    public CollisionManager.SpatialIndexingMethod getSpatialIndexingMethod() {
        return indexingMethod;
    }
}