import java.io.*;
import java.util.*;

public class InlineMethodTest {
  static int failed = 0;
  static void check(boolean ok, String msg) { if (!ok) { failed++; System.out.println("FAIL: " + msg); } }
  static String capture(Runnable r) {
    PrintStream old = System.out;
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    System.setOut(new PrintStream(b, true));
    try { r.run(); } finally { System.setOut(old); }
    return b.toString().replace("\r\n", "\n");
  }
  public static void main(String[] args) {
    try { run(); } catch (Throwable t) { failed++; System.out.println("FAIL: exception " + t); }
    if (failed == 0) System.out.println("PASS"); else { System.out.println("FAILED " + failed); System.exit(1); }
  }
  static void run() throws Exception {
    check(new PizzaDelivery(0).getRating() == 1, "0 late deliveries should rate 1");
    check(new PizzaDelivery(5).getRating() == 1, "5 late deliveries should rate 1");
    check(new PizzaDelivery(6).getRating() == 2, "6 late deliveries should rate 2");
    check(new PizzaDelivery(100).getRating() == 2, "100 late deliveries should rate 2");
  }
}
