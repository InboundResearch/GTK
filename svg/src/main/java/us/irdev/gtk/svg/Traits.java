package us.irdev.gtk.svg;

public class Traits {
  public final double strokeWeight;
  public final String strokeColor;
  public final String fillColor;

  // stroke wights are given as a percentage, so 1 = 1%

  public Traits () {
    strokeWeight = 0.1;
    strokeColor = "black";
    fillColor = "none";
  }

  public Traits (double strokeWeight, String strokeColor, String fillColor) {
    this.strokeWeight = strokeWeight;
    this.strokeColor = strokeColor;
    this.fillColor = fillColor;
  }
}
