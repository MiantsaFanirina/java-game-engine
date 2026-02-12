package engine.collision;

import java.util.List;

public class CollisionDetector {
    
    public static CollisionResult checkCollision(Geometry geom1, Geometry geom2) {
        // Handle basic shape combinations
        if (geom1.getType() == GeometryType.CIRCLE && geom2.getType() == GeometryType.CIRCLE) {
            return checkCircleCircle((Circle) geom1, (Circle) geom2);
        } else if (geom1.getType() == GeometryType.RECTANGLE && geom2.getType() == GeometryType.RECTANGLE) {
            return checkRectangleRectangle((Rectangle) geom1, (Rectangle) geom2);
        } else if ((geom1.getType() == GeometryType.CIRCLE && geom2.getType() == GeometryType.RECTANGLE) ||
                   (geom1.getType() == GeometryType.RECTANGLE && geom2.getType() == GeometryType.CIRCLE)) {
            Circle circle = geom1.getType() == GeometryType.CIRCLE ? (Circle) geom1 : (Circle) geom2;
            Rectangle rect = geom1.getType() == GeometryType.RECTANGLE ? (Rectangle) geom1 : (Rectangle) geom2;
            return checkCircleRectangle(circle, rect);
        } else if (geom1.getType() == GeometryType.LINE || geom2.getType() == GeometryType.LINE) {
            Line line = geom1.getType() == GeometryType.LINE ? (Line) geom1 : (Line) geom2;
            Geometry other = geom1.getType() == GeometryType.LINE ? geom2 : geom1;
            return checkLineGeometry(line, other);
        } else if (geom1.getType() == GeometryType.POLYGON || geom2.getType() == GeometryType.POLYGON) {
            return checkPolygonGeometry((Polygon) (geom1.getType() == GeometryType.POLYGON ? geom1 : geom2),
                                      geom1.getType() == GeometryType.POLYGON ? geom2 : geom1);
        }
        
        // Handle complex shape combinations
        if (geom1.getType() == GeometryType.ELLIPSE || geom2.getType() == GeometryType.ELLIPSE) {
            Ellipse ellipse = geom1.getType() == GeometryType.ELLIPSE ? (Ellipse) geom1 : (Ellipse) geom2;
            Geometry other = geom1.getType() == GeometryType.ELLIPSE ? geom2 : geom1;
            return checkEllipseGeometry(ellipse, other);
        }
        
        if (geom1.getType() == GeometryType.CURVE || geom2.getType() == GeometryType.CURVE) {
            Curve curve = geom1.getType() == GeometryType.CURVE ? (Curve) geom1 : (Curve) geom2;
            Geometry other = geom1.getType() == GeometryType.CURVE ? geom2 : geom1;
            return checkCurveGeometry(curve, other);
        }
        
        if (geom1.getType() == GeometryType.COMPOSITE || geom2.getType() == GeometryType.COMPOSITE) {
            CompositeShape composite = geom1.getType() == GeometryType.COMPOSITE ? (CompositeShape) geom1 : (CompositeShape) geom2;
            Geometry other = geom1.getType() == GeometryType.COMPOSITE ? geom2 : geom1;
            return checkCompositeGeometry(composite, other);
        }
        
        if (geom1.getType() == GeometryType.FREEFORM || geom2.getType() == GeometryType.FREEFORM) {
            FreeForm freeForm = geom1.getType() == GeometryType.FREEFORM ? (FreeForm) geom1 : (FreeForm) geom2;
            Geometry other = geom1.getType() == GeometryType.FREEFORM ? geom2 : geom1;
            return checkFreeFormGeometry(freeForm, other);
        }
        
        // Fallback to basic intersection
        return new CollisionResult(geom1.intersects(geom2));
    }
    
    private static CollisionResult checkCircleCircle(Circle c1, Circle c2) {
        Vector2D centerDiff = c2.getCenter().subtract(c1.getCenter());
        double distance = centerDiff.magnitude();
        double radiusSum = c1.getRadius() + c2.getRadius();
        
        boolean colliding = distance <= radiusSum;
        if (!colliding) return new CollisionResult(false);
        
        if (distance == 0) {
            return new CollisionResult(true, new Vector2D(1, 0), radiusSum, c1.getCenter());
        }
        
        Vector2D normal = centerDiff.normalize();
        Vector2D penetrationVector = normal.multiply(radiusSum - distance);
        Vector2D contactPoint = c1.getCenter().add(normal.multiply(c1.getRadius()));
        
        return new CollisionResult(true, penetrationVector, radiusSum - distance, contactPoint);
    }
    
    private static CollisionResult checkRectangleRectangle(Rectangle r1, Rectangle r2) {
        boolean colliding = r1.intersects(r2);
        if (!colliding) return new CollisionResult(false);
        
        double overlapX = Math.min(r1.getRight(), r2.getRight()) - Math.max(r1.getLeft(), r2.getLeft());
        double overlapY = Math.min(r1.getBottom(), r2.getBottom()) - Math.max(r1.getTop(), r2.getTop());
        
        Vector2D penetrationVector;
        double penetrationDepth;
        
        if (overlapX < overlapY) {
            penetrationDepth = overlapX;
            double direction = r1.getCenter().getX() < r2.getCenter().getX() ? -1 : 1;
            penetrationVector = new Vector2D(direction * penetrationDepth, 0);
        } else {
            penetrationDepth = overlapY;
            double direction = r1.getCenter().getY() < r2.getCenter().getY() ? -1 : 1;
            penetrationVector = new Vector2D(0, direction * penetrationDepth);
        }
        
        Vector2D contactPoint = new Vector2D(
            Math.max(r1.getLeft(), r2.getLeft()) + overlapX / 2,
            Math.max(r1.getTop(), r2.getTop()) + overlapY / 2
        );
        
        return new CollisionResult(true, penetrationVector, penetrationDepth, contactPoint);
    }
    
    private static CollisionResult checkCircleRectangle(Circle circle, Rectangle rect) {
        Vector2D rectCenter = rect.getCenter();
        Vector2D circleCenter = circle.getCenter();
        
        double closestX = Math.max(rect.getLeft(), Math.min(circleCenter.getX(), rect.getRight()));
        double closestY = Math.max(rect.getTop(), Math.min(circleCenter.getY(), rect.getBottom()));
        
        Vector2D closestPoint = new Vector2D(closestX, closestY);
        double distance = circleCenter.distanceTo(closestPoint);
        
        boolean colliding = distance <= circle.getRadius();
        if (!colliding) return new CollisionResult(false);
        
        Vector2D normal;
        if (distance == 0) {
            double distLeft = Math.abs(circleCenter.getX() - rect.getLeft());
            double distRight = Math.abs(circleCenter.getX() - rect.getRight());
            double distTop = Math.abs(circleCenter.getY() - rect.getTop());
            double distBottom = Math.abs(circleCenter.getY() - rect.getBottom());
            
            double minDist = Math.min(Math.min(distLeft, distRight), Math.min(distTop, distBottom));
            if (minDist == distLeft || minDist == distRight) {
                normal = new Vector2D(minDist == distLeft ? -1 : 1, 0);
            } else {
                normal = new Vector2D(0, minDist == distTop ? -1 : 1);
            }
        } else {
            normal = circleCenter.subtract(closestPoint).normalize();
        }
        
        double penetrationDepth = circle.getRadius() - distance;
        Vector2D penetrationVector = normal.multiply(penetrationDepth);
        
        return new CollisionResult(true, penetrationVector, penetrationDepth, closestPoint);
    }
    
    private static CollisionResult checkLineGeometry(Line line, Geometry geom) {
        if (geom.getType() == GeometryType.LINE) {
            boolean colliding = line.intersects((Line) geom);
            return new CollisionResult(colliding);
        } else if (geom.getType() == GeometryType.CIRCLE) {
            return checkLineCircle(line, (Circle) geom);
        } else if (geom.getType() == GeometryType.RECTANGLE) {
            return checkLineRectangle(line, (Rectangle) geom);
        }
        return new CollisionResult(line.intersects(geom));
    }
    
    private static CollisionResult checkLineCircle(Line line, Circle circle) {
        boolean colliding = circle.intersectsLine(line);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D lineCenter = line.getCenter();
        Vector2D circleCenter = circle.getCenter();
        Vector2D normal = circleCenter.subtract(lineCenter).normalize();
        
        return new CollisionResult(true, normal, 0, lineCenter);
    }
    
    private static CollisionResult checkLineRectangle(Line line, Rectangle rect) {
        boolean colliding = rect.intersectsLine(line);
        if (!colliding) return new CollisionResult(false);
        
        return new CollisionResult(true, new Vector2D(0, 0), 0, line.getCenter());
    }
    
    private static CollisionResult checkPolygonGeometry(Polygon polygon, Geometry geom) {
        boolean colliding = polygon.intersects(geom);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D center1 = polygon.getCenter();
        Vector2D center2 = geom.getCenter();
        Vector2D normal = center2.subtract(center1).normalize();
        
        return new CollisionResult(true, normal, 0, center1);
    }
    
    private static CollisionResult checkEllipseGeometry(Ellipse ellipse, Geometry geom) {
        boolean colliding = ellipse.intersects(geom);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D center1 = ellipse.getCenter();
        Vector2D center2 = geom.getCenter();
        Vector2D normal = center2.subtract(center1).normalize();
        
        // Calculate penetration depth more accurately for ellipses
        double penetrationDepth = 0;
        if (geom.getType() == GeometryType.CIRCLE) {
            Circle circle = (Circle) geom;
            Vector2D distance = circle.getCenter().subtract(ellipse.getCenter());
            penetrationDepth = circle.getRadius() + Math.min(ellipse.getRadiusX(), ellipse.getRadiusY()) - distance.magnitude();
        } else {
            penetrationDepth = 1.0; // Default penetration
        }
        
        return new CollisionResult(true, normal, penetrationDepth, center1);
    }
    
    private static CollisionResult checkCurveGeometry(Curve curve, Geometry geom) {
        boolean colliding = curve.intersects(geom);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D center1 = curve.getCenter();
        Vector2D center2 = geom.getCenter();
        Vector2D normal = center2.subtract(center1).normalize();
        
        // Find closest point on curve to other geometry's center
        Vector2D closestPoint = findClosestPointOnCurve(curve, center2);
        double penetrationDepth = 0;
        
        if (geom.getType() == GeometryType.CIRCLE) {
            Circle circle = (Circle) geom;
            penetrationDepth = circle.getRadius() - closestPoint.distanceTo(circle.getCenter());
        }
        
        return new CollisionResult(true, normal, penetrationDepth, closestPoint);
    }
    
    private static CollisionResult checkCompositeGeometry(CompositeShape composite, Geometry geom) {
        boolean colliding = composite.intersects(geom);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D center1 = composite.getCenter();
        Vector2D center2 = geom.getCenter();
        Vector2D normal = center2.subtract(center1).normalize();
        
        // Find the most significant collision among composite shapes
        double maxPenetration = 0;
        for (Geometry shape : composite.getShapes()) {
            CollisionResult result = checkCollision(shape, geom);
            if (result.isColliding()) {
                maxPenetration = Math.max(maxPenetration, result.getPenetrationDepth());
            }
        }
        
        return new CollisionResult(true, normal, maxPenetration, center1);
    }
    
    private static CollisionResult checkFreeFormGeometry(FreeForm freeForm, Geometry geom) {
        boolean colliding = freeForm.intersects(geom);
        if (!colliding) return new CollisionResult(false);
        
        Vector2D center1 = freeForm.getCenter();
        Vector2D center2 = geom.getCenter();
        Vector2D normal = center2.subtract(center1).normalize();
        
        // For freeform, use approximate penetration depth
        double penetrationDepth = 2.0; // Default approximation
        
        if (geom.getType() == GeometryType.CIRCLE) {
            Circle circle = (Circle) geom;
            Vector2D distance = circle.getCenter().subtract(freeForm.getCenter());
            BoundingBox bounds = freeForm.getBoundingBox();
            double approxRadius = Math.max(bounds.getWidth(), bounds.getHeight()) / 2;
            penetrationDepth = circle.getRadius() + approxRadius - distance.magnitude();
        }
        
        return new CollisionResult(true, normal, penetrationDepth, center1);
    }
    
    private static Vector2D findClosestPointOnCurve(Curve curve, Vector2D point) {
        Vector2D closestPoint = curve.getPoint(0);
        double minDistance = point.distanceTo(closestPoint);
        
        // Sample the curve to find closest point
        List<Vector2D> samplePoints = curve.getSamplePoints();
        for (Vector2D samplePoint : samplePoints) {
            double distance = point.distanceTo(samplePoint);
            if (distance < minDistance) {
                minDistance = distance;
                closestPoint = samplePoint;
            }
        }
        
        return closestPoint;
    }
}