package us.irdev.gtk.geography;

import org.junit.jupiter.api.Test;
import us.irdev.gtk.functional.ListFunc;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static us.irdev.gtk.xyw.Tuple.PT;

public class JurisdictionTest {

    @Test
    public void testJurisdiction() {
        // complete test, with world, usa, states
        var worldGeoJsonList = GeoJson.read (Paths.get("data", "world.json").toString());
        var usaGeoJsonList = GeoJson.read (Paths.get("data", "usa.json").toString());
        var usaStatesGeoJsonList = GeoJson.read (Paths.get("data", "usa_states.json").toString());
        var usaCountiesGeoJsonList = GeoJson.read (Paths.get("data", "usa_counties.json").toString());

        // condition the counties into sub lists by state
        var statesMap = new HashMap<Integer, List<Jurisdiction>>();
        usaCountiesGeoJsonList.forEach(geo -> statesMap.computeIfAbsent (geo.properties.getInteger("state"), k -> new ArrayList<>()).add (new Jurisdiction (geo)));

        // create the states
        var statesJurisdictions = ListFunc.map (usaStatesGeoJsonList, geo -> new Jurisdiction(geo, statesMap.get(geo.properties.getInteger ("state"))));
        //var statesJurisdictions = ListFunc.map (usaStatesGeoJsonList, geo -> new Jurisdiction(geo));

        // simple, create the usa and the world
        var usaJurisdiction = new Jurisdiction (usaGeoJsonList.get (0), statesJurisdictions);
        var worldJurisdiction = new Jurisdiction (worldGeoJsonList.get (0), List.of (usaJurisdiction));

        usaJurisdiction.toSvg ("usa-jurisdiction");

        /*
        // houston
        assertTrue (geoJson.contains (PT (-95.3701, 29.7601)));
        // austin
        assertTrue (geoJson.contains (PT (-97.7431, 30.2672)));
        // san antonio
        assertTrue (geoJson.contains (PT (-98.4946, 29.4252)));
        // mesquite bay
        assertTrue (geoJson.contains (PT (-96.83564, 28.09093)));
        // new orleans
        assertFalse (geoJson.contains (PT (-90.0758, 29.9509)));
        // mexico city
        assertFalse (geoJson.contains (PT (-99.1332, 19.4326)));
        // phoenix
        assertFalse (geoJson.contains (PT (-112.0740, 33.4484)));
        // denver
        assertFalse (geoJson.contains (PT (-104.9903, 39.7392)));
        // gulf of mexico
        assertFalse (geoJson.contains (PT (-95.31868, 27.93636)));
        */
        // houston
        var enumeration = worldJurisdiction.enumerate (PT (-95.3701, 29.7601));
        assert enumeration != null;
        assertEquals (4, enumeration.size());
        assertEquals("World", enumeration.get (0).properties().getString("name"));
        assertEquals("USA", enumeration.get (1).properties().getString("name"));
        assertEquals("Texas", enumeration.get (2).properties().getString("name"));
        assertEquals("Harris", enumeration.get (3).properties().getString("name"));

        // baltimore inner harbor
        enumeration = worldJurisdiction.enumerate (PT (-76.61198, 39.28589));
        assert enumeration != null;
        assertEquals (4, enumeration.size());
        assertEquals("World", enumeration.get (0).properties().getString("name"));
        assertEquals("USA", enumeration.get (1).properties().getString("name"));
        assertEquals("Maryland", enumeration.get (2).properties().getString("name"));
        assertEquals("Baltimore", enumeration.get (3).properties().getString("name"));
    }
}
