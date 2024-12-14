package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;

public class Box implements Element {
  private final Domain domain;

  public Box (Domain domain) {
    this.domain = domain;
  }

  @Override
  public String emit (Domain domainIn, Traits traits) {
    // <rect width="300" height="100" style="fill:rgb(0,0,255);stroke-width:3;stroke:rgb(0,0,0)" />
    Tuple size = domain.size();
    return String.format ("<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%f%%\" opacity=\"%f\"/>\n", domain.min.x, domain.min.y, size.x, size.y, traits.fillColor, traits.strokeColor, traits.strokeWeight, traits.opacity);
  }

  @Override
  public Domain domain () {
    return domain;
  }
}
