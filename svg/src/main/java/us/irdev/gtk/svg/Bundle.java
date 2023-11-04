package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;

import java.util.ArrayList;
import java.util.List;

public class Bundle {
  public Traits traits;
  public List<Element> elements;

  public Bundle (Traits traits) {
    this.traits = traits;
    elements = new ArrayList<> ();
  }

  public void add (Element element) {
    elements.add(element);
  }

  public String emit(Domain domain) {
    var builder = new StringBuilder();
    for(Element element: elements) {
      builder.append (element.emit (domain, traits));
    }
    return builder.toString();
  }

  public Domain domain () {
    var domain = new Domain();
    for(Element element: elements) {
      domain = Domain.union (domain, element.domain());
    }
    return domain;
  }
}
