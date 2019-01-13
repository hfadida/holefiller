package lightricks.image.processor;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

public class HoleBoundaryMask extends Mat{

    private static final int TYPE = CvType.CV_8SC1;
    private static final int HOLE_PIXEL_VAL = Hole.HOLE_PIXEL_VAL;
    private static final int BACKGROUND_PIXEL_VALUE = 0;
    private static final int BOUNDARY_PIXEL_VALUE = 1;

    public static boolean isABackgroundPixel(double val) {
        return val == BACKGROUND_PIXEL_VALUE;
    }

    public HoleBoundaryMask(Size size) {
        super(size, TYPE, new Scalar(BACKGROUND_PIXEL_VALUE));
    }

    public void setHolePixel(int row, int col) {
        put(row, col, HOLE_PIXEL_VAL);
    }

    public void setBoundaryPixel(int row, int col) {
        put(row, col, BOUNDARY_PIXEL_VALUE);
    }
}
