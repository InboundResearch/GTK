package us.irdev.gtk.xyw;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Tuple.PT;

public class PolygonTest {
    @Test
    public void testContains() {
        Tuple[] pts = {
                PT(1, 1),
                PT(-1, 1),
                PT(-1, -1),
                PT(1, -1)
        };
        var polygon = new Polygon (pts);

        assertTrue(polygon.contains(Tuple.ORIGIN));

        assertTrue(polygon.contains(PT(0, 1)));
        assertTrue(polygon.contains(PT(0, -1)));

        assertTrue(polygon.contains(PT(-1, 0)));
        assertTrue(polygon.contains(PT(1, 0)));

        assertTrue(polygon.contains(PT(1, 1)));
        assertTrue(polygon.contains(PT(1, -1)));

        assertTrue(polygon.contains(PT(-1, -1)));
        assertTrue(polygon.contains(PT(-1, 1)));

        assertFalse(polygon.contains(PT(2, 0)));
        assertFalse(polygon.contains(PT(-2, 0)));
        assertFalse(polygon.contains(PT(0, 2)));
        assertFalse(polygon.contains(PT(0, -2)));

        assertFalse(polygon.contains(PT(2, 2)));
        assertFalse(polygon.contains(PT(-2, 2)));
        assertFalse(polygon.contains(PT(-2, 2)));
        assertFalse(polygon.contains(PT(2, -2)));
    }

    @Test
    public void testIntersectsAtVertex() {
        Tuple[] pts = {
                PT(1, 0),
                PT(0, 1),
                PT(-1, 0),
                PT(0, -1)
        };
        var polygon = new Polygon(pts);

        assertTrue(polygon.contains(Tuple.ORIGIN));
        assertTrue(polygon.contains(PT(0.5, 0)));
        assertTrue(polygon.contains(PT(1, 0)));
        assertTrue(polygon.contains(PT(-0.5, 0)));
        assertTrue(polygon.contains(PT(-1, 0)));

        assertFalse(polygon.contains(PT(1.5, 0)));
        assertFalse(polygon.contains(PT(-1.5, 0)));

        assertFalse(polygon.contains(PT(0, 1.5)));
        assertFalse(polygon.contains(PT(0, -1.5)));
    }

    @Test
    public void testW() {
        Tuple[] pts = {
                PT(2, 1),
                PT(-1, 1),
                PT(-1, -1),
                PT(0, 0.5),
                PT(1, -1),

                PT(1.25, -0.5),
                PT(1.75, -0.5),
                PT(2, -1),
        };
        var polygon = new Polygon(pts);

        assertFalse(polygon.contains(Tuple.ORIGIN));

        assertTrue(polygon.contains(PT(-0.5, 0.5)));
        assertTrue(polygon.contains(PT(0.5, 0.5)));

        assertTrue(polygon.contains(PT(0.5, 0)));
        assertTrue(polygon.contains(PT(1, 0)));
        assertTrue(polygon.contains(PT(-0.5, 0)));
        assertTrue(polygon.contains(PT(-1, 0)));

        assertTrue(polygon.contains(PT(1.5, 0)));
        assertFalse(polygon.contains(PT(2.5, 0)));
        assertFalse(polygon.contains(PT(-1.5, 0)));

        assertFalse(polygon.contains(PT(0, 1.5)));
        assertFalse(polygon.contains(PT(0, -1)));

        assertFalse(polygon.contains(PT(-1.5, -0.5)));
        assertTrue(polygon.contains(PT(-0.9, -0.5)));
        assertFalse(polygon.contains(PT(0, -0.5)));
        assertTrue(polygon.contains(PT(1, -0.5)));
        assertTrue(polygon.contains(PT(1.5, -0.5)));
        assertTrue(polygon.contains(PT(1.9, -0.5)));
        assertFalse(polygon.contains(PT(2.5, -0.5)));
        assertFalse(polygon.contains(PT(1.5, -0.75)));
    }
}
