package yanwittmann.types;

/**
 * A simple point object with two to three dimensions.<br>
 * This class has been written by <a href="http://yanwittmann.de">Yan Wittmann</a>.
 * @author Yan Wittmann
 */
public class Point {

    public final static double DEFAULT_VALUE = -59375.34d;

    private double x, y, z;
    private boolean hasThreeComponents = false;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        hasThreeComponents = true;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        if (!hasThreeComponents) return DEFAULT_VALUE;
        return z;
    }

    public Point setX(double x) {
        this.x = x;
        return this;
    }

    public Point setY(double y) {
        this.y = y;
        return this;
    }

    public Point setZ(double z) {
        hasThreeComponents = true;
        this.z = z;
        return this;
    }

    public Point set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Point set(double x, double y, double z) {
        hasThreeComponents = true;
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Point translate(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Point translate(double x, double y, double z) {
        this.x += x;
        this.y += y;
        if (hasThreeComponents)
            this.z += z;
        return this;
    }

    public Point translate(Point point) {
        this.x += point.x;
        this.y += point.y;
        if (hasThreeComponents)
            this.z += point.z;
        return this;
    }

    public Point round() {
        x = Math.round(x);
        y = Math.round(y);
        if (hasThreeComponents) z = Math.round(z);
        return this;
    }

    public boolean equals(Point point) {
        return point.getX() == x && point.getY() == y;
    }

    public boolean equals(double x, double y) {
        return this.x == x && this.y == y;
    }

    public boolean equals(double x, double y, double z) {
        if (!hasThreeComponents) return false;
        return this.x == x && this.y == y && this.z == z;
    }

    @Override
    public String toString() {
        if (hasThreeComponents)
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    ", z=" + z +
                    '}';
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
