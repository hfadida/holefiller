package lightricks.image.processor;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.sun.tools.javac.util.Pair;
import org.opencv.core.Point;

import java.util.LinkedList;
import java.util.List;

public class HoleFillerLightricksApproximation extends HoleFiller {

    private WeightingFunction weightingFunction;
    private int connectivity;

    @Inject
    public HoleFillerLightricksApproximation(
            @Assisted("connectivity") int connectivity,
            @Assisted("epsilon") float epsilon,
            @Assisted("z") int z) {
        this(connectivity, new DefaultWeightingFunction(epsilon, z));
    }

    public HoleFillerLightricksApproximation(int connectivity, WeightingFunction weightingFunction) {
        this.connectivity = connectivity;
        this.weightingFunction = weightingFunction;
    }

    private double estimatePixelValue(HoleFillerMat mat, Point holePixel, int depth) {
        ConnectedComponentSet<Point> connectedComponentSet = ConnectivityUtils.connectedComponent(holePixel, connectivity);

        double weighedValSum = 0.0;
        double weightSum = 0.0;

        for (Point connectedPixel: connectedComponentSet) {
            double connectedVal = mat.get((int)connectedPixel.y, (int)connectedPixel.x)[0];
            if (Hole.isAHolePixel(connectedVal)) {
                continue;
            }

            Point diff = new Point(connectedPixel.x - holePixel.x, connectedPixel.y - holePixel.y);

            Point newEstimatedBoundaryPixel = new Point(holePixel.x + diff.x*depth, holePixel.y + diff.y*depth);

            double weight = weightingFunction.weight(newEstimatedBoundaryPixel, holePixel);
            weightSum += weight;
            weighedValSum += weight*connectedVal;
        }
        return weighedValSum/weightSum;
    }

    private HoleFillerMat evaluateHolePixelsValue(HoleFillerMat mat, Hole hole, BoundarySet boundarySet) {

        ConnectedComponentSet<Point> currConnectedComponent = new ConnectedComponentSet<>();
        currConnectedComponent.addAll(boundarySet);

        ConnectedComponentSet<Point> currHole = new ConnectedComponentSet<>();

        List<Pair<Point, Double>> newValues = new LinkedList<Pair<Point, Double>>();

        int depth = 1;

        while (currHole.size() < hole.size()) {
            currConnectedComponent = ConnectivityUtils.connectedComponentHole(mat, currConnectedComponent, connectivity);
            currHole.addAll(currConnectedComponent);
            for (Point pixel: currConnectedComponent) {
                double val = estimatePixelValue(mat, pixel, depth);
                newValues.add(new Pair<>(pixel, val));
            }

            for (Pair<Point, Double> pixelVal :newValues) {
                Point pixel = pixelVal.fst;
                double val = pixelVal.snd;

                mat.put((int)pixel.y, (int)pixel.x, val);
            }

            depth++;
        }

        return mat;
    }

    public void apply(HoleFillerMat holeMat, HoleFillerMat fixedMat ) {

        Hole hole = new Hole(holeMat);

        if (hole.isEmpty()) {
            holeMat.copyTo(fixedMat);
            return;
        }

        BoundarySet boundary = BoundarySet.boundary(holeMat, hole, connectivity);

        holeMat.copyTo(fixedMat);
        evaluateHolePixelsValue(fixedMat, hole, boundary);
    }
}
