package Block;

import Exception.ErrorCode;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BlockManager implements Serializable{
    private final int id;
    private List<Block> blocks;
    private String name;

    public BlockManager(int id) {
        this.id = id;
        this.blocks = new ArrayList<>();
        name = "bm" + id;
        blocks = new ArrayList<>();
    }


    public Block getBlock(int index) {
        for(int i = 0; i < this.blocks.size(); i ++){
            if(blocks.get(i).getIndex() == index){
                return blocks.get(i);
            }
        }
        throw new ErrorCode(ErrorCode.BLOCK_NOT_FOUND);
    }


    public Block newBlock(byte[] b) throws Exception{
        Block newBlock = new Block(blocks.size() + 1, this);
        blocks.add(newBlock);
        newBlock.write(b);
        return newBlock;
    }

    public Block newEmptyBlock(int blockSize) {
        return null;
    }

    public int getId() {
        return id;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public String getName() {
        return name;
    }
}
