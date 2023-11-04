package us.irdev.gtk.xyw;

import static us.irdev.gtk.xyw.Tuple.VEC;

public class At {
  public final Tuple xy;
  public final double f;
  public final Tuple dxdy;
  public At(Tuple xy, double f, double dx, double dy) {
    this.xy = xy;
    this.f = f;
    this.dxdy = Tuple.VEC (dx, dy);
  }
}
