package us.irdev.gtk.functional;

public class Interfaces {
  public static interface MapInterfaceWithIndex<A, B> {
    B act (int i, A a);
  }

  public static interface MapInterfaceNoIndex<A, B> {
    B act (A a);
  }

  //------------------------------------------------------------------------------------------------
  public static interface ReduceWithIndex<A, B> {
    B act (int i, A a, B value);
  }

  public static interface ReduceNoIndex<A, B> {
    B act (A a, B value);
  }

  // special cases for return double, boolean, and int, since java can't do generics over fundamental types
  public static interface ReduceWithIndexDouble<A> {
    double act (int i, A a, double value);
  }

  public static interface ReduceNoIndexDouble<A> {
    double act (A a, double value);
  }

  public static interface ReduceWithIndexInt<A> {
    int act (int i, A a, int value);
  }

  public static interface ReduceNoIndexInt<A> {
    int act (A a, int value);
  }

  //------------------------------------------------------------------------------------------------
  public static interface FilterWithIndex<A> {
    boolean check (int i, A a);
  }

  public static interface FilterNoIndex<A> {
    boolean check (A a);
  }

  //------------------------------------------------------------------------------------------------
  public static interface FillWithIndex<A> {
    A act (int i);
  }


}
