package file;

import Block.Block;
import Block.BlockManager;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import Exception.ErrorCode;
import sun.security.util.ByteArrayLexOrder;

public class FileManager implements Serializable {
    private final int id;
    private List<File> files;
    private int fileNum;
    private BlockManager[] blockManagers;

    public FileManager(int id, BlockManager[] blockManagers) {
        this.id = id;
        files = new ArrayList<>();
        fileNum = 0;
        this.blockManagers = blockManagers;
    }

    public File getFile(int fileId) {
        for(int i = 0; i < files.size(); i++ ) {
            if (files.get(i).getFileId() == fileId) {
                return files.get(i);
            }
        }
        try{
            throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
        }catch (ErrorCode errorCode){
            System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
        }
        return null;
    }

    public File getFile(String filename){
        for(int i = 0; i < files.size(); i++ ){
            if(files.get(i).getFilename() == filename){
                return files.get(i);
            }
        }
        throw new ErrorCode(ErrorCode.FILE_NOT_FOUND);
    }

    public File newFile(int fileId) {
        for(int i = 0; i < files.size(); i++){
            try {
                if (files.get(i).getFileId() == fileId) {
                    throw new ErrorCode(ErrorCode.FILE_HAVE_EXIST);
                }
            }catch(ErrorCode errorCode){
                System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
            }
        }
        File file = new File(fileId, this,blockManagers);
        files.add(file);
        fileNum ++;
        return file;
    }

    public File newFile(File file){
        File newFile = new File(files.size()+1,this,blockManagers);
        byte[] sourceData = new byte[file.getSize()];
        for(int i = 0,k = 0;i <file.getLogicBlocks().size(); i++){
            byte[] temp = file.getLogicBlocks().get(i).read();
            for(int j = 0; j < temp.length; j++){
                sourceData[k] = temp[j];
                k++;
            }
        }
        newFile.write(sourceData);
        newFile.close();
        files.add(newFile);
        return newFile;
    }


    public int getFileNum() {
        return fileNum;
    }

    public List<File> getFiles() {
        return files;
    }

    public int getId() {
        return id;
    }
}
