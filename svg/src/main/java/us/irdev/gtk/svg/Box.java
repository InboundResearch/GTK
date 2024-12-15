package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;

public class Box implements Element {
  private final Domain domain;
  private final String title;

  public Box (Domain domain, String title) {
    this.domain = domain;
    this.title = title;
  }

  @Override
  public String emit (Domain domainIn, Traits traits) {
    // <rect width="300" height="100" style="fill:rgb(0,0,255);stroke-width:3;stroke:rgb(0,0,0)" />
    var size = domain.size();
    var preamble = String.format ("<rect x=\"%f\" y=\"%f\" width=\"%f\" height=\"%f\" fill=\"%s\" stroke=\"%s\" stroke-width=\"%f%%\" opacity=\"%f\"", domain.min.x, domain.min.y, size.x, size.y, traits.fillColor, traits.strokeColor, traits.strokeWeight, traits.opacity);
    return (title != null) ? preamble + "><title>" + title + "</title></rect>\n" : preamble + "/>\n";
  }

  @Override
  public Domain domain () {
    return domain;
  }
}
