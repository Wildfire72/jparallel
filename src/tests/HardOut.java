public class HardOut {
    public static void main (String []args ) {
        int x  = 10;
        class Runnable0 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Outer parallel block, part 1, x = "+ x );
            }
        }
        class Runnable1 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                class Runnable2 implements Runnable {

                    public void run() {
                        System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                        System .out .println ("Inner parallel block, part 1, x = "+ x );
                    }
                }
                class Runnable3 implements Runnable {

                    public void run() {
                        System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                        System .out .println ("Inner parallel block, part 2, x = "+ (x * 2));
                    }
                }
                Thread t0 = new Thread(new Runnable0());
                Thread t1 = new Thread(new Runnable1());
                t0.start();
                t1.start();
                try {
                    t0.join();
                    t1.join();
                } catch (InterruptedException e) {

                }
            }
        }
        class Runnable3 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Outer parallel block, part 2, x = "+ (x + 5));
            }
        }
        Thread t0 = new Thread(new Runnable0());
        Thread t1 = new Thread(new Runnable1());
        Thread t2 = new Thread(new Runnable2());
        Thread t3 = new Thread(new Runnable3());
        t0.start();
        t1.start();
        t2.start();
        t3.start();
        try {
            t0.join();
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {

        }
        System .out .println ("Done with nested parallel blocks");
    }

}

