package file;

import Block.*;
import Block.BlockManager;
import Exception.ErrorCode;
import org.omg.CORBA.ARG_IN;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class File implements Serializable{
    private String filename;
    private final int id;
    private final FileManager fileManager;
    private List<LogicBlock> logicBlocks;
    private BlockManager[] blockManagers;
    private int size;
    private int cursor;
    private Buffer buffer;
    private String metaPath;
    private String name;
    private boolean isModified;

    public static final int MOVE_CURR = 0; //只是光标的三个枚举值，具体数值⽆实际意义
    public static final int MOVE_HEAD = 1;
    public static final int MOVE_TAIL = 2;


    public File(int id, FileManager fileManager, BlockManager[] blockManagers) {
        this.id = id;
        this.fileManager = fileManager;
        this.blockManagers = blockManagers;
        this.size = 0;
        this.cursor = 0;
        buffer = new Buffer();
        isModified = false;
        metaPath = "D:/大三上/计算机系统工程/lab/FileSystem/src/Memory/FM/" + fileManager.getId() + "/" + id + ".meta";
        try {
            java.io.File file = new java.io.File(metaPath);
            java.io.File fileParent = file.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            file.createNewFile();
        } catch (IOException e) {
            ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION);
        }
        name = "f" + id;
    }

    String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public int getFileId() {
        return this.id;
    }


    public FileManager getFileManager() {
        return this.fileManager;
    }


    public byte[] read(int length) {
        if (length >= 0) {
            if (length == 0) {
                return new byte[0];
            } else {
                try {
                    if (this.cursor + length > size) {
                        throw new ErrorCode(ErrorCode.READ_END_OF_FILE);
                    } else {
                        //文件有修改直接在buffer中读取：
                        if (isModified) {
                            byte[] result = new byte[length];
                            for (int i = 0; i < length; i++) {
                                result[i] = buffer.getBuffer()[i + cursor];
                            }
                            cursor += length;
                            return result;
                        }
                        //文件未修改在磁盘中读取：
                        byte[] fileData = new byte[size];
                        for (int i = 0, j = 0; i < logicBlocks.size(); i++) {
                            byte[] temp = logicBlocks.get(i).read();
                            if(temp == null){
                                return new byte[0];
                            }
                            for (int k = 0; k < temp.length; k++) {
                                fileData[j] = temp[k];
                                j++;
                            }
                        }
                        byte[] result = new byte[length];
                        for(int i = 0; i < length; i++){
                            result[i] = fileData[i+cursor];
                        }
                        cursor += length;
                        return result;
                    }
                } catch (ErrorCode errorCode) {
                    System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
                }
            }
        } else {
            try {
                throw new ErrorCode(ErrorCode.INVALID_LENGTH);
            } catch (ErrorCode errorCode) {
                System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
            }
        }
        return new byte[0];
    }


    public void write(byte[] b) {

        if (b.length == 0) {
            return;
        }
        isModified = true;
        if (buffer.getBuffer().length != 0) {
            buffer.write(b, cursor);
            size += b.length;
            cursor += b.length;
        } else {
            if (size != 0) {
                byte[] fileData = new byte[size];
                for (int i = 0, j = 0; i < logicBlocks.size(); i++) {
                    byte[] temp = logicBlocks.get(i).read();
                    for (int k = 0; k < temp.length; k++) {
                        fileData[j] = temp[k];
                        j++;
                    }
                }
                buffer = new Buffer(fileData);
            }
            buffer.write(b, cursor);
            cursor += b.length;
            size += b.length;

        }
    }

    public byte[] smartCat() {
        if (size == 0) {
            return new byte[0];
        }
        if (buffer.getBuffer().length != 0) {
            return buffer.getBuffer();
        }
        byte[] result = new byte[size];
        for (int i = 0, j = 0; i < logicBlocks.size(); i++) {
            for (int k = 0; k < logicBlocks.get(i).getContentSize(); k++) {
                byte[] temp = logicBlocks.get(i).read();
                for (int m = 0; m < temp.length; m++) {
                    result[j] = temp[m];
                    j++;
                }
            }

        }
        return result;
    }

    public int move(int offset, int where) {
        int newCursor;
        switch (where) {
            case MOVE_CURR:
                newCursor = cursor + offset;
                break;
            case MOVE_HEAD:
                newCursor = offset;
                break;
            case MOVE_TAIL:
                newCursor = size + offset;
                break;
            default:
                newCursor = -1;
        }
        try {
            if (newCursor < 0) {
                throw new ErrorCode(ErrorCode.INVALID_ARGUMENT_WHERE);
            }
            if (newCursor > size) {
                throw new ErrorCode(ErrorCode.MOVE_OUT_OF_FILE);
            }
            cursor = newCursor;
            return cursor;
        } catch (ErrorCode errorCode) {
            System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
        }
        return cursor;
    }


    public int getSize() {
        return this.size;
    }


    public void setSize(int newSize) {
        if (size == newSize) {
            cursor = size;
            return;
        }
        if (newSize == 0) {
            buffer = new Buffer();
            size = 0;
            cursor = size;
            isModified = true;
            return;
        }
        if (size == 0) {
            buffer = new Buffer();
            byte[] newData = new byte[newSize - size];
            buffer.write(newData, size);
            size = newSize;
            cursor = newSize;
            isModified = true;
            return;
        }
        if (newSize > size) {
            byte[] newData = new byte[newSize - size];
            cursor = size;
            write(newData);
            isModified = true;
        } else {
            if (isModified) {
                byte[] temp = new byte[newSize];
                for (int i = 0; i < newSize; i++) {
                    temp[i] = buffer.getBuffer()[i];
                }
                buffer = new Buffer(temp);
                size = newSize;
                cursor = newSize;
            } else {
                byte[] fileData = new byte[size];
                for (int i = 0, j = 0; i < logicBlocks.size(); i++) {
                    byte[] temp = logicBlocks.get(i).read();
                    for (int k = 0; k < temp.length; k++) {
                        fileData[j] = temp[k];
                        j++;
                    }
                }
                byte[] temp = new byte[newSize];
                for (int i = 0; i < newSize; i++) {
                    temp[i] = fileData[i];
                }
                buffer = new Buffer(temp);
                size = newSize;
                cursor = newSize;
                isModified = true;
            }
        }
    }


    public void close() {
        if (!isModified){
            cursor = 0;
            return;
        } else {
            List<LogicBlock> newLogicBlocks = new ArrayList<LogicBlock>();
            //cursor = 0;
            byte[] temp = buffer.getBuffer();
            int blockNum = temp.length / Block.BLOCK_SIZE;
            HashMap<String, Object> map = new HashMap<>();
            try {
                for (int i = 0; i < blockNum; i++) {
                    byte[] b = new byte[Block.BLOCK_SIZE];
                    for (int j = 0; j < Block.BLOCK_SIZE; j++) {
                        b[j] = temp[i * Block.BLOCK_SIZE + j];
                    }
                    LogicBlock logicBlock = new LogicBlock(blockManagers);
                    logicBlock.write(b);
                    newLogicBlocks.add(logicBlock);
                }
                if(temp.length % Block.BLOCK_SIZE != 0) {
                    int m = temp.length % Block.BLOCK_SIZE;
                    byte[] b = new byte[m];
                    for (int i = 0; i < m; i++) {
                        b[i] = temp[blockNum * Block.BLOCK_SIZE + i];
                    }
                    LogicBlock logicBlock = new LogicBlock(blockManagers);
                    logicBlock.write(b);
                    newLogicBlocks.add(logicBlock);
                }

                logicBlocks = newLogicBlocks;

            } catch (FileNotFoundException e) {
                System.out.println(ErrorCode.getErrorText(ErrorCode.FILE_NOT_FOUND));
            } catch (IOException e) {
                System.out.println(ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION));
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //更新文件size和meta
                map.put("id", id);
                map.put("block", logicBlocks);
                int newSize = 0;
                for (int i = 0; i < logicBlocks.size(); i++) {
                    newSize += logicBlocks.get(i).getContentSize();
                }
                size = newSize;
                map.put("size", size);
                cursor = 0;
            }

            //写入meta
            try {
                java.io.File file = new java.io.File(metaPath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
                oos.writeObject(map);
                oos.close();
            } catch (FileNotFoundException e) {
                System.out.println(ErrorCode.getErrorText(ErrorCode.FILE_NOT_FOUND));
            } catch (IOException e) {
                System.out.println(ErrorCode.getErrorText(ErrorCode.IO_EXCEPTION));
            }

        }
        isModified = false;
        cursor = 0;
    }



    public Buffer getBuffer() {
        return buffer;
    }

    public String getBlocksName(){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < logicBlocks.size(); i++){
            sb.append(logicBlocks.get(i).getBlocksName());
        }

        return sb.toString();
    }

    public int getId() {
        return id;
    }

    public List<LogicBlock> getLogicBlocks() {
        return logicBlocks;
    }

    public void setLogicBlocks(List<LogicBlock> logicBlocks) {
        this.logicBlocks = logicBlocks;
    }
}
