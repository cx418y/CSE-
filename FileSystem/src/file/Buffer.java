package file;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Buffer implements Serializable {
    private byte[] buffer;
    private int position;

    public Buffer(){
        buffer = new byte[0];
    }

    public Buffer(byte[] data){
        buffer = new byte[data.length];
        for(int i = 0; i < data.length; i++){
            buffer[i] = data[i];
        }
        position = data.length - 1;
    }

    public void write(byte[] content,int position){
        byte[] temp = new byte[buffer.length + content.length];

        for(int i = 0; i < position; i++){
            temp[i] = buffer[i];
        }
        for(int i = 0; i < content.length; i++){
            temp[i + position] = content[i];
        }
        for(int i = position + content.length; i < buffer.length + content.length;i++){
            temp[i] = buffer[i-content.length];
        }
        buffer = temp;
    }

    public byte[] getBuffer() {
        return buffer;
    }
}
