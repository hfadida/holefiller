package lightricks.image.processor;

import org.opencv.core.CvType;
import org.opencv.core.Point;

public class DefaultWeightingFunction implements WeightingFunction {

    private static final int TYPE = CvType.CV_32FC1;
    private float epsilon;
    private int z;

    public DefaultWeightingFunction(float epsilon, int z) {
        this.epsilon = epsilon;
        this.z = z;
    }

    public double weight(Point u, Point v) {
        Point diff = new Point(u.x - v.x, u.y - v.y);
        return  Math.pow(Math.pow(Math.sqrt(diff.x*diff.x  + diff.y*diff.y), z) + epsilon, -1);
    }

    public int type() { return TYPE; }
}
