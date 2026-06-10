public class SynchronizedMethodDemo {
    public static int count = 0;

    static class MyThread extends Thread {
        public static   synchronized void add() {
            for (int i = 0; i < 10000; i++) {
                count++;
            }
        }
        public void run(){
            add();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyThread[] threads = new MyThread[10];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new MyThread();
            threads[i].start();
        }
        for (MyThread thread : threads) {
            thread.join();
        }
        System.out.println(count);
    }
}
