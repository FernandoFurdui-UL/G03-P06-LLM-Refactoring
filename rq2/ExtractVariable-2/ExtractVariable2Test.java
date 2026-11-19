import java.io.*;
import java.util.*;

public class ExtractVariable2Test {
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
    boolean[] tf = {true, false};
    for (boolean fc : tf) for (boolean tc : tf) for (boolean er : tf) for (boolean same : tf) {
      BannerRenderer r = new BannerRenderer();
      r.frame.isChanged = fc;
      r.target.isChanged = tc;
      r.experiment.running = er;
      r.frame.size = 10;
      r.screen.size = same ? 10 : 20;
      r.renderBanner();
      boolean expected = ((fc || tc) || er) && same;
      check(r.printed == expected, "printed for (frame=" + fc + ",target=" + tc + ",experiment=" + er + ",fullscreen=" + same + ") should be " + expected);
    }
  }
}
