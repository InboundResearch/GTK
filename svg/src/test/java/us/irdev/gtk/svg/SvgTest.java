package us.irdev.gtk.svg;

import us.irdev.gtk.io.Utility;
import us.irdev.gtk.xyw.Domain;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static us.irdev.gtk.xyw.Tuple.PT;
import static us.irdev.gtk.xyw.Tuple.VEC;

public class SvgTest {
  @Test
  public void testSvg() {
    Frame frame = new Frame (new Domain (-180, 180, -90, 90))
            .begin(new Traits (0.1, "#aaa", "none"))
            .element (new Grid (VEC(22.5, 22.5)))
            .end()
            .begin(new Traits(0.5, "#700", "none"))
            .line (PT(0, -10), PT(90, 80))
            .begin(new Traits(0.725, "#007", "none"))
            .poly (PT(-50, -50), PT(-30, 40), PT(-10, -30), PT(10, 20), PT(30, -10), PT(50, 0));

    String x = Paths.get ("output", "svgtest.").toString();
    Utility.writeFile (x + "svg", frame.emitSvg("test", 800));
    Utility.writeFile (x + "html", frame.emitHtml("test", 800));
  }
}
