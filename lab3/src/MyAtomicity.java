import com.sun.org.apache.regexp.internal.RE;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MyAtomicity {
    // TODO: declare your variables here
    private String databasePath;
    private String logPath;
    private int highWaterMarket; //
    private boolean isTest1;

    private HashMap<Integer,Long> transactionsThread;  // 记录事务id和线程
    private HashMap<Integer,String> transactionsOutcome;  // 用于记录事务状态
    private HashMap<Integer,Character> transactionValue;
    private List<Integer> versionId;
    private MyLock lock;

    public MyAtomicity(String databasePath, String logPath,Boolean isTest1){
        this.databasePath = databasePath;
        this.logPath = logPath;
        transactionsThread = new HashMap<>();
        transactionsOutcome = new HashMap<>();
        transactionValue = new HashMap<>();
        highWaterMarket = 0;
        versionId = new ArrayList<>();
        lock = new MyLock();
        this.isTest1 = isTest1;
    }

    /**
     * Update the text file "database"
     * @param ch The input value for all lines
     */
    public void update(char ch) {
        // TODO
        int tId = getTransactionId();
        if(highWaterMarket > tId){
            //isAborted = true;
            System.out.println("Transaction "+tId+" abort!");
            abort();
            return;
        }
        transactionsOutcome.replace(tId,"Pending");
        versionId.add(tId);

        recover();
        //boolean isAborted = false;
        for(int i = 0; i < 10; i++){
            //System.out.println(tId+" verion "+highWaterMarket);
            write(i,ch);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("Interrupted");
                transactionsOutcome.replace(tId,"Aborted");
                abort();
                return;
            }
        }
        if(highWaterMarket > tId){
            //isAborted = true;
            System.out.println("Transaction "+tId+" abort!");
            abort();
            return;
        }
        transactionsOutcome.replace(tId,"Pending");
        System.out.println("Transaction "+tId+" update is finished! value is: "+ch);
        transactionValue.replace(tId,ch);
        //log("Change," + tId); //+","+index+"," + ch+"," + preData);
        commit();
    }

    /**
     * Write a single character to "database"
     * @param index The location of database to be updated
     * @param ch The input value
     */
    public void write(int index, char ch) {
        // TODO
        //transactionsOutcome.replace(getTransactionId(),"Pending");
        int tId = getTransactionId();
        char preData = (char)(ch-1); // 上一个事务写的字符
        if(tId != -1) {
            log("Change," + tId +","+index+"," + ch+"," + preData);
        }
    }


    // log内容： TYPE:(begin,change,committed,aborted), TRANSACTION_ID, INDEX, REDO(updateDate), UNDO(preData),

    /*** Log some information
     * @param text Log text
     */

    private void log(String text) {
        // TODO
        int tId = getTransactionId();
        //System.out.println("tid:"+tId);
        if(tId != -1){
            try {
                File file;
                if(isTest1)
                {
                    file = new File(logPath + 0 + ".txt");
                }else{
                    file = new File(logPath + tId + ".txt");

                }
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                // 移动指针到最后一行
                raf.seek(raf.length());
                raf.write(text.getBytes("utf-8"));
                raf.write("\n".getBytes("utf-8"));
                raf.close();
            }catch (IOException e){
                System.out.println(e.getMessage());
            }
        }

    }

    /**
     * Recover the system from your log.
     * This should be called at the start of each {@code update()} call.
     */
     public void recover() {
         int tId = getTransactionId();
         boolean last = tId == -1;

         // TODO
         try {
             String fileName = "";
             RandomAccessFile r = null;
             // 单线程
             if(isTest1){
                 fileName = "D:/大三上/计算机系统工程/lab/lab3/src/File/log0.txt";
             }else{
                 if(tId == 1){
                     return;
                 }else{
                     int preId = tId==-1?transactionsThread.size():tId-1;
                     fileName =  "D:/大三上/计算机系统工程/lab/lab3/src/File/log"+preId+".txt";

                 }
             }
             //System.out.println(Thread.currentThread().getId()+","+getTransactionId());
             r = new RandomAccessFile(fileName, "r");
             if(r.length()!=0) {
                 long start = r.getFilePointer();
                 long nextend = start + r.length() - 2;
                 //System.out.println("nextend:" + nextend);
                 String result = "";
                 r.seek(nextend);
                 int c = -1;
                 boolean findCommitted = false;
                 char updateDate = '0';
                 while (nextend >= start) {
                     c = r.read();
                     if (c == '\n' || c == '\r') {
                         result = r.readLine();
                         //System.out.println(result);
                         if (result.startsWith("Committed")) {
                             findCommitted = true;
                         } else {
                             if (findCommitted) {
                                 String[] logContent = result.split(",");
                                 updateDate = logContent[logContent.length - 2].charAt(0);
                                 break;
                             }
                         }
                         //System.out.println(result);//打印在控制台
                         nextend--;
                     }
                     nextend--;
                     if (nextend >= 0) {
                         r.seek(nextend);
                         if (nextend == 0) {// 当文件指针退至文件开始处，输出第一行
                             result = r.readLine();
                             //System.out.println(result);
                         }
                     }

                 }
                 if (updateDate != '0') {
                     FileWriter fw = new FileWriter(databasePath);
                     for (int i = 0; i < 10; i++) {
                         fw.write(updateDate);
                         fw.write("\n");
                     }
                     fw.close();
                     if (last){
                         System.out.println("final result is "+ updateDate);
                     }
                 }else {
                     System.out.println("No update");
                 }
             }
             r.close();

         } catch (FileNotFoundException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }

     }

     // 读取数据库内容
     public char read(){
         //System.out.println("read");
         // 单线程则只用返回当前的值
         int tId = getTransactionId();
         if(transactionsThread.size() == 1){
             return transactionValue.get(tId);
         }

         // 多线程
         for(int i = versionId.size()-1; i >= 0; i--){
             int vId = versionId.get(i);
             // 修改数据的事务id大于当前事务的id则继续往前寻找
             if(vId > tId){
                 continue;
             }
             try {
                 int time = (int)(1+Math.random()*5);
                 Thread.sleep(time*1000);
                 //Thread.sleep(1000);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
             //等待直到之前的事务结束
             while (transactionsOutcome.get(vId).equals("Pending")){
                 try {
                     int time = (int)(1+Math.random()*5);
                     Thread.sleep(time*1000);
                 } catch (InterruptedException e) {
                     System.out.println(e.getMessage());
                 }
             }
             if(transactionsOutcome.get(vId).equals("Committed") ){
               // 修改highWaterMarket
                 highWaterMarket = Math.max(tId,highWaterMarket);
                 return transactionValue.get(vId);
             }

         }
         highWaterMarket = Math.max(tId,highWaterMarket);
         return transactionValue.get(1);
     }

     public void begin() {
         try {
             lock.lock();
             addTransaction(transactionsThread.size() + 1, Thread.currentThread().getId());
             System.out.println("Transaction "+getTransactionId()+", is thread "+Thread.currentThread().getId());
             //read();
             lock.unLock();
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     }

     // 添加事务
     public void addTransaction(int transactionId,long threadId){
         transactionsThread.put(transactionId,threadId);
         transactionsOutcome.put(transactionId,"Begin");
         transactionValue.put(transactionId,'0');
         log("Begin,"+getTransactionId());
         //System.out.println("Begin,"+getTransactionId());
     }

     public int getTransactionId(){
         for (HashMap.Entry<Integer, Long> entry : transactionsThread.entrySet()) {
             // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
             if(entry.getValue() == Thread.currentThread().getId()){
                 return entry.getKey();
             }
         }
         return -1;
     }

     public void abort(){
         int tId = getTransactionId();
         log("Aborted,"+ tId);
         transactionsOutcome.replace(tId,"Aborted");
     }

     public void commit(){
         int tId = getTransactionId();
         log("Committed,"+ tId);
         transactionsOutcome.replace(tId,"Committed");
     }


}
