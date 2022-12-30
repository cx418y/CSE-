import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class Test {
    static String database1Path = "D:/大三上/计算机系统工程/lab/lab3/src/File/database1.txt";
    static String database2Path = "D:/大三上/计算机系统工程/lab/lab3/src/File/database2.txt";
    static String logPath = "D:/大三上/计算机系统工程/lab/lab3/src/File/log";
    public static void main(String[]args) throws IOException {
        File database1 = new File(database1Path);
        File database2 = new File(database2Path);
        initial(database1,database2);
        MyAtomicity myAtomicity1 = new MyAtomicity(database1Path,logPath,true);
        MyAtomicity myAtomicity2 = new MyAtomicity(database2Path,logPath,false);

        testWriteAhead(database1Path,logPath,0,myAtomicity1);
        testReadCapture(database2Path,logPath,0,myAtomicity2,6);
    }

    //初始化原始文件和log文件
    public static void initial(File database1, File database2) throws IOException {
        if(!database1.exists()){
            database1.createNewFile();

        }
        FileWriter fw1 = new FileWriter(database1);
        for(int i = 0; i < 10; i++){
            fw1.write('0');
            fw1.write("\n");
        }
        fw1.close();

        if(!database2.exists()){
            database2.createNewFile();
        }
        FileWriter fw2 = new FileWriter(database2);
        for(int i = 0; i < 10; i++){
            fw2.write('0');
            fw2.write("\n");
        }
        fw2.close();


    }

    public static void testWriteAhead(String databasePath, String logPath, int id,MyAtomicity myAtomicity){
        Thread thread = new Thread(() -> {
            test1(database1Path,logPath,0,myAtomicity);
        });
        thread.start();
        try {
            thread.join(15000); // 在主线程中等待t1执行2秒
        } catch (InterruptedException e) {
            System.out.println("t1 interrupted when waiting join");
            e.printStackTrace();
        }
        thread.interrupt();
        myAtomicity.recover();
    }

    public static void test1(String databasePath, String logPath, int id,MyAtomicity myAtomicity){
        try{
            File myLogFile = new File(logPath+id+".txt");
            myLogFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }

        myAtomicity.begin();
        char preData = myAtomicity.read();
        char newData = (char) (preData +1);
        myAtomicity.update(newData);
        myAtomicity.update((char) (newData+1));
        myAtomicity.recover();
    }

    public static void testReadCapture(String databasePath, String logPath, int startId,MyAtomicity myAtomicity,int threadNumber){
        final CountDownLatch cdl = new CountDownLatch(threadNumber);//参数为 线程个数
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threadNumber; i++) {
            threads[i] = new Thread(() -> {
                myAtomicity.begin();
                try {
                    int time = (int)(1+Math.random()*15);
                    Thread.sleep(time*1000);
                   // Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                char preData = myAtomicity.read();
                System.out.println("Transaction "+myAtomicity.getTransactionId()+" read value is: "+preData);
                //myAtomicity.getMap();
                char newData = (char) (preData +1);
                myAtomicity.update(newData);
                // System.out.println("This is transaction "+myAtomicity.getTransactionId()+", status is:"+myAtomicity.getStatus());
                cdl.countDown();
                });
        }
        for (int i = 0; i < threadNumber; i++) {
            threads[i].start();
            //System.out.println((i+1+startId)+","+threads[i].getId());
            //myAtomicity.addTransaction(i+1+startId,threads[i].getId());
        }
        try {
            cdl.await();
            //需要捕获异常，当其中线程数为0时这里才会继续运行
            System.out.println("finish");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myAtomicity.recover();
        //myAtomicity.getMap();
    }




}
