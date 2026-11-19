import java.io.*;
import java.util.*;

public class ExtractMethod3Test {
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
    List<Product> ps = Arrays.asList(new Product(2, 10.0), new Product(1, 5.0));
    check(Math.abs(new Order(new User("US"), ps).calculateTotal() - 21.25) < 1e-9, "US total should be 21.25");
    check(Math.abs(new Order(new User("RU"), ps).calculateTotal() - 18.75) < 1e-9, "RU total should be 18.75");
    check(Math.abs(new Order(new User("CN"), ps).calculateTotal() - 22.5) < 1e-9, "CN total should be 22.5");
    check(Math.abs(new Order(new User("IE"), ps).calculateTotal() - 25.0) < 1e-9, "IE (no discount) total should be 25.0");
    check(Math.abs(new Order(new User("US"), new ArrayList<Product>()).calculateTotal()) < 1e-9, "empty order should total 0");
  }
}
