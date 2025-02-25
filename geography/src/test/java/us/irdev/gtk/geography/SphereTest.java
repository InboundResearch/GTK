package us.irdev.gtk.geography;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static us.irdev.gtk.geography.Sphere.greatArcLength;
import static us.irdev.gtk.xyw.Assertions.assertSimilar;
import static us.irdev.gtk.xyw.Tuple.PT;

public class SphereTest {
    private static final Logger log = LogManager.getLogger(SphereTest.class);

    @Test
    public void testSphere() throws Exception {
        var sphere = new Sphere();
        assertEquals(0.0, sphere.greatArcLength (PT (0, 0), PT(0, 0)));
        assertSimilar(69.0547669239163, sphere.greatArcLength (PT (1, 0), PT(0, 0)));
        assertSimilar(69.0547669239163, sphere.greatArcLength (PT (0, 1), PT(0, 0)));
    }

    @Test
    public void testGreatArc() throws Exception {
        assertEquals(0.0, greatArcLength (PT (0, 0), PT(0, 0), 1.0));
        assertSimilar(Math.PI / 2.0, greatArcLength (PT (90, 0), PT(0, 0), 1.0));
        assertSimilar(Math.PI / 2.0, greatArcLength (PT (0, 90), PT(0, 0), 1.0));

        assertEquals(0.0, greatArcLength (PT (0, 0), PT(0, 0), Sphere.EARTH_RADIUS_MILES));
        assertSimilar(69.0547669239163, greatArcLength (PT (1, 0), PT(0, 0), Sphere.EARTH_RADIUS_MILES));
        assertSimilar(69.0547669239163, greatArcLength (PT (0, 1), PT(0, 0), Sphere.EARTH_RADIUS_MILES));
    }
}
