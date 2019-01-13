package lightricks.image.processor;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

public class HoleFillerMat extends Mat{

    private static final int TYPE = CvType.CV_32FC1;
    private static final int HOLE_PIXEL_VAL = Hole.HOLE_PIXEL_VAL;
    private static final double MIN_PIXEL_VAL = 0.;
    private static final double MAX_PIXEL_VAL = 1.;
    private static final double SCALE_FACTOR = 255.;

    public static HoleFillerMat imread(String imageFilename, String holeMaskFilename) throws Exception {
        Mat image = Imgcodecs.imread(imageFilename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat holeMask = Imgcodecs.imread(holeMaskFilename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        if (!image.size().equals(holeMask.size())) {
            throw new Exception("mask and image are not in the same size");
        }
        image.convertTo(image, CvType.CV_32FC1, 1/ SCALE_FACTOR);
        holeMask.convertTo(holeMask, CvType.CV_8UC1, 1/ SCALE_FACTOR);
        image.setTo(new Scalar(HOLE_PIXEL_VAL), holeMask);

        return new HoleFillerMat(image);
    }

    public static boolean imwrite(String filename, HoleFillerMat img) {
        Mat mat = new Mat(img.size(), CvType.CV_8UC1);
        img.convertTo(mat, CvType.CV_8UC1, SCALE_FACTOR);
        return Imgcodecs.imwrite(filename, mat);
    }

    private boolean isAHole(double val) {
        return (val == HOLE_PIXEL_VAL);
    }
    private boolean isAValidPixel(double val) {
        com.google.common.collect.Range<Double> range = com.google.common.collect.Range.closed(MIN_PIXEL_VAL, MAX_PIXEL_VAL);
        return range.contains(val) || isAHole(val);
    }

    private void validation() {
        if (!empty() && !(this.type() == TYPE)) {
            throw new IllegalArgumentException("Illegal mat type");
        }

        for (int i =  0; i < this.rows(); i++ ) {
            for (int j = 0; j < this.cols(); j++) {
                if (!isAValidPixel(this.get(i, j)[0])) {
                    throw new IllegalArgumentException("Illegal mat element");
                }
            }
        }
    }

    public HoleFillerMat(Mat m) {
        super(m, Range.all(), Range.all());
        validation();
    }

    public HoleFillerMat(Size size) {
        super(size, CvType.CV_32FC1);
    }
}
