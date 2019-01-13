package lightricks.image.processor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;

public class HoleFillerLightricks extends HoleFiller {

    private WeightingFunction weightingFunction;
    private int connectivity;

    @Inject
    public HoleFillerLightricks(
            @Assisted("connectivity") int connectivity,
            @Assisted("epsilon") float epsilon,
            @Assisted("z") int z) {
        this(connectivity, new DefaultWeightingFunction(epsilon, z));
    }

    public HoleFillerLightricks(int connectivity, WeightingFunction weightingFunction) {
        this.connectivity = connectivity;
        this.weightingFunction = weightingFunction;
    }

    private Mat weightingMatrix(Hole hole, BoundarySet boundary) {
        Mat weightingMatrix = new Mat(hole.size(), boundary.size(), weightingFunction.type());

        int holePixelIndx = 0, boundaryPixelIndx;

        for (Point holePixel:hole) {
            boundaryPixelIndx = 0;
            for (Point b: boundary) {
                weightingMatrix.put(holePixelIndx, boundaryPixelIndx++, weightingFunction.weight(holePixel, b));
            }
            holePixelIndx++;
        }

        return weightingMatrix;
    }

    private  Mat evaluateHolePixelsValue(HoleFillerMat mat, Hole hole, BoundarySet boundary) {
        Mat weightingMatrix = weightingMatrix(hole, boundary);
        Mat boundaryValuesVector = boundary.valVector();

        Mat weightedBoundaryValuesSum = new Mat(hole.size(), 1, boundaryValuesVector.type());
        Core.gemm(weightingMatrix, boundaryValuesVector, 1, new Mat(), 0, weightedBoundaryValuesSum);

        Mat boundaryWeightSum= new Mat(hole.size(), 1, weightingMatrix.type());
        Mat sumVector = Mat.ones(boundaryValuesVector.size(), CvType.CV_32FC1);
        Core.gemm(weightingMatrix, sumVector, 1, new Mat(), 0, boundaryWeightSum);

        Mat fixedValueVector = new Mat(hole.size(), 1, mat.type());
        Core.divide(weightedBoundaryValuesSum, boundaryWeightSum, fixedValueVector);

        return fixedValueVector;
    }

    private void fillHolesValue(Hole hole, Mat holePixelsValues, HoleFillerMat mat) {

        int pixelIndex = 0;

        for (Point holePixel: hole) {
            mat.put((int)holePixel.y, (int)holePixel.x, holePixelsValues.get(pixelIndex++, 0));
        }
    }

    public void apply(HoleFillerMat holeMat, HoleFillerMat fixedMat ) {

        Hole hole = new Hole(holeMat);

        if (hole.isEmpty()) {
            holeMat.copyTo(fixedMat);
            return;
        }

        BoundarySet boundary = BoundarySet.boundary(holeMat, hole, connectivity);

        Mat holePixelsValues = evaluateHolePixelsValue(holeMat, hole, boundary);

        holeMat.copyTo(fixedMat);
        fillHolesValue(hole, holePixelsValues, fixedMat);
    }
}
