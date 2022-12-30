package Block;



import Exception.ErrorCode;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Block implements Serializable {
    private final int id;
    public final static int BLOCK_SIZE = 2;
    private int contentSize;
    private String metaPath;
    private String dataPath;
    private final BlockManager blockManager;
    private List<Block> myDuplication;
    private String checksum;
    private String name;

    public Block(int id, BlockManager blockManager) {
        this.id = id;
        this.blockManager = blockManager;
        this.metaPath = "D:/大三上/计算机系统工程/lab/FileSystem/src/Memory/BM/" + blockManager.getId() + "/" + id +".meta";
        this.dataPath = "D:/大三上/计算机系统工程/lab/FileSystem/src/Memory/BM/" + blockManager.getId() + "/" + id +".data";
        try {
            java.io.File file = new java.io.File(metaPath);
            java.io.File file1 = new java.io.File(dataPath);
            java.io.File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
            file1.createNewFile();
        }catch (IOException e){
            ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION);
        }
        name = blockManager.getName() + "-b" + id;
        myDuplication = new ArrayList<>();
    }


    public int getIndex() {
        return this.id;
    }


    public BlockManager getBlockManager() {
        return this.blockManager;
    }

    public byte[] read() {
        HashMap<String, String> map;
        byte[] data;
        String checksum;
        try{
            //读取meta
            ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(metaPath))));
            map = (HashMap<String, String>) ois.readObject();
            ois.close();

            //读取data
            File file = new File(dataPath);
            if(!file.exists()){
                throw new ErrorCode(ErrorCode.BLOCK_BROKEN);
            }
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(dataPath)));

            data = new byte[bis.available()];
            bis.read(data);
            bis.close();

            checksum = map.get("checksum");

            if (!checksum.equals(MD5Util.getMD5String(data))) {
                throw new ErrorCode(ErrorCode.CHECKSUM_CHECK_FAILED);
            }else{
                return data;
            }

        }catch (ErrorCode errorCode){
            System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
        }catch (FileNotFoundException e){
            System.out.println(ErrorCode.getErrorText(ErrorCode.FILE_NOT_FOUND));
        }
        catch(IOException e){
            System.out.println(ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION));
        }catch(ClassNotFoundException e){
            System.out.println(ErrorCode.getErrorText(ErrorCode.CLASS_NOT_FOUND));
        }
        return null;
    }

    public void write(byte[] b) throws Exception {
        if (b.length > BLOCK_SIZE) {
            throw new ErrorCode(ErrorCode.WRITE_OUT_OF_BOUND);
        }

        checksum = MD5Util.getMD5String(b);
        //写入data
        File file = new File(dataPath);
        if (! file.exists()) {
            throw new  IOException();
        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dataPath)));
        bos.write(b);
        bos.close();
        this.contentSize = b.length;

    }


    public int getSize() {
        return this.contentSize;
    }

    public String getDataPath() {
        return dataPath;
    }

    public String getMetaPath() {
        return metaPath;
    }

    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    public void setMetaPath(String metaPath) {
        this.metaPath = metaPath;
    }

    public int getContentSize() {
        return contentSize;
    }

    public List<Block> getMyDuplication() {
        return myDuplication;
    }

    public void setMyDuplication(LogicBlock logicBlock) {
        List<Block> duplicationBlocks = new ArrayList<>();
        for(int i = 0; i < logicBlock.getBlocks().length; i++){
            if(logicBlock.getBlocks()[i] != this){
                duplicationBlocks.add(logicBlock.getBlocks()[i]);
            }
        }
        myDuplication = duplicationBlocks;
        writeMeta();
    }

    public void writeMeta(){
        HashMap<String, Object> map = new HashMap<>();
        map.put("checksum", checksum);
        map.put("id", id);
        map.put("physical path",dataPath);
        map.put("duplication block",myDuplication);
        try{
            //写入meta
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(new File(metaPath))));
            oos.writeObject(map);
            oos.close();

        }catch (FileNotFoundException e){
            System.out.println(ErrorCode.getErrorText(ErrorCode.FILE_NOT_FOUND));
        }
        catch (IOException e) {
            System.out.println("b-meta:"+ ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION));
        }
    }

    public String getName() {
        return name;
    }

    public String getDuplicationName(){
        StringBuilder sb = new StringBuilder("");
        if(myDuplication.size() != 0){
            sb.append(myDuplication.get(0).getName());
            sb.append(",");
            sb.append(myDuplication.get(1).getName());
        }

        return sb.toString();
    }
}
