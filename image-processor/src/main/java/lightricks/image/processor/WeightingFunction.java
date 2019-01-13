package lightricks.image.processor;

import org.opencv.core.Point;

public interface WeightingFunction {
    double weight(Point u, Point v);
    int type();
}
