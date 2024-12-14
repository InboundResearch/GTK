package us.irdev.gtk.svg;

public class Traits {
  public final double strokeWeight;
  public final String strokeColor;
  public final String fillColor;
  public final double opacity;

  private final static double DEFAULT_STROKE_WEIGHT = 0.1;
  private final static double DEFAULT_OPACITY = 1.0;

  // stroke wights are given as a percentage, so 1 = 1%

  public Traits (double strokeWeight, String strokeColor, String fillColor, double opacity) {
    this.strokeWeight = strokeWeight;
    this.strokeColor = strokeColor;
    this.fillColor = fillColor;
    this.opacity = opacity;
  }

  public Traits (double strokeWeight, String strokeColor, String fillColor) {
    this (strokeWeight, strokeColor, fillColor, DEFAULT_OPACITY);
  }

  public Traits () {
    this (DEFAULT_STROKE_WEIGHT, "black", "none");
  }

}
