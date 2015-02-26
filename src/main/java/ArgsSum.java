/**
 * just a sample to be invoked by ReusableJVM.
 */
public class ArgsSum {
    private static int sum = 0;

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            sum += Integer.parseInt(args[i]);
        }
        System.out.println(sum);
    }
}
