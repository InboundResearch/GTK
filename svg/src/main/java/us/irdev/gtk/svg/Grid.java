package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;

import static us.irdev.gtk.xyw.Tuple.PT;

public class Grid implements Element {
  private int xSteps, ySteps;

  public Grid (int xSteps, int ySteps) {
    this.xSteps = xSteps; this.ySteps = ySteps;
  }

  @Override
  public String emit (Domain domain, Traits traits) {
    // emit gridlines using the current traits
    var builder = new StringBuilder();

    Tuple size = domain.size();

    // emit the vertical axis lines
    double spacing = size.x / xSteps;
    for (int i = 0; i <= xSteps; ++i) {
      double x = domain.min.x + (i * spacing);
      builder.append (new Line (PT (x, domain.min.y), PT(x, domain.max.y)).emit (domain, traits));
    }

    // emit the horizontal axis lines
    spacing = size.y / ySteps;
    for (int i = 0; i <= ySteps; ++i) {
      double y = domain.min.y + (i * spacing);
      builder.append (new Line (PT (domain.min.x, y), PT(domain.max.x, y)).emit (domain, traits));
    }

    return builder.toString();
  }

  @Override
  public Domain domain () {
    // the grid doesn't have its own domain, it has to be told what the domain is
    return new Domain ();
  }
}
