package Block;

import java.io.Serializable;
import java.util.Date;

import Exception.ErrorCode;

public class LogicBlock implements Serializable {
    private Block[] blocks;
    public static final int LOGIC_BLOCK_NUM = 3;
    private BlockManager[] blockManagers;
    private final int BLOCK_SIZE = Block.BLOCK_SIZE;
    private int contentSize;

    public LogicBlock(BlockManager[] blockManagers) {
        this.blockManagers = new BlockManager[3];
        this.blockManagers = blockManagers;
        this.contentSize = 0;
        blocks = new Block[LOGIC_BLOCK_NUM];
    }

    public byte[] read(){
        Date date = new Date();
        int bkIndex = (int) date.getTime() % LOGIC_BLOCK_NUM;
        for(int i = 0; i < 3; i++ ){
            byte[]data = blocks[i].read();
            if(data != null){
                return data;
            }
           // bkIndex = (bkIndex + 1) % LOGIC_BLOCK_NUM;
        }

        throw new ErrorCode(ErrorCode.LOGIC_BLOCK_BROKEN);

    }

    public void write(byte[] content) throws Exception{
        if(content.length > Block.BLOCK_SIZE){
            try {
                throw new ErrorCode(ErrorCode.WRITE_OUT_OF_BOUND);
            }catch (ErrorCode errorCode){
                System.out.println(ErrorCode.getErrorText(errorCode.getErrorCode()));
            }
        }
        for(int i = 0; i < LOGIC_BLOCK_NUM; i++){
            blocks[i] = blockManagers[i].newBlock(content);
        }
        for(int i = 0; i < LOGIC_BLOCK_NUM;i++){
            blocks[i].setMyDuplication(this);
        }
        this.contentSize = content.length;
    }

    public int getContentSize(){return this.contentSize;}

    public Block[] getBlocks(){return this.blocks;}

    public String getBlocksName(){
        StringBuilder sb = new StringBuilder("");
        for(int i = 0; i < blocks.length; i++){
            sb.append(blocks[i].getName() + " ");
        }

        return sb.toString();
    }
}
