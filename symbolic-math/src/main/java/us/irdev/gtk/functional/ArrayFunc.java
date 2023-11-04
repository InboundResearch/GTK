package us.irdev.gtk.functional;

import java.lang.reflect.Array;

public class ArrayFunc {
  //------------------------------------------------------------------------------------------------
  /**
   * map an array to another array, possibly of a different type.
   */
  public static <A, B> B[] map(A[] input, Class<B> outputClass, Interfaces.MapInterfaceWithIndex<A, B> mapInterface) {
    @SuppressWarnings("unchecked")
    B[] output = (B[]) Array.newInstance (outputClass, input.length);
    for (int i = 0; i < input.length; ++i) {
      output[i] = mapInterface.act (i, input[i]);
    }
    return output;
  }

  /**
   * map variant with a map interface function that doesn't need the index.
   */
  public static <A, B> B[] map(A[] input, Class<B> outputClass, Interfaces.MapInterfaceNoIndex<A, B> mapInterface) {
    return map(input, outputClass, (i, b) -> mapInterface.act(b));
  }

  //------------------------------------------------------------------------------------------------
  public static <A, B> B reduce(A[] input, int start, B initialValue, Interfaces.ReduceWithIndex<A, B> reduceInterface) {
    B b = initialValue;
    for (int i = start; i < input.length; ++i) {
      b = reduceInterface.act (i, input[i], b);
    }
    return b;
  }
  public static <A, B> B reduce(A[] input, B initialValue, Interfaces.ReduceWithIndex<A, B> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  public static <A, B> B reduce(A[] input, int start, B initialValue, Interfaces.ReduceNoIndex<A, B> reduceInterface) {
    return reduce (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A, B> B reduce(A[] input, B initialValue, Interfaces.ReduceNoIndex<A, B> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  public static <A> double reduce(A[] input, int start, double initialValue, Interfaces.ReduceWithIndexDouble<A> reduceInterface) {
    double b = initialValue;
    for (int i = start; i < input.length; ++i) {
      b = reduceInterface.act (i, input[i], b);
    }
    return b;
  }

  public static <A> double reduce(A[] input, double initialValue, Interfaces.ReduceWithIndexDouble<A> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  public static <A> double reduce(A[] input, int start, double initialValue, Interfaces.ReduceNoIndexDouble<A> reduceInterface) {
    return reduce (input, start, initialValue, (i, a, b) -> reduceInterface.act (a, b));
  }

  public static <A> double reduce(A[] input, double initialValue, Interfaces.ReduceNoIndexDouble<A> reduceInterface) {
    return reduce (input, 0, initialValue, reduceInterface);
  }

  //------------------------------------------------------------------------------------------------
  /*
  public static <A> A[] filter(A[] input, Interfaces.Filter<A> filter) {
    List<A> tmp = new ArrayList<A> ();
    return null;
  }
  */
}
