package us.irdev.gtk.xy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolyLine {
  private final Tuple[] points;
  private final boolean closed;

  public PolyLine (Tuple[] points, boolean closed) {
    this.points = points;
    this.closed = closed;
  }

  public PolyLine (List<Tuple> points, boolean closed) {
    this (points.toArray(new Tuple[]{}), closed);
  }

  public Tuple[] getPoints() {
    return points;
  }

  public boolean isClosed () {
    return closed;
  }

  private static Segment getNextSegment(Tuple tuple, Segment source, Map<Tuple, List<Segment>> mappedSegments, List<Tuple> output) {
    // try to get the referred segment list, and check if it succeeded
    List<Segment> segments = mappedSegments.get (tuple);
    if (segments != null) {
      // remove this tuple from the mapping, and output the tuple
      mappedSegments.remove (tuple);
      output.add (tuple);

      // the segments list has just one or two entries, one of which was the segment we followed
      // to this one. if the source was null, we don't care what segment we return, so the first
      // one is fine. otherwise, we don't want to return the same segment that we followed to get
      // here, so we return the other one.
      for (Segment segment : segments) {
        if (!Segment.similar(segment, source)) {
          return segment;
        }
      }
    }

    // if we got here, it's the end of the line (literally)
    return null;
  }

  private static PolyLine extractLine(Tuple tuple, Map<Tuple, List<Segment>> mappedSegments, boolean closed) {
    List<Tuple> output = new ArrayList<> ();
    Segment segment = null;
    // pull each segment in the line in-turn and hopscotch along it until all the segments in this
    // line have been processed
    while ((segment = getNextSegment (tuple, segment, mappedSegments, output)) != null) {
      tuple = Tuple.similar(segment.a, tuple) ? segment.b : segment.a;
    }
    return new PolyLine (output, closed);
  }

  private static Tuple findEndpoint (Map<Tuple, List<Segment>> mappedSegments) {
    // look for a tuple with only one entry first, these are endpoints
    List<Tuple> tuples = new ArrayList<> (mappedSegments.keySet());
    for (Tuple tuple: tuples) {
      if (mappedSegments.get(tuple).size() == 1) {
        return tuple;
      }
    }

    // if we got here, all the single entry endpoints are done and the remainder are closed loops.
    return null;
  }

  public static List<PolyLine> polyLinesFromSegments (List<Segment> segments) {
    List<PolyLine> output = new ArrayList<> ();
    // start by putting all the segments into a mapping table by their endpoints. this enables an
    // efficient query
    Map<Tuple, List<Segment>> mappedSegments = new HashMap<> ();
    for (Segment segment: segments) {
      // create or update a list for both ends of the segment, first a
      List<Segment> list = mappedSegments.getOrDefault(segment.a, new ArrayList<> ());
      list.add(segment);
      mappedSegments.put(segment.a, list);

      // then b
      list = mappedSegments.getOrDefault(segment.b, new ArrayList<> ());
      list.add(segment);
      mappedSegments.put(segment.b, list);
    }

    // now all the entries in the mapping table have one or two segments. we look for entries with
    // only one segment first, as they represent the open segments (one end of a line, and there
    // must be another end).
    Tuple endPoint;
    while ((endPoint = findEndpoint(mappedSegments)) != null) {
      output.add (extractLine (endPoint, mappedSegments, false));
    }

    // the remaining segments are all closed, meaning they are loops, so the mapping table for all
    // their endpoints will have exactly two segments, and we have to just pick one as a starting
    // point.
    while (mappedSegments.size() > 0) {
      endPoint = mappedSegments.keySet().stream().findFirst().get ();
      output.add (extractLine (endPoint, mappedSegments, true));
    }
    return output;
  }
}
