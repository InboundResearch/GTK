package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;

public interface Element {
  String emit (Domain domain, Traits traits);
  Domain domain ();
}
