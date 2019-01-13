package lightricks.image.processor;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import java.util.LinkedHashSet;

public class BoundarySet extends LinkedHashSet<Point> {

    public static BoundarySet boundary(HoleFillerMat mat, Hole hole, int connectivity) {
        HoleBoundaryMask boundaryMat = new HoleBoundaryMask(mat.size());

        for (Point holePixel: hole) {
            boundaryMat.setHolePixel((int)holePixel.y, (int)holePixel.x);
        }

        for (Point holePixel: hole) {
            ConnectedComponentSet<Point> connectedComponent = ConnectivityUtils.connectedComponent(holePixel, connectivity);

            for (Point pixel: connectedComponent) {
                if (HoleBoundaryMask.isABackgroundPixel(boundaryMat.get((int)pixel.y, (int)pixel.x)[0])) {
                    boundaryMat.setBoundaryPixel((int)pixel.y, (int)pixel.x);
                }
            }
        }

        BoundarySet boundarySet = new BoundarySet();
        boundarySet.addAll(boundaryMat, mat);

        return boundarySet;
    }

    private Mat valVector;

    public Mat valVector() {
        return valVector;
    }

    public void addAll(Mat boundaryMat, Mat mat) {
        valVector = new Mat();

        for (int i = 0; i < boundaryMat.rows(); i++) {
            for (int j = 0; j< boundaryMat.cols(); j++) {
                if (boundaryMat.get(i, j)[0] == 1) {
                    this.add(new Point(j, i));
                }
            }
        }

        valVector = new Mat(this.size(), 1, mat.type());
        int i = 0;
        for (Point b: this) {
            valVector.put(i, 0, mat.get((int) b.y, (int) b.x));
            i++;
        }
    }
}
