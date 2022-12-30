import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) {
        MyLock lock = new MyLock();
        //TODO: initialize the lock

        testA1(lock);

    }
    static int cnt = 0;

    public static void testA1(MyLock lock) {
        cnt = 0;
        System.out.println("Test A start");
        int threadNumber = 5;
        final CountDownLatch cdl = new CountDownLatch(threadNumber);//参数为 线程个数

        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            int id = i;
            threads[i] = new Thread(() -> {
                try {
                    lock.lock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int tmp = cnt;

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                cnt = tmp + 1;
                //System.out.println(id+","+cnt);
                lock.unLock();
                cdl.countDown();//此方法是CountDownLatch的线程数-1
            });
        }
        for (int i = 0; i < threadNumber; i++) {
            threads[i].start();

        }
//线程启动后调用countDownLatch方法
        try {
            cdl.await();
            //需要捕获异常，当其中线程数为0时这里才会继续运行
            System.out.println(cnt);
            String res = cnt == threadNumber ? "Test A passed" : "Test A failed,cnt should be "+threadNumber;
            System.out.println(res);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
