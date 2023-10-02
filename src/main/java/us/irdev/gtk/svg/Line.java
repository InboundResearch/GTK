package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Segment;
import us.irdev.gtk.xyw.Tuple;

public class Line implements Element {
  private final Tuple a, b;

  public Line (Tuple a, Tuple b) {
    this.a = a; this.b = b;
  }

  public Line (Segment segment) {
    this (segment.a, segment.b);
  }

  @Override
  public String emit(Domain domain, Traits traits) {
    return String.format ("<line x1=\"%f\" y1=\"%f\" x2=\"%f\" y2=\"%f\" fill=\"none\" stroke=\"%s\" stroke-width=\"%f%%\"/>\n", a.x, a.y, b.x, b.y, traits.strokeColor, traits.strokeWeight);
  }

  @Override
  public Domain domain () {
    return new Domain (a, b);
  }
}
