package us.irdev.gtk.geography;

import us.irdev.gtk.xyw.Segments;
import us.irdev.gtk.xyw.SegmentsPair;

import java.util.List;

public class RingArray {
    public final Segments boundary;
    public final List<Segments> holes;
    public RingArray(List<Segments> segmentsList) {
        boundary = segmentsList.get(0);
        holes = segmentsList.subList (1, segmentsList.size());
    }

    private static boolean segmentsContains (Segments source, Segments test) {
        SegmentsPair sp = SegmentsPair.reduce(source, test);
        if (sp != null) {
            return (sp.intersections().size() % 2) == 1;
        }
        return false;
    }

    public boolean contains(Segments segments) {
        // test the boundary, and if that passes, test the holes for exclusion
        if (segmentsContains (boundary, segments)) {
            for (Segments hole : holes) {
                if (segmentsContains(hole, segments)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
}
