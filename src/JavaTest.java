public class JavaTest {
    private static int fact(int a) {
        if (a == 0)
            return 1;
        else
            return a*fact(a-1);
    }

    public static void main(String[] args) {
        for (int n = 10; n <= 100; n++) {
            for (int k = 10; k <= 100; k++) {
                try {
                    System.out.println("Checking " + n + " " + k);
                    int c = fact(n) / (fact(k) * fact(n-k));
                    System.out.println("\t = " + c);

                } catch(ArithmeticException e) {
                    System.out.println("Failed at" + n + " " + k);

                } catch(StackOverflowError e) {
                    System.out.println("SO for + " + n + " " + k);
                }
            }
        }

    }
}
