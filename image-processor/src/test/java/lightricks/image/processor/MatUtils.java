package lightricks.image.processor;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


//Based on https://github.com/opencv/opencv/blob/master/modules/java/test/android_test/src/org/opencv/test/OpenCVTestCase.java
public class MatUtils {

    static private void compareMats(Mat expected, Mat actual, boolean isEqualityMeasured) {
        if (expected.type() != actual.type() || expected.cols() != actual.cols() || expected.rows() != actual.rows()) {
            throw new UnsupportedOperationException("Can not compare " + expected + " and " + actual);
        }

        Mat diff = new Mat();

        Core.absdiff(expected, actual, diff);
        Mat reshaped = diff.reshape(1);
        int mistakes = Core.countNonZero(reshaped);

        reshaped.release();
        diff.release();

        if (isEqualityMeasured)
            assertTrue("Mats are different in " + mistakes + " points", 0 == mistakes);
        else
            assertFalse("Mats are equal", 0 == mistakes);
    }

    public static void assertMatEqual(Mat m1, Mat m2) {
        compareMats(m1, m2, true);
    }

    public static void assertMatNotEqual(Mat m1, Mat m2) {
        compareMats(m1, m2, false);
    }
}
