import java.util.HashSet;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<Integer> set = new HashSet<>();
        int n = Integer.parseInt(args[0]);
        set.add(n);
        while (n != 1) {
            n = getNext(n);
            if (set.contains(n)) {
                System.out.println(false);
                return;
            }
            set.add(n);
        }
        System.out.println(true);
    }

    public static int getNext(int n) {
        int next = 0;
        while (n > 0) {
            int tmp = n % 10;
            next += tmp * tmp;
            n = n / 10;
        }
        return next;
    }
}