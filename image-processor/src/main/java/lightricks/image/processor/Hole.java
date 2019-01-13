package lightricks.image.processor;

import org.opencv.core.Point;

import java.util.LinkedHashSet;

public class Hole extends LinkedHashSet<Point> {
    public static final int HOLE_PIXEL_VAL = -1;

    public Hole(HoleFillerMat mat) {
        for (int i = 0; i < mat.rows(); i++) {
            for (int j=0; j < mat.cols(); j++) {
                if (isAHolePixel(mat.get(i, j)[0])) {
                    this.add(new Point(j, i));
                }
            }
        }
    }

    public static boolean isAHolePixel(double val) {
        return val == HOLE_PIXEL_VAL;
    }
}
