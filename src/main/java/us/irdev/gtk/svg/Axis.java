package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;

import static us.irdev.gtk.xyw.Tuple.PT;

public class Axis implements Element {
  @Override
  public String emit (Domain domain, Traits traits) {
    // emit gridlines using the current traits
    var builder = new StringBuilder();

    // emit the vertical axis line
    builder.append (new Line (PT (0, domain.min.y), PT(0, domain.max.y)).emit (domain, traits));

    // emit the horizontal axis line
    builder.append (new Line (PT (domain.min.x, 0), PT(domain.max.x, 0)).emit (domain, traits));

    return builder.toString();
  }

  @Override
  public Domain domain () {
    // the grid doesn't have its own domain, it has to be told what the domain is
    return new Domain ();
  }
}
