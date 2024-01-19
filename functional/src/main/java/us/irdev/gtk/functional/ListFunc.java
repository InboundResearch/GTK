package us.irdev.gtk.functional;

import java.util.ArrayList;
import java.util.List;

/**
 * java provides the stream interfaces that contain map, reduce, and filtering capabilities. we find
 * they are limited in ways that interfere with the goal of reducing code and building efficient
 * operations. these map, reduce, filter, and split operations are better suited to the way we work
 * with lists, most notably having the freedom to use the first element of a list as the initial
 * value, and providing additional functionality like split.
 */
public class ListFunc {
  //------------------------------------------------------------------------------------------------
  /**
   * map an array to another array, possibly of a different type.
   */
  public static <A, B> List<B> map(List<A> input, Interfaces.MapInterfaceWithIndex<A, B> mapInterface) {
    List<B> output = new ArrayList<B> ();
    for (int i = 0; i < input.size(); ++i) {
      output.add (mapInterface.act (i, input.get (i)));
    }
    return output;
  }

  /**
   * map variant with a map interface function that doesn't need the index.
   */
  public static <A, B> List<B> map(List<A> input, Interfaces.MapInterfaceNoIndex<A, B> mapInterface) {
    return map(input, (i, b) -> mapInterface.act(b));
  }

  //------------------------------------------------------------------------------------------------
  public static <A, B> B reduce(List<A> input, int start, B initialValue, Interfaces.ReduceWithIndex<A, B> reduceInterface) {
    B b = initialValue;
    for (int i = start; i < input.size(); ++i) {
      b = reduceInterface.act (i, input.get(i), b);
    }
    return b;
  }
  public static <A, B> B reduce(List<A> input, B initialValue, Interfaces.ReduceWithIndex<A, B> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  public static <A, B> B reduce(List<A> input, int start, B initialValue, Interfaces.ReduceNoIndex<A, B> reduceInterface) {
    return reduce (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A, B> B reduce(List<A> input, B initialValue, Interfaces.ReduceNoIndex<A, B> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  @SuppressWarnings("unchecked")
  public static <A, B> B reduce(List<A> input, Interfaces.ReduceNoIndex<A, B> reduceInterface) {
    return reduce (input, 1, (B) input.get(0), reduceInterface);
  }

  @SuppressWarnings("unchecked")
  public static <A, B> B reduce(List<A> input, Interfaces.ReduceWithIndex<A, B> reduceInterface) {
    return reduce (input, 1, (B) input.get(0), reduceInterface);
  }

  public static <A> double reduceDouble(List<A> input, int start, double initialValue, Interfaces.ReduceWithIndexDouble<A> reduceInterface) {
    double b = initialValue;
    for (int i = start; i < input.size(); ++i) {
      b = reduceInterface.act (i, input.get(i), b);
    }
    return b;
  }

  public static <A> double reduceDouble(List<A> input, double initialValue, Interfaces.ReduceWithIndexDouble<A> reduceInterface) {
    return reduceDouble (input, 0, initialValue, reduceInterface);
  }

  public static <A> double reduceDouble(List<A> input, int start, double initialValue, Interfaces.ReduceNoIndexDouble<A> reduceInterface) {
    return reduceDouble (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A> double reduceDouble(List<A> input, double initialValue, Interfaces.ReduceNoIndexDouble<A> reduceInterface) {
    return reduceDouble (input, 0, initialValue, reduceInterface);
  }


  public static <A> int reduceInt(List<A> input, int start, int initialValue, Interfaces.ReduceWithIndexInt<A> reduceInterface) {
    int b = initialValue;
    for (int i = start; i < input.size(); ++i) {
      b = reduceInterface.act (i, input.get(i), b);
    }
    return b;
  }

  public static <A> int reduceInt(List<A> input, int initialValue, Interfaces.ReduceWithIndexInt<A> reduceInterface) {
    return reduceInt (input, 0, initialValue, reduceInterface);
  }

  public static <A> int reduceInt(List<A> input, int start, int initialValue, Interfaces.ReduceNoIndexInt<A> reduceInterface) {
    return reduceInt (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A> int reduceInt(List<A> input, int initialValue, Interfaces.ReduceNoIndexInt<A> reduceInterface) {
    return reduceInt (input, 0, initialValue, reduceInterface);
  }





  public static <A> boolean reduceBool(List<A> input, int start, boolean initialValue, Interfaces.ReduceWithIndexBool<A> reduceInterface) {
    boolean b = initialValue;
    for (int i = start; i < input.size(); ++i) {
      b = reduceInterface.act (i, input.get(i), b);
    }
    return b;
  }

  public static <A> boolean reduceBool(List<A> input, boolean initialValue, Interfaces.ReduceWithIndexBool<A> reduceInterface) {
    return reduceBool (input, 0, initialValue, reduceInterface);
  }

  public static <A> boolean reduceBool(List<A> input, int start, boolean initialValue, Interfaces.ReduceNoIndexBool<A> reduceInterface) {
    return reduceBool (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A> boolean reduceBool(List<A> input, boolean initialValue, Interfaces.ReduceNoIndexBool<A> reduceInterface) {
    return reduceBool (input, 0, initialValue, reduceInterface);
  }

  //------------------------------------------------------------------------------------------------
  public static <A> List<A> filter(List<A> input, Interfaces.FilterWithIndex<A> filter) {
    List<A> output = new ArrayList<A> ();
    for (int i = 0; i < input.size(); ++i) {
      A entry = input.get(i);
      if (filter.check (i, entry)) {
        output.add (entry);
      }
    }
    return output;
  }

  public static <A> List<A> filter(List<A> input, Interfaces.FilterNoIndex<A> filter) {
    return filter (input, (i, entry) -> filter.check(entry));
  }

  public static <A> List<A>[] split(List<A> input, Interfaces.FilterWithIndex<A> filter) {
    @SuppressWarnings("unchecked")
    List<A>[] output = new List[2];
    output[0] = new ArrayList<A> ();
    output[1] = new ArrayList<A> ();
    for (int i = 0; i < input.size(); ++i) {
      A entry = input.get(i);
      output[filter.check (i, entry) ? 1 : 0].add (entry);
    }
    return output;
  }

  public static <A> List<A>[] split(List<A> input, Interfaces.FilterNoIndex<A> filter) {
    return split (input, (i, entry) -> filter.check(entry));
  }

  //------------------------------------------------------------------------------------------------
  public static <A> List<A> fill(int n, Interfaces.FillWithIndex<A> filler) {
    List<A> output = new ArrayList<>(n);
    for (int i = 0; i < n; ++i) {
      output.add (filler.act (i));
    }
    return output;
  }
}
