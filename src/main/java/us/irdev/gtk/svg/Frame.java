package us.irdev.gtk.svg;

import us.irdev.gtk.xyw.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static us.irdev.gtk.xyw.Tuple.VEC;

public class Frame {
  private final List<Bundle> bundles;
  private final Stack<Traits> traitsStack;
  private final Domain domain;
  private Bundle currentBundle;

  public Frame(Domain domain) {
    bundles = new ArrayList<>();
    traitsStack = new Stack<>();
    traitsStack.add (new Traits ());
    this.domain = domain;
    currentBundle = null;
  }

  public Frame() {
    this(null);
  }

  public Frame element (Element element) {
    if (currentBundle == null) {
      currentBundle = new Bundle (traitsStack.peek());
      bundles.add (currentBundle);
    }
    currentBundle.add(element);
    return this;
  }

  public Frame line (Tuple a, Tuple b) {
    return element (new Line (a, b));
  }

  public Frame line (Segment segment) {
    return element (new Line (segment));
  }

  public Frame poly (PolyLine polyline) {
    return element (new Poly (polyline));
  }

  public Frame poly (Tuple... points) {
    return poly (new PolyLine (points, false));
  }

  public Frame poly (List<Segment> segments) {
    List<PolyLine> polylines = PolyLine.polyLinesFromSegments(segments);
    for (PolyLine polyline: polylines) {
      poly (polyline);
    }
    return this;
  }

  public Frame poly (Segments segments) {
    return poly (segments.segments);
  }

  public Frame points (double size, Tuple... points) {
    return element (new Points (size, points));
  }

  public Frame box (Domain domain) {
    return element (new Box(domain));
  }

  public Frame begin(Traits traits) {
    traitsStack.push (traits);
    currentBundle = null;
    return this;
  }

  public Frame end () {
    traitsStack.pop ();
    currentBundle = null;
    return this;
  }

  public String emitSvg(String title, int svgWidth) {
    var builder = new StringBuilder ();

    // get the domain of what we want to draw
    Domain domain = this.domain;
    if (domain == null) {
      domain = new Domain ();
      for (Bundle bundle : bundles) {
        domain = Domain.union (domain, bundle.domain ());
      }
    }

    // adjust the domain to have a little space in our drawing
    Tuple domainSize = domain.size();
    double padding = Math.max(domainSize.x, domainSize.y) * 0.05;
    Domain documentDomain = domain.pad(VEC(padding, padding));

    // compute the aspect ratio so we can size the svg element
    Tuple documentSize = documentDomain.size ();
    double aspectRatio = documentSize.y / documentSize.x;
    var svgHeight = (int) (svgWidth * aspectRatio);

    // emit the svg with domain, and a transform to draw the coordinate axis the right way up
    builder.append (String.format ("<svg version=\"1.1\" xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\"", svgWidth, svgHeight))
            .append (String.format (" viewBox=\"%f %f %f %f\"", documentDomain.min.x, documentDomain.min.y, documentSize.x, documentSize.y))
            .append(" style=\"background-color:#fff;\"")
            .append(">\n");
    builder.append (String.format("<g transform=\"translate(0,%f) scale(1,-1)\">\n", domain.max.y + domain.min.y));

    // emit all the drawing bundles
    for (Bundle bundle: bundles) {
      builder.append (bundle.emit(domain));
    }


    // close the svg
    builder.append("</g>\n</svg>\n");

    // return the built string
    return builder.toString();
  }

  public String emitHtml(String title, int svgWidth) {
    var builder = new StringBuilder ();

    // emit an html wrapper doc
    return new StringBuilder ()
            .append ("<!doctype html><html lang=\"en-us\"><head><meta charset=utf-8><title>")
            .append (title)
            .append (String.format ("</title></head><body style=\"width: %d px; margin: 0; display: flex; justify-content: center; align-items: center; background-color: #c7c7c7;\">", svgWidth))
            .append(emitSvg(title, svgWidth))
            .append("</body>")
            .toString();
  }

}
