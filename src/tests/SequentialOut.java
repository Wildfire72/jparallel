class Medium {
    public static void main (String []args ) {
        System .out .println ("Before first parallel block");
        class Runnable0 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Parallel A1");
            }
        }
        class Runnable1 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Parallel A2");
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
        System .out .println ("Between parallel blocks");
        class Runnable2 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Parallel B1");
            }
        }
        class Runnable3 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Parallel B2");
            }
        }
        class Runnable4 implements Runnable {

            public void run() {
                System.out.println(Thread.currentThread().getName() + ", executing run() method!");
                System .out .println ("Parallel B3");
            }
        }
        Thread t2 = new Thread(new Runnable2());
        Thread t3 = new Thread(new Runnable3());
        Thread t4 = new Thread(new Runnable4());
        t2.start();
        t3.start();
        t4.start();
        try {
            t0.join();
            t1.join();
            t2.join();
        } catch (InterruptedException e) {

        }
        System .out .println ("After second parallel block");
    }

}

