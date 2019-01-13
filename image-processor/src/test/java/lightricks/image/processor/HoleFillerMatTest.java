package lightricks.image.processor;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;

public class HoleFillerMatTest {

    @Inject
    private LibsLoader libsLoader;

    private static final double HOLE_PIXEL_VAL = -1;
    private static final String IMAGE_FILENAME = "starry_night.jpg";
    private static final String MASK_FILENAME = "circle_mask.jpg";
    private static final String OUTPUT_FILENAME = "output.png";

    private String imageFilename, maskFilename;

    @Before public void setUp() {
        Injector injector = Guice.createInjector(new TestHoleFillerModule());
        libsLoader = injector.getInstance(LibsLoader.class);

        imageFilename = this.getClass().getClassLoader().getResource(IMAGE_FILENAME).getFile();
        maskFilename = this.getClass().getClassLoader().getResource(MASK_FILENAME).getFile();
    }

    @Rule
    public TemporaryFolder outputFolder= new TemporaryFolder();

    @Test
    public void testFileToMatConversion() throws Exception {

        Mat image = Imgcodecs.imread(imageFilename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
        Mat holeMask = Imgcodecs.imread(maskFilename, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        HoleFillerMat holeFillerMat = HoleFillerMat.imread(imageFilename, maskFilename);

        Mat expectedMatrix = new Mat(image.size(), CvType.CV_32FC1);
        image.convertTo(expectedMatrix, CvType.CV_32FC1, 1.f/255);
        holeMask.convertTo(holeMask, CvType.CV_8UC1, 1.f/255);
        expectedMatrix.setTo(new Scalar(HOLE_PIXEL_VAL), holeMask);

        MatUtils.assertMatEqual(expectedMatrix, holeFillerMat);
    }

    @Test
    public void testMatToFileConversion() throws Exception {
        String outputImageFile = outputFolder.getRoot() + "/" + OUTPUT_FILENAME;

        Mat origMat = new Mat(100, 100, CvType.CV_8UC1);
        Core.randu(origMat,0, 255);

        Mat scaledMat = new Mat(origMat.size(), CvType.CV_32FC1);
        origMat.convertTo(scaledMat, CvType.CV_32FC1, 1.f/255);
        HoleFillerMat.imwrite(outputImageFile, new HoleFillerMat(scaledMat));

        Mat res = Imgcodecs.imread(outputImageFile, Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

        MatUtils.assertMatEqual(origMat, res);
    }
}
