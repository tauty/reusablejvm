
public class Sample1_Summary {
    private static int sum = 0;

    public static void main(String[] args) {
        sum += Integer.parseInt(args[0]);
        System.out.println(sum);
    }
}
