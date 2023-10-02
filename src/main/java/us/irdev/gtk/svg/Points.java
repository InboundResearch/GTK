package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;

public class Points implements Element {
  private final double size;
  private final Tuple[] points;

  public Points (double size, Tuple... points) {
    this.size = size * 0.5;
    this.points = points;
  }

  @Override
  public String emit (Domain domain, Traits traits) {
    // <circle cx="50" cy="50" r="50" />
    StringBuilder builder = new StringBuilder();
    for (Tuple point: points) {
      builder.append (String.format ("<circle cx=\"%f\" cy=\"%f\" r=\"%f\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%f%%\"/>\n", point.x, point.y, size, traits.fillColor, traits.strokeColor, traits.strokeWeight));
    }
    return builder.toString();
  }

  @Override
  public Domain domain () {
    return new Domain(points);
  }
}
