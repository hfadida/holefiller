package lightricks.image.processor;

import org.opencv.core.Point;

public interface ConnectivityUtils {

    static ConnectedComponentSet<Point> connectedComponentHole(HoleFillerMat mat, ConnectedComponentSet<Point> pointSet, int connectivity){
        ConnectedComponentSet<Point> connectedComponent = new ConnectedComponentSet();
        for (Point point: pointSet) {
            connectedComponent.addAll(connectedComponentHole(mat, point, connectivity));
        }
        return connectedComponent;
    }

    static ConnectedComponentSet<Point> connectedComponentHole(HoleFillerMat mat, Point point, int connectivity){

        ConnectedComponentSet<Point> connectedComponent = ConnectivityUtils.connectedComponent(point, connectivity);
        ConnectedComponentSet<Point> holeConnectedComponent = new ConnectedComponentSet<>();

        for (Point pixel: connectedComponent) {
            if (Hole.isAHolePixel(mat.get((int)pixel.y, (int)pixel.x)[0])) {
                holeConnectedComponent.add(pixel);
            }
        }
        return holeConnectedComponent;
    }

    static ConnectedComponentSet<Point> connectedComponent(Point point, int connectivity){

        switch (connectivity) {
            case 4: return connectedComponent4way(point);
            case 8: return connectedComponent8way(point);
            default: throw new IllegalArgumentException();
        }
    }

    static ConnectedComponentSet<Point> connectedComponent4way(Point point) {

        ConnectedComponentSet<Point> connectedComponent = new ConnectedComponentSet();
        connectedComponent.add(new Point(point.x-1, point.y));
        connectedComponent.add(new Point(point.x, point.y-1));
        connectedComponent.add(new Point(point.x, point.y+1));
        connectedComponent.add(new Point(point.x+1, point.y));

        return connectedComponent;
    }

    static ConnectedComponentSet<Point> connectedComponent8way(Point point) {

        ConnectedComponentSet<Point> connectedComponent = new ConnectedComponentSet();

        connectedComponent.add(new Point(point.x-1, point.y-1));
        connectedComponent.add(new Point(point.x-1, point.y+1));
        connectedComponent.add(new Point(point.x+1, point.y+1));
        connectedComponent.add(new Point(point.x+1, point.y-1));

        connectedComponent.add(new Point(point.x-1, point.y));
        connectedComponent.add(new Point(point.x+1, point.y));
        connectedComponent.add(new Point(point.x, point.y-1));
        connectedComponent.add(new Point(point.x, point.y+1));

        return connectedComponent;
    }
}
