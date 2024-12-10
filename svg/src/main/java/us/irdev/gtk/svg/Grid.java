package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.Domain;
import us.irdev.gtk.xyw.Tuple;

import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class Grid implements Element {
  private static final int DEFAULT_STEP_COUNT = 10;
  private final int defaultStepCount;
  private final Tuple spacing;
  private final Tuple origin;

  public Grid (Tuple spacing, Tuple origin, int defaultStepCount) {
    // condition the input, default to whatever spacing was passed in for spacing, and fix the
    // origin if it wasn't supplied
    this.spacing = spacing;
    this.origin = (origin != null) ? origin : Tuple.ORIGIN;
    this.defaultStepCount = defaultStepCount;
  }

  public Grid (Tuple spacing, Tuple origin) {
    this(spacing, origin, DEFAULT_STEP_COUNT);
  }

  public Grid (Tuple spacing) {
    this(spacing, null, DEFAULT_STEP_COUNT);
  }

  public Grid (int defaultStepCount) {
    this(null, null, defaultStepCount);
  }

  public Grid () {
    this(null, null, DEFAULT_STEP_COUNT);
  }

  @Override
  public String emit (Domain domain, Traits traits) {
    // condition the spacing, if the result should be dynamically computed
    Tuple spacing = (this.spacing != null) ? this.spacing : domain.size().scale (1.0 / defaultStepCount);

    // emit gridlines using the current traits
    StringBuilder builder = new StringBuilder();

    // compute the size of the domain, the bottom left of the grid, and the number of steps
    Tuple size = domain.size();
    Tuple bottomLeft = domain.min.hquotient (spacing).floor().hproduct (spacing);
    Tuple stepCount = size.hquotient (spacing).ceil();

    // emit the vertical axis lines, starting with the first grid line >= left of the domain
    for (int i = 0; i <= (int) stepCount.x; ++i) {
      double x = bottomLeft.x + (i * spacing.x);
      builder.append (new Line (PT (x, domain.min.y), PT(x, domain.max.y)).emit (domain, traits));
    }

    // emit the horizontal axis lines
    for (int i = 0; i <= (int) stepCount.y; ++i) {
      double y = bottomLeft.y + (i * spacing.y);
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
