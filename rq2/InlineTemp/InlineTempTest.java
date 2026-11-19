import java.io.*;
import java.util.*;

public class InlineTempTest {
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
    Pricing p = new Pricing();
    check(!p.hasDiscount(new Order(999)), "999 should not get a discount");
    check(!p.hasDiscount(new Order(1000)), "1000 should not get a discount");
    check(p.hasDiscount(new Order(1000.5)), "1000.5 should get a discount");
    check(p.hasDiscount(new Order(5000)), "5000 should get a discount");
  }
}
