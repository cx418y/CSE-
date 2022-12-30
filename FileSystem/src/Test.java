import Block.BlockManager;
import file.File;
import file.FileManager;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Test {

    public static BlockManager[] blockManagers = new BlockManager[3];
    public static FileManager[] fileManagers = new FileManager[3];

    public static void main(String[] args){
        /*
         * initialize your file system here
         * for example, initialize FileManagers and BlockManagers
         * and offer all the required interfaces
         * */

        for(int i = 0; i < 3; i++){
            blockManagers[i] = new BlockManager(i+1);

        }
        for(int i = 0; i < 3; i++){
            fileManagers[i] = new FileManager(i+1,blockManagers);
        }


        File file = fileManagers[0].newFile(1);
        file.write("abcd".getBytes(StandardCharsets.UTF_8));
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        file.move(1,file.MOVE_HEAD);
        file.write("ef".getBytes(StandardCharsets.UTF_8));
        file.close();
        Tools.smartLs();
        System.out.println(new String(file.read(file.getSize())));


        //Tools.smartHex(blockManagers[0],1);
        file.setSize(4);
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        file.move(0,file.MOVE_HEAD);
        file.setSize(8);
        file.close();
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        Tools.smartLs();


        //File file1 = fileManagers[0].newFile(1);
        /*
        // test code
        File file = fileManagers[0].newFile(1); // id为1的⼀个file
        file.write("FileSystem".getBytes(StandardCharsets.UTF_8));
        file.move(0,file.MOVE_HEAD);
        System.out.println(Array.toString(file.read(file.getSize())));
        file.move(0,File.MOVE_HEAD);
        file.write("Smart".getBytes(StandardCharsets.UTF_8));
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        file.setSize(100);
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        file.setSize(16);
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        file.close();
        Tools.smartLs();


        //here we will destroy a block, and you should handler this exception
        File file1 = fileManagers[0].getFile(1);
        System.out.println(new String(file.read(file.getSize())));
        Tools.smartLs();
        File file2 = Tools.smartCopy(1,1);
        System.out.println(new String(file2.read(file2.getSize())));
        file.move(0,file.MOVE_HEAD);
        System.out.println(new String(file.read(file.getSize())));
        Tools.smartHex(blockManagers[0],1); // print the first block of a specific bm
        Tools.smartWrite(file2.getSize(),File.MOVE_HEAD,file2.getFileId(),file2.getFileManager());
        file2.move(0,file2.MOVE_HEAD);
        System.out.println( "file2:"+new String(file2.read(file2.getSize())));
        file2.close();
        System.out.println(new String(file2.read(file2.getSize())));
        Tools.smartLs();
        Tools.smartHex(blockManagers[0],1);
        */



    }
}
