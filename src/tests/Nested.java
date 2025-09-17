public class Nested {
    public static void main(String[] args) {
        int x = 10;

        parallel {
            System.out.println("Outer parallel block, part 1, x = " + x);

            parallel {
                System.out.println("Inner parallel block, part 1, x = " + x);
                System.out.println("Inner parallel block, part 2, x = " + (x * 2));
            }

            System.out.println("Outer parallel block, part 2, x = " + (x + 5));
        }

        System.out.println("Done with nested parallel blocks");
    }
}
