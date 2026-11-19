import java.io.*;
import java.util.*;

public class ExtractMethod2Test {
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
    List<User> users = Arrays.asList(new User("Ann", 30), new User("Bob", 41));
    String out = capture(() -> new UserPrinter().printProperties(users));
    check(out.equals("Ann 30\nBob 41\n"), "printProperties output was: " + out.replace("\n", "|"));
    String none = capture(() -> new UserPrinter().printProperties(new ArrayList<User>()));
    check(none.isEmpty(), "empty list should print nothing, got: " + none.replace("\n", "|"));
  }
}
