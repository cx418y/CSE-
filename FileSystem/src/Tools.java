import Block.Block;
import Block.BlockManager;
import file.File;
import file.FileManager;

import java.util.Date;
import java.util.List;
import java.util.Scanner;
import Block.LogicBlock;

class Tools{
    // implements 4 smart-function
    public static byte[] smartCat(int fileId, int fmId){
        File file = Test.fileManagers[fmId-1].getFile(fileId);
        return file.smartCat();
    };
    // print a specific block, so any reasonable input is okay
    public static void smartHex(BlockManager bm, int index){
        Block block = bm.getBlock(index);
        if(block == null){
            System.out.println("no such block!");
            return;
        }

        byte[] data = block.read();
        StringBuilder sb = new StringBuilder("");
        for(byte b : data){
            String temp = Integer.toHexString(b & 0XFF);
            if(temp.length() == 1){
                sb.append("0");
            }
            sb.append(temp);
        }
        System.out.println(sb.toString());
    };

    public static void smartWrite(int offset, int where, int fileId, FileManager fm){
        File file = fm.getFile(fileId);
        if(file == null){
            System.out.println("no such file!");
            return;
        }
        file.move(offset,where);

        System.out.println("Please enter file content: ");
        Scanner scanner = new Scanner(System.in);
        byte[] bytes = scanner.nextLine().getBytes();
        file.write(bytes);
        file.close();
        byte[] a = file.read(file.getSize());

    };

    public static File smartCopy(int fileId, int fmId){
        File file = new File(Test.fileManagers[fmId-1].getFileNum(),Test.fileManagers[fmId],Test.blockManagers);
        if(file == null){
            System.out.println("no such file!");
            return null;
        }

        return Test.fileManagers[fmId-1].newFile(Test.fileManagers[fmId-1].getFile(fileId));
    }
    public static void smartLs(){
        BlockManager[] bms = Test.blockManagers;
        for(int i = 0; i < 3; i ++){
            System.out.println();
            System.out.println("BlockManager" + bms[i].getId() +": ");
            List<Block> blocks = bms[i].getBlocks();
            for(int j = 0; j < blocks.size(); j++){
                System.out.print( "[" + blocks.get(j).getName());
                System.out.print(" (" + blocks.get(j).getDuplicationName() +")" + "]");
            }
        }
        System.out.println();

        FileManager[] fileManagers = Test.fileManagers;
        for(int i = 0; i < 3; i++){
            System.out.println();
            System.out.println("FileManager "+ (i+1) +": ");
            List<File> files = fileManagers[i].getFiles();
            for(int j = 0; j <files.size(); j++){
                System.out.println("f"+files.get(j).getId());
                List<LogicBlock> logicBlocks = files.get(j).getLogicBlocks();
                for(int k = 0; k < logicBlocks.size(); k++){
                    System.out.println(k+1+": [" + logicBlocks.get(k).getBlocksName() +"]" );
                }
            }
        }
    };


}
