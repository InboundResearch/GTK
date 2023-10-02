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

  public static Segments join (Segments a, Segments b) {
    List<Segment> segments = new ArrayList<>(a.segments.size() + b.segments.size());
    // XXX do we need to consider whether there are any duplicate segments?
    segments.addAll(a.segments);
    segments.addAll(b.segments);
    return new Segments (segments);
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
    segments = clipToLine(segments, Line.horizontalLeft (domain.top()), Line.Classification.BACK, Line.Classification.ON);
    segments = clipToLine(segments, Line.horizontalRight (domain.bottom()), Line.Classification.BACK, Line.Classification.ON);
    segments = clipToLine(segments, Line.verticalDown (domain.left()), Line.Classification.BACK, Line.Classification.ON);
    segments = clipToLine(segments, Line.verticalUp (domain.right()), Line.Classification.BACK, Line.Classification.ON);
    return (segments.size() > 0) ? segments : null;
  }

  public Segments clipToDomain (Domain domain) {
    List<Segment> result = clipToDomain (segments, domain);
    return (result !=null) ? new Segments (result) : null;
  }

  public static List<Segment> trimToDomain (List<Segment> segments, Domain domain) {
    List<Segment> result = new ArrayList<>();
    for (Segment segment: segments) {
      if (domain.contains(segment)) {
        result.add(segment);
      }
    }
    return (result.size() > 0) ? result : null;
  }

  public Segments trimToDomain (Domain domain) {
    List<Segment> result = trimToDomain (segments, domain);
    return (result !=null) ? new Segments (result) : null;
  }

  public List<Segments> partition () {
    int segmentsSize = segments.size();
    int partitionSize = Math.max (1, (int) Math.sqrt (segmentsSize));
    int partitionCount = (segmentsSize / partitionSize) + (((segmentsSize % partitionSize) > 0) ? 1 : 0);
    List<Segments> output = new ArrayList<>(partitionCount);
    for (int i = 0, start = 0; i < partitionCount; ++i, start += partitionSize) {
      output.add (new Segments (segments.subList (start, Math.min (start + partitionSize, segmentsSize - 1))));
    }
    return output;
  }
}
