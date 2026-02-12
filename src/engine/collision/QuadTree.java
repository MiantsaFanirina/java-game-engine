package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class QuadTree {
    private static final int MAX_OBJECTS = 10;
    private static final int MAX_LEVELS = 5;
    
    private int level;
    private List<Geometry> objects;
    private BoundingBox bounds;
    private QuadTree[] nodes;
    
    public QuadTree(int level, BoundingBox bounds) {
        this.level = level;
        this.objects = new ArrayList<>();
        this.bounds = bounds;
        this.nodes = new QuadTree[4];
    }
    
    public void clear() {
        objects.clear();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }
    
    private void split() {
        double subWidth = bounds.getWidth() / 2;
        double subHeight = bounds.getHeight() / 2;
        double x = bounds.getMinX();
        double y = bounds.getMinY();
        
        nodes[0] = new QuadTree(level + 1, new BoundingBox(x, y, x + subWidth, y + subHeight));
        nodes[1] = new QuadTree(level + 1, new BoundingBox(x + subWidth, y, bounds.getMaxX(), y + subHeight));
        nodes[2] = new QuadTree(level + 1, new BoundingBox(x, y + subHeight, x + subWidth, bounds.getMaxY()));
        nodes[3] = new QuadTree(level + 1, new BoundingBox(x + subWidth, y + subHeight, bounds.getMaxX(), bounds.getMaxY()));
    }
    
    private int getIndex(Geometry geom) {
        BoundingBox geomBounds = geom.getBoundingBox();
        int index = -1;
        double verticalMidpoint = bounds.getMinX() + bounds.getWidth() / 2;
        double horizontalMidpoint = bounds.getMinY() + bounds.getHeight() / 2;
        
        boolean topQuadrant = geomBounds.getMaxY() < horizontalMidpoint;
        boolean bottomQuadrant = geomBounds.getMinY() > horizontalMidpoint;
        
        if (geomBounds.getMaxX() < verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            } else if (bottomQuadrant) {
                index = 2;
            }
        } else if (geomBounds.getMinX() > verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            } else if (bottomQuadrant) {
                index = 3;
            }
        }
        
        return index;
    }
    
    public void insert(Geometry geom) {
        if (nodes[0] != null) {
            int index = getIndex(geom);
            if (index != -1) {
                nodes[index].insert(geom);
                return;
            }
        }
        
        objects.add(geom);
        
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }
            
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    nodes[index].insert(objects.remove(i));
                } else {
                    i++;
                }
            }
        }
    }
    
    public List<Geometry> retrieve(List<Geometry> returnObjects, Geometry geom) {
        int index = getIndex(geom);
        if (nodes[0] != null) {
            if (index != -1) {
                nodes[index].retrieve(returnObjects, geom);
            } else {
                for (QuadTree node : nodes) {
                    if (node != null) {
                        node.retrieve(returnObjects, geom);
                    }
                }
            }
        }
        
        returnObjects.addAll(objects);
        return returnObjects;
    }
    
    public List<CollisionResult> checkCollisions() {
        List<CollisionResult> collisions = new ArrayList<>();
        List<Geometry> checkObjects = new ArrayList<>();
        
        for (int i = 0; i < objects.size(); i++) {
            checkObjects.clear();
            retrieve(checkObjects, objects.get(i));
            
            for (Geometry other : checkObjects) {
                if (objects.get(i) != other) {
                    CollisionResult result = CollisionDetector.checkCollision(objects.get(i), other);
                    if (result.isColliding()) {
                        collisions.add(result);
                    }
                }
            }
        }
        
        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                if (node != null) {
                    collisions.addAll(node.checkCollisions());
                }
            }
        }
        
        return collisions;
    }
}