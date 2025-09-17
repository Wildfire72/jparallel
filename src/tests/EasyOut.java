public class EasyOut {
	public static void main (String []args ) {
				class Runnable0 implements Runnable {

			public void run() {
				System.out.println(Thread.currentThread().getName() + ", executing run() method!");
				System .out .println ("Task 1 running");
			}
		}
		class Runnable1 implements Runnable {

			public void run() {
				System.out.println(Thread.currentThread().getName() + ", executing run() method!");
				System .out .println ("Task 2 running");
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

