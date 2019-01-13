package lightricks.image.processor;

import org.junit.Test;
import org.opencv.core.Point;

import static org.junit.Assert.assertEquals;

public class ConnectivityUtilsTest {
    @Test(expected = IllegalArgumentException.class)
    public void testIllegalConnectivityType() {
        ConnectivityUtils.connectedComponent(new Point(0, 0), -4);
    }

    @Test
    public void test4WayConnectivityType() {
        Point point = new Point(0, 0);
        ConnectedComponentSet<Point> connectedComponentExpected = new ConnectedComponentSet();
        connectedComponentExpected.add(new Point(point.x - 1, point.y));
        connectedComponentExpected.add(new Point(point.x +1 , point.y));
        connectedComponentExpected.add(new Point(point.x, point.y - 1));
        connectedComponentExpected.add(new Point(point.x , point.y + 1));

        ConnectedComponentSet<Point> connectedComponentActual = ConnectivityUtils.connectedComponent(point, 4);
        assertEquals(connectedComponentExpected, connectedComponentActual);
    }

    @Test
    public void test8WayConnectivityType() {
        Point point = new Point(0, 0);
        ConnectedComponentSet<Point> connectedComponentExpected = new ConnectedComponentSet();
        connectedComponentExpected.add(new Point(point.x - 1, point.y));
        connectedComponentExpected.add(new Point(point.x +1 , point.y));
        connectedComponentExpected.add(new Point(point.x, point.y - 1));
        connectedComponentExpected.add(new Point(point.x , point.y + 1));

        connectedComponentExpected.add(new Point(point.x - 1, point.y - 1));
        connectedComponentExpected.add(new Point(point.x - 1, point.y + 1));
        connectedComponentExpected.add(new Point(point.x +1 , point.y - 1));
        connectedComponentExpected.add(new Point(point.x +1 , point.y + 1));

        ConnectedComponentSet<Point> connectedComponentActual = ConnectivityUtils.connectedComponent(point, 8);
        assertEquals(connectedComponentExpected, connectedComponentActual);
    }
}
