package lightricks.image.processor;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class HoleFillerLightricksTest {

    private static final double MIN_PIXEL_VAL = 0.;
    private static final double MAX_PIXEL_VAL = 1.;
    private static final double HOLE_PIXEL_VAL = -1;

    private HoleFiller holefiller;

    @Inject
    private LibsLoader libsLoader;

    @Inject
    HoleFillerFactory holeFillerFactory;

    @Before
    public void setUp() {
        Injector injector = Guice.createInjector(new TestHoleFillerModule());
        holeFillerFactory = injector.getInstance(HoleFillerFactory.class);
        holefiller = holeFillerFactory.create(4, 0.01f, 3);
    }

    @Test
    public void testNoHoleMatrix() {
        HoleFillerMat holeMat = new HoleFillerMat(new Size(100, 100));
        Core.randu(holeMat,MIN_PIXEL_VAL, MAX_PIXEL_VAL);

        HoleFillerMat res = new HoleFillerMat(holeMat.size());

        holefiller.apply(holeMat, res);

        MatUtils.assertMatEqual(holeMat, res);
    }

    @Test
    public void testHoleMatrix() {
        HoleFillerMat holeMat = new HoleFillerMat(new Size(1000, 1000));
        Core.randu(holeMat,MIN_PIXEL_VAL, MAX_PIXEL_VAL);

        Imgproc.circle (
                holeMat,
                new Point(230, 160),
                100,
                new Scalar(HOLE_PIXEL_VAL),
                Core.FILLED
        );

        HoleFillerMat outputMat = new HoleFillerMat(holeMat.size());
        holefiller.apply(holeMat, outputMat);

        //verify values range
        Mat rangeMat = new Mat();
        Core.inRange(outputMat, new Scalar(MIN_PIXEL_VAL), new Scalar(MAX_PIXEL_VAL), rangeMat);

        MatUtils.assertMatEqual(new Mat(rangeMat.size(), rangeMat.type(), new Scalar(255)), rangeMat);

        //verify no values change in the background
        Mat mask = new Mat();

        Core.multiply(holeMat, new Scalar(-1), mask);
        Core.add(mask, new Scalar(1), mask);
        mask.convertTo(mask, CvType.CV_8UC1, 1/2.f);

        Mat actualMat = outputMat.clone();
        Mat expectedMat = holeMat.clone();

        actualMat.setTo(new Scalar(2), mask);
        expectedMat.setTo(new Scalar(2), mask);

        MatUtils.assertMatEqual(expectedMat, actualMat);

        //verify no holes in the output mat
        Core.multiply(outputMat, new Scalar(-1), outputMat);
        Core.add(outputMat, new Scalar(1), outputMat);
        outputMat.convertTo(outputMat, CvType.CV_8UC1, 1/2.f);

        Mat negativePixelMat = Mat.zeros(outputMat.size(), outputMat.type());
        negativePixelMat.setTo(new Scalar(1), outputMat);

        MatUtils.assertMatEqual(Mat.zeros(outputMat.size(), outputMat.type()), negativePixelMat);
    }


}
