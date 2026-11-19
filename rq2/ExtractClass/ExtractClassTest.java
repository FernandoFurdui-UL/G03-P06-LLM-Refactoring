import java.io.*;
import java.util.*;

public class ExtractClassTest {
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
    Soldier a = new Soldier(100, 10, 1);
    Soldier b = new Soldier(50, 0, 1);
    a.attack(b);
    check(b.health == 40, "one attack should leave target at 40, got " + b.health);
    a.attack(b);
    check(b.health == 30, "two attacks should leave target at 30, got " + b.health);
    check(a.health == 100, "attacker health should be unchanged");
    Soldier broken = new Soldier(100, 10, 0);
    Soldier c = new Soldier(50, 0, 1);
    broken.attack(c);
    check(c.health == 50, "a soldier whose weapon status is 0 should do no damage, got " + c.health);
  }
}
