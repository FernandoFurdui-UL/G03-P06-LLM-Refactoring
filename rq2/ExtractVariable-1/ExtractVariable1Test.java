import java.io.*;
import java.util.*;

public class ExtractVariable1Test {
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
    String[] platforms = {"MacOS", "mac", "Windows"};
    String[] browsers = {"IE11", "ie", "Chrome"};
    int[] resizes = {0, 1, 5};
    boolean[] inits = {true, false};
    for (String p : platforms) for (String b : browsers) for (int r : resizes) for (boolean i : inits) {
      Banner banner = new Banner(p, b, r, i);
      banner.renderBanner();
      boolean expected = p.toUpperCase().indexOf("MAC") > -1 && b.toUpperCase().indexOf("IE") > -1 && i && r > 0;
      check(banner.rendered == expected, "rendered for (" + p + "," + b + "," + r + "," + i + ") should be " + expected);
    }
    // Diagnostic only (not gating): the original short-circuits, so a null browser on a non-Mac platform must not throw.
    try { new Banner("Windows", null, 1, true).renderBanner(); System.out.println("DIAGNOSTIC: null-browser case ok"); }
    catch (NullPointerException e) { System.out.println("DIAGNOSTIC: null-browser case throws NullPointerException (original does not)"); }
  }
}
