package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.PolyLine;
import us.irdev.gtk.xyw.Tuple;

public class Poly implements Element {
  private final PolyLine polyine;

  // a polygon if closed, a polyline if not?
  public Poly(PolyLine polyline) {
    this.polyine = polyline;
  }

  @Override
  public String emit (Domain domain, Traits traits) {
    // <polygon points="200,10 250,190 160,210" style="fill:lime;stroke:purple;stroke-width:1" />
    var builder = new StringBuilder();
    builder.append(String.format("<poly%s fill=\"%s\" stroke=\"%s\" stroke-width=\"%f%%\" stroke-linejoin=\"round\" stroke-linecap=\"round\" points=\"", polyine.isClosed() ? "gon" : "line", polyine.isClosed() ? traits.fillColor : "none", traits.strokeColor, traits.strokeWeight));
    String spacer = "";
    for (Tuple point: polyine.getPoints()) {
      builder.append(String.format ("%s%f,%f", spacer, point.x, point.y));
      spacer = " ";
    }
    builder.append("\"/>\n");
    return builder.toString();
  }

  @Override
  public Domain domain () {
    return polyine.domain();
  }
}
