class Test {
    private static volatile boolean flag = true;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            while(flag){

            }
            System.out.println("线程结束");
        }).start();

        Thread.sleep(1000);

        flag = false;
        System.out.println(flag);
    }
}