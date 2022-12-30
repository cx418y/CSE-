public class MyLock extends Thread{
    private static int sem;
    private int lockID = -1;
    public MyLock(){
        sem = 1;
    }

    public  void lock(int id) throws InterruptedException {
            //Thread.sleep(1000);
            long min = Long.MAX_VALUE;
            while (lockID != id){
                while (true)
                {
                    long time = System.nanoTime();
                    while (lockID >= 0){
                        // long start = System.nanoTime();
                        Thread.yield();
                        time = System.nanoTime();
                        //long end = System.nanoTime();
                        //System.out.printf("id = %d, elapsed = %d \n", id, end - start);
                        //Thread.sleep(20);
                    }
                    lockID = id;
                    if (System.nanoTime() - time < 10000)
                        break;
                    else
                        lockID = -1;
                }
                long start = System.nanoTime();
                long end = start;
                for (int i = 1; end - start < 200000; i++)
                {
                    Thread.yield();
                    end = System.nanoTime();
                }
                //System.out.printf("id = %d, elapsed = %d \n", id, end - start);
                //Thread.sleep(20);
            }
            //lockID = -1;
            //Thread.join();

    }
    public void unLock(){
        lockID = -1;
    }
}
