import java.io.*;
import java.util.*;

public class ExtractMethod1Test {
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
    String out = capture(() -> new Invoice("Ann", 12.5).printOwing());
    check(out.equals("*** Customer Owes ***\nname: Ann\namount: 12.5\n"), "printOwing output was: " + out.replace("\n", "|"));
    String out2 = capture(() -> new Invoice("Bob", 0).printOwing());
    check(out2.equals("*** Customer Owes ***\nname: Bob\namount: 0.0\n"), "printOwing output (zero) was: " + out2.replace("\n", "|"));
  }
}
