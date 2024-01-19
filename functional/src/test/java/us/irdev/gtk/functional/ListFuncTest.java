package us.irdev.gtk.functional;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListFuncTest {
  @Test
  public void testMap () {
    List<String> input = new ArrayList<>();
    for (int i = 0; i < 26; ++i) {
      input.add(String.valueOf('A' + i));
    }

    List<String> output = ListFunc.map(input, (i, a) -> a + String.valueOf('a' + i));
    assertEquals(input.size(), output.size());
    for (int i = 0; i < 26; ++i) {
      assertEquals(String.valueOf('A' + i) + String.valueOf('a' + i), output.get(i));
    }

    output = ListFunc.map(input, a -> a + a);
    assertEquals(input.size(), output.size());
    for (int i = 0; i < 26; ++i) {
      String string = String.valueOf('A' + i);
      String expect = string + string;
      assertEquals(expect, output.get(i));
    }
  }

  @Test
  public void testReduce () {
    // 1 .. 50, 101 .. 150
    List<Integer> input1 = new ArrayList<>();
    List<Integer> input2 = new ArrayList<>();
    for (int i = 0; i < 50; ++i) {
      input1.add(i + 1);
      input2.add(i + 101);
    }

    boolean outputBool = ListFunc.reduce(input1, 0, true, (i, a, b) -> b && (Objects.equals (a, input1.get (i))));
    assertTrue (outputBool);

    outputBool = ListFunc.reduce(input1, 0, true, (i, a, b) -> b && (input2.get(i) == (a + 100)));
    assertTrue (outputBool);

    Integer output = ListFunc.reduce(input1, 0, 0, (i, a, b) -> b + a);
    assertEquals (1275, output);

    output = ListFunc.reduce(input1, 0, 0, (a, b) -> b + a);
    assertEquals (1275, output);

    output = ListFunc.reduce(input1, 0, (i, a, b) -> b + a);
    assertEquals (1275, output);

    output = ListFunc.reduce(input1, 1, (i, a, b) -> b + a);
    assertEquals (1276, output);

    output = ListFunc.reduce(input1, 0, (a, b) -> b + a);
    assertEquals (1275, output);

    output = ListFunc.reduce(input1, 1, (a, b) -> b + a);
    assertEquals (1276, output);

    output = ListFunc.reduce(input1, (i, a, b) -> b + a + i);
    assertEquals (2500, output);

    output = ListFunc.reduce(input1, Integer::sum);
    assertEquals (1275, output);
  }

  @Test
  public void testReduceInt () {
    List<Integer> input = ListFunc.fill (50, i -> i + 1);

    int output = ListFunc.reduceInt(input, 0, (a, b) -> b + 1);
    assertEquals (50, output);

    output = ListFunc.reduceInt(input, 0, (i, a, b) -> b + a + i);
    assertEquals (2500, output);
  }

  @Test
  public void testReduceDouble () {
    List<Integer> input = ListFunc.fill (50, i -> i + 1);

    double output = ListFunc.reduceDouble(input, 2.0, (a, b) -> b + 2.0);
    assertEquals (102.0, output);

    output = ListFunc.reduceDouble(input, 2.0, (i, a, b) -> b + 2.0 + i);
    assertEquals (1225 + 102.0, output);
  }

  @Test
  public void testReduceBool () {
    List<Integer> input1 = ListFunc.fill (50, i -> i + 1);
    List<Integer> input2 = ListFunc.fill (50, i -> i + 101);

    boolean output = ListFunc.reduceBool(input1, 0, true, (i, a, b) -> b && (input2.get(i) == (a + 100)));
    assertTrue (output);

    output = ListFunc.reduceBool(input1, true, (i, a, b) -> b && (input2.get(i) == (a + 100)));
    assertTrue (output);
  }

  @Test
  public void testFilter () {
    List<Integer> input = ListFunc.fill (50, i -> i + 1);
    List<Integer> output1 = ListFunc.filter(input, a -> a % 2 == 1);
    assertEquals(25, output1.size());
    boolean output = ListFunc.reduceBool(output1, true, (a, b) -> b && ((a % 2) == 1));
    assertTrue (output);
  }

  @Test
  public void testSplit () {
    List<Integer> input = ListFunc.fill (50, i -> i + 1);
    List<Integer>[] splits = ListFunc.split(input, a -> a % 2 == 1);
    assertEquals(25, splits[0].size());
    assertEquals(25, splits[0].size());
    assertTrue(ListFunc.reduceBool(splits[0], true, (a, b) -> b && ((a % 2) == 0)));
    assertTrue(ListFunc.reduceBool(splits[1], true, (a, b) -> b && ((a % 2) == 1)));
  }

  @Test
  public void testFill () {
    List<Integer> input = ListFunc.fill (50, i -> i + 1);
  }
}
