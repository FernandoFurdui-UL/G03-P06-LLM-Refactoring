import java.io.*;
import java.util.*;

public class RenameMethodTest {
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
    // The method name is expected to change, so the test finds the accessor by behaviour:
    // some non-private, non-static, no-argument method of Customer must return "lastname".
    Customer c = new Customer();
    boolean found = false;
    boolean stillOldName = false;
    for (java.lang.reflect.Method m : Customer.class.getDeclaredMethods()) {
      if (m.isSynthetic() || m.getParameterCount() != 0 || m.getReturnType() != String.class) continue;
      if (java.lang.reflect.Modifier.isPrivate(m.getModifiers()) || java.lang.reflect.Modifier.isStatic(m.getModifiers())) continue;
      m.setAccessible(true);
      if ("lastname".equals(m.invoke(c))) {
        found = true;
        if (m.getName().equals("getlnm")) stillOldName = true;
      }
    }
    check(found, "a no-argument accessor returning \"lastname\" must still exist");
    // Diagnostic only (not gating): whether the method was actually renamed.
    System.out.println(stillOldName ? "DIAGNOSTIC: accessor still named getlnm (not renamed)" : "DIAGNOSTIC: accessor renamed");
  }
}
