package us.irdev.gtk.xyw;

import us.irdev.gtk.functional.ListFunc;

import java.util.*;

import static us.irdev.gtk.xyw.Tuple.VEC;

public class Grid<T> {
    public final Domain domain;
    public final Tuple spacing;
    private final int width;
    private final int height;
    private final Map<Integer, Set<T>> cells;

    public Grid (Domain domain, Tuple spacing) {
        this.domain = domain;
        this.spacing = spacing;
        var size = domain.size().hquotient (spacing).ceil ();
        width = (int) size.x;
        height = (int) size.y;
        cells = new HashMap<> ();
    }

    private static Tuple computeSpacing (Domain domain, int n) {
        var aspectRatio = domain.aspectRatio();
        var sqrtn = Math.sqrt(n);
        var dimX = 1.0 / Math.ceil (sqrtn * domain.aspectRatio());
        var dimY = 1.0 / Math.ceil (sqrtn);
        return domain.size().hproduct (VEC(dimX, dimY));
    }

    public Grid (Domain domain, int n) {
        this (domain, computeSpacing (domain, n));
    }

    protected int hash (Tuple pt) {
        var cell = pt.subtract(domain.min).hquotient (spacing).floor();
        return (int) cell.x + ((int) cell.y * width);
    }

    public Set<T> getAt (Tuple pt) {
        var result = cells.get (hash (pt));
        return (result != null) ? Collections.unmodifiableSet (result) : Collections.emptySet();
    }

    public void putAt (Tuple pt, T value) {
        cells.computeIfAbsent(hash (pt), k -> new HashSet<>()).add(value);
    }

    public List<Domain> enumerate () {
        var results = new ArrayList<Domain> ();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var pt = domain.min.add(spacing.hproduct (VEC(x, y)));
                results.add (new Domain (pt, pt.add(spacing)));
            }
        }
        return results;
    }

    public List<Domain> enumerate (Domain input) {
        var results = new ArrayList<Domain> ();
        input = Domain.intersection (domain, input);
        var bottomLeft = input.min.subtract(domain.min).hquotient(spacing).floor();
        var topRight = input.max.subtract(domain.min).hquotient(spacing).ceil();
        for (var y = (int) bottomLeft.y; y < (int) topRight.y; y++) {
            for (var x = (int) bottomLeft.x; x < (int) topRight.x; x++) {
                var pt = domain.min.add(spacing.hproduct (VEC(x, y)));
                results.add (new Domain (pt, pt.add(spacing)));
            }
        }
        return results;
    }

    public double occupancy () {
        return cells.size() / (double) (width * height);
    }
}
