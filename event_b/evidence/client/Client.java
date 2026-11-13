import org.apache.commons.lang3.builder.ReflectionDiffBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
public class Client {
    static class P { int x; P(int x) { this.x = x; } }
    public static void main(String[] a) {
        System.out.println(new ReflectionDiffBuilder<>(new P(1), new P(2), ToStringStyle.SHORT_PREFIX_STYLE).build().getNumberOfDiffs() + " diff(s)");
    }
}
