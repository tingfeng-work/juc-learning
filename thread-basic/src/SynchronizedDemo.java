public class SynchronizedDemo {

    private static int count = 0;

    static class MyThread extends Thread {
        public void run() {

            for (int i = 0; i < 10000; i++) {

                synchronized (MyThread.class){
                    System.out.println(Thread.currentThread().getName()+"进入");
                    count++;
                    System.out.println(Thread.currentThread().getName()+"退出");
                }

            }

        }
    }

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread();
            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println(count);

    }

}
