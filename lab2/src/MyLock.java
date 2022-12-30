public class MyLock extends Thread{

    private long lockID = -1;

    public  void lock() throws InterruptedException {
        while (lockID != Thread.currentThread().getId()){
            while (true)
            {
                long time = System.nanoTime();
                while (lockID >= 0){
                    Thread.yield();
                    time = System.nanoTime();
                }
                lockID = Thread.currentThread().getId();
                if (System.nanoTime() - time < 100)
                    break;
                else
                    lockID = -1;
            }

            long start = System.nanoTime();
            long end = start;

            // 防止多个线程同时对锁进行了修改，改了id后需要再等待一段时间确保所有改了id的线程操作均已完成
            for (int i = 1; end - start < 300000; i++)
            {
                Thread.yield();
                end = System.nanoTime();
            }
        }
    }

    public void unLock(){
        lockID = -1;
    }
}
