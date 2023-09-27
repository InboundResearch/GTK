package us.irdev.gtk.xyw;

import java.util.ArrayList;
import java.util.List;

public class Segments {
  public final List<Segment> segments;
  public final Domain domain;
  public final int length;

  public Segments (List<Segment> segments) {
    this.segments = segments;
    length = segments.size();
    domain = new Domain ();
    for (Segment segment: segments) {
      domain.add(segment.a);
      domain.add(segment.b);
    }
  }

  public static List<Segment> clipToLine (List<Segment> segments, Line line, int... keeps) {
    List<Segment> result = new ArrayList<> ();
    if (segments != null) {
      for (Segment segment : segments) {
        Segment.Clip clip = segment.clipToLine (line);
        for (int keep : keeps) {
          if ((keep == Line.Classification.BACK) && (clip.back != null)) {
            result.add (clip.back);
          }
          if ((keep == Line.Classification.FRONT) && (clip.front != null)) {
            result.add (clip.front);
          }
          if ((keep == Line.Classification.ON) && (clip.on != null)) {
            result.add (clip.on);
          }
        }
      }
    }
    return result;
  }

  public Segments clipToLine (Line line, int... keeps) {
    List<Segment> result = clipToLine (segments, line, keeps);
    return (result.size() > 0) ? new Segments (result) : null;
  }

  public static List<Segment> clipToDomain (List<Segment> segments, Domain domain) {
    segments = clipToLine(segments, Line.horizontal(domain.top()), (domain.top() > 0) ? Line.Classification.FRONT : Line.Classification.BACK, Line.Classification.ON);
    segments = clipToLine(segments, Line.horizontal(domain.bottom()), (domain.bottom() > 0) ? Line.Classification.BACK : Line.Classification.FRONT, Line.Classification.ON);
    segments = clipToLine(segments, Line.vertical(domain.left()), (domain.left() > 0) ? Line.Classification.BACK : Line.Classification.FRONT, Line.Classification.ON);
    segments = clipToLine(segments, Line.vertical(domain.right()), (domain.right() > 0) ? Line.Classification.FRONT : Line.Classification.BACK, Line.Classification.ON);
    return (segments.size() > 0) ? segments : null;
  }

  public Segments clipToDomain (Domain domain) {
    List<Segment> result = clipToDomain (segments, domain);
    return (result !=null) ? new Segments (result) : null;
  }

  public static List<Tuple> intersect(Segments a, Segments b) {
    List<Tuple> result = new ArrayList<>();

    // loop until the stopping criterion are reached
    int counter = 0;
    while (true) {
      if ((a.length == 1) && (b.length == 1)) {
        break;
      }
      Domain domain = Domain.intersection (a.domain, b.domain).scale(1.05);
      if (domain.valid()) {
          a = a.clipToDomain (domain);
          b = b.clipToDomain (domain);
      } else {
        break;
      }
      ++counter;
    }

    // the result should be a pair of reduced-size lists of m and n segments, which we test
    // accordingly

    return result;
  }
}
