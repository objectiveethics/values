package objectiveethics.test;

public class AssemblyTest {
    public static void main(String[] args) {
        double d1 = Math.exp(50) / 1000000;
        double d2 = Math.exp(50 - Math.log(1000000));

        System.out.println(d1);
        System.out.println(d2);

    }
}
