package engine.collision;

import java.util.ArrayList;
import java.util.List;

public class CurveMath {
    
    public static class BezierCurve {
        private Vector2D[] controlPoints;
        
        public BezierCurve(Vector2D[] controlPoints) {
            if (controlPoints.length < 2) {
                throw new IllegalArgumentException("Bezier curve needs at least 2 control points");
            }
            this.controlPoints = controlPoints.clone();
        }
        
        public Vector2D getPoint(double t) {
            t = Math.max(0, Math.min(1, t));
            return calculateBezierPoint(controlPoints, t);
        }
        
        public Vector2D getTangent(double t) {
            t = Math.max(0, Math.min(1, t));
            return calculateBezierTangent(controlPoints, t);
        }
        
        public double getCurvature(double t) {
            Vector2D firstDerivative = calculateBezierTangent(controlPoints, t);
            Vector2D secondDerivative = calculateBezierSecondDerivative(controlPoints, t);
            
            double cross = Math.abs(firstDerivative.cross(secondDerivative));
            double firstMagCubed = Math.pow(firstDerivative.magnitude(), 3);
            
            return firstMagCubed == 0 ? 0 : cross / firstMagCubed;
        }
        
        public List<Vector2D> samplePoints(int numPoints) {
            List<Vector2D> points = new ArrayList<>();
            for (int i = 0; i <= numPoints; i++) {
                double t = (double) i / numPoints;
                points.add(getPoint(t));
            }
            return points;
        }
        
        public BoundingBox getBoundingBox() {
            List<Vector2D> samples = samplePoints(20);
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            
            for (Vector2D point : samples) {
                minX = Math.min(minX, point.getX());
                minY = Math.min(minY, point.getY());
                maxX = Math.max(maxX, point.getX());
                maxY = Math.max(maxY, point.getY());
            }
            
            return new BoundingBox(minX, minY, maxX, maxY);
        }
        
        private Vector2D calculateBezierPoint(Vector2D[] points, double t) {
            if (points.length == 1) return points[0];
            
            Vector2D[] newPoints = new Vector2D[points.length - 1];
            for (int i = 0; i < points.length - 1; i++) {
                double x = (1 - t) * points[i].getX() + t * points[i + 1].getX();
                double y = (1 - t) * points[i].getY() + t * points[i + 1].getY();
                newPoints[i] = new Vector2D(x, y);
            }
            
            return calculateBezierPoint(newPoints, t);
        }
        
        private Vector2D calculateBezierTangent(Vector2D[] points, double t) {
            if (points.length == 1) return new Vector2D(0, 0);
            
            Vector2D[] derivatives = new Vector2D[points.length - 1];
            for (int i = 0; i < points.length - 1; i++) {
                derivatives[i] = points[i + 1].subtract(points[i]).multiply(points.length - 1);
            }
            
            return calculateBezierPoint(derivatives, t);
        }
        
        private Vector2D calculateBezierSecondDerivative(Vector2D[] points, double t) {
            if (points.length <= 2) return new Vector2D(0, 0);
            
            Vector2D[] secondDerivatives = new Vector2D[points.length - 2];
            for (int i = 0; i < points.length - 2; i++) {
                Vector2D term1 = points[i + 2].subtract(points[i + 1].multiply(2)).add(points[i]);
                secondDerivatives[i] = term1.multiply((points.length - 1) * (points.length - 2));
            }
            
            return calculateBezierPoint(secondDerivatives, t);
        }
    }
    
    public static class CatmullRomSpline {
        private Vector2D[] controlPoints;
        private double alpha;
        
        public CatmullRomSpline(Vector2D[] controlPoints, double alpha) {
            if (controlPoints.length < 4) {
                throw new IllegalArgumentException("Catmull-Rom spline needs at least 4 control points");
            }
            this.controlPoints = controlPoints.clone();
            this.alpha = alpha;
        }
        
        public Vector2D getPoint(double t) {
            int segmentCount = controlPoints.length - 3;
            int segment = (int) (t * segmentCount);
            segment = Math.max(0, Math.min(segmentCount - 1, segment));
            double localT = (t * segmentCount) - segment;
            
            return calculateCatmullRomPoint(segment, localT);
        }
        
        public Vector2D getTangent(double t) {
            int segmentCount = controlPoints.length - 3;
            int segment = (int) (t * segmentCount);
            segment = Math.max(0, Math.min(segmentCount - 1, segment));
            double localT = (t * segmentCount) - segment;
            
            return calculateCatmullRomTangent(segment, localT);
        }
        
        public List<Vector2D> samplePoints(int numPoints) {
            List<Vector2D> points = new ArrayList<>();
            for (int i = 0; i <= numPoints; i++) {
                double t = (double) i / numPoints;
                points.add(getPoint(t));
            }
            return points;
        }
        
        private Vector2D calculateCatmullRomPoint(int segment, double t) {
            Vector2D p0 = controlPoints[segment];
            Vector2D p1 = controlPoints[segment + 1];
            Vector2D p2 = controlPoints[segment + 2];
            Vector2D p3 = controlPoints[segment + 3];
            
            double t2 = t * t;
            double t3 = t2 * t;
            
            return new Vector2D(
                0.5 * ((2 * p1.getX()) + (-p0.getX() + p2.getX()) * t + 
                       (2 * p0.getX() - 5 * p1.getX() + 4 * p2.getX() - p3.getX()) * t2 +
                       (-p0.getX() + 3 * p1.getX() - 3 * p2.getX() + p3.getX()) * t3),
                0.5 * ((2 * p1.getY()) + (-p0.getY() + p2.getY()) * t + 
                       (2 * p0.getY() - 5 * p1.getY() + 4 * p2.getY() - p3.getY()) * t2 +
                       (-p0.getY() + 3 * p1.getY() - 3 * p2.getY() + p3.getY()) * t3)
            );
        }
        
        private Vector2D calculateCatmullRomTangent(int segment, double t) {
            Vector2D p0 = controlPoints[segment];
            Vector2D p1 = controlPoints[segment + 1];
            Vector2D p2 = controlPoints[segment + 2];
            Vector2D p3 = controlPoints[segment + 3];
            
            double t2 = t * t;
            
            return new Vector2D(
                0.5 * ((-p0.getX() + p2.getX()) + 
                       2 * (2 * p0.getX() - 5 * p1.getX() + 4 * p2.getX() - p3.getX()) * t +
                       3 * (-p0.getX() + 3 * p1.getX() - 3 * p2.getX() + p3.getX()) * t2),
                0.5 * ((-p0.getY() + p2.getY()) + 
                       2 * (2 * p0.getY() - 5 * p1.getY() + 4 * p2.getY() - p3.getY()) * t +
                       3 * (-p0.getY() + 3 * p1.getY() - 3 * p2.getY() + p3.getY()) * t2)
            );
        }
    }
    
    public static class Ellipse {
        private Vector2D center;
        private double radiusX;
        private double radiusY;
        private double rotation;
        
        public Ellipse(Vector2D center, double radiusX, double radiusY, double rotation) {
            this.center = center;
            this.radiusX = radiusX;
            this.radiusY = radiusY;
            this.rotation = rotation;
        }
        
        public Ellipse(Vector2D center, double radiusX, double radiusY) {
            this(center, radiusX, radiusY, 0);
        }
        
        public Vector2D getPoint(double angle) {
            double cosRot = Math.cos(rotation);
            double sinRot = Math.sin(rotation);
            double cosAngle = Math.cos(angle);
            double sinAngle = Math.sin(angle);
            
            double x = center.getX() + radiusX * cosAngle * cosRot - radiusY * sinAngle * sinRot;
            double y = center.getY() + radiusX * cosAngle * sinRot + radiusY * sinAngle * cosRot;
            
            return new Vector2D(x, y);
        }
        
        public Vector2D getNormal(double angle) {
            double cosRot = Math.cos(rotation);
            double sinRot = Math.sin(rotation);
            double cosAngle = Math.cos(angle);
            double sinAngle = Math.sin(angle);
            
            double nx = (cosAngle / radiusX) * cosRot - (sinAngle / radiusY) * sinRot;
            double ny = (cosAngle / radiusX) * sinRot + (sinAngle / radiusY) * cosRot;
            
            Vector2D normal = new Vector2D(nx, ny);
            return normal.normalize();
        }
        
        public double getCurvature(double angle) {
            double cosAngle = Math.cos(angle);
            double sinAngle = Math.sin(angle);
            
            double numerator = Math.pow(radiusX * radiusY, 2);
            double denominator = Math.pow(
                Math.pow(radiusY * cosAngle, 2) + Math.pow(radiusX * sinAngle, 2), 
                1.5
            );
            
            return numerator / denominator;
        }
        
        public List<Vector2D> samplePoints(int numPoints) {
            List<Vector2D> points = new ArrayList<>();
            for (int i = 0; i < numPoints; i++) {
                double angle = 2 * Math.PI * i / numPoints;
                points.add(getPoint(angle));
            }
            return points;
        }
        
        public BoundingBox getBoundingBox() {
            List<Vector2D> samples = samplePoints(64);
            double minX = Double.POSITIVE_INFINITY;
            double minY = Double.POSITIVE_INFINITY;
            double maxX = Double.NEGATIVE_INFINITY;
            double maxY = Double.NEGATIVE_INFINITY;
            
            for (Vector2D point : samples) {
                minX = Math.min(minX, point.getX());
                minY = Math.min(minY, point.getY());
                maxX = Math.max(maxX, point.getX());
                maxY = Math.max(maxY, point.getY());
            }
            
            return new BoundingBox(minX, minY, maxX, maxY);
        }
        
        public Vector2D getCenter() { return center; }
        public double getRadiusX() { return radiusX; }
        public double getRadiusY() { return radiusY; }
        public double getRotation() { return rotation; }
        
        public void setCenter(Vector2D center) { this.center = center; }
        public void setRadiusX(double radiusX) { this.radiusX = radiusX; }
        public void setRadiusY(double radiusY) { this.radiusY = radiusY; }
        public void setRotation(double rotation) { this.rotation = rotation; }
    }
}