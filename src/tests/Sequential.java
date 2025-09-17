public class Medium {
    public static void main(String[] args) {
        System.out.println("Before first parallel block");
        int x = 0;
        parallel {
            System.out.println("Parallel A1");
            System.out.println("Parallel A2");
        }

        System.out.println("Between parallel blocks");

        parallel {
            System.out.println("Parallel B1");
            System.out.println("Parallel B2");
            System.out.println("Parallel B3");
        }

        System.out.println("After second parallel block");
    }
}
