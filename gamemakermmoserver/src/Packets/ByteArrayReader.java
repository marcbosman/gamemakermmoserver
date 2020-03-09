/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Packets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author marcb
 */
public class ByteArrayReader {
    public String readNullTerminatedString(int packetLength, DataInputStream in) throws IOException{
        ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
        
        byte data;
        for (int i = 0; i < packetLength; i++){ //prevent infinite loop if string isn't terminated properly
            data = in.readByte();
            if (data == 0) break;
            byteWriter.write(data);
        }
        return new String(byteWriter.toByteArray());
    }
    
    public int readLEInt(DataInputStream in) throws IOException{
        //reverse order from in.readInt() to have it as little endian
        return in.read() + (in.read() << 8) + (in.read() << 16) + (in.read() << 24);
        
        /* old method
        ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();
        
        byte data;
        for (int i = 0; i < 4; i++){
            data = in.readByte();
            byteWriter.write(data);
        }
        
        //Gamemaker studio sends the number with little endian byte order
        int result = ByteBuffer.wrap(byteWriter.toByteArray()).order(ByteOrder.LITTLE_ENDIAN).getInt();
        return result;
        */
    }
    
    public short readLEShort(DataInputStream in) throws IOException{
        //reverse order from in.readShort() to have it as little endian
        return (short)(in.readByte() + (in.readByte() << 8));
        
        /* old method
        byteWriter.reset();
        
        byte data;
        for (int i = 0; i < 2; i++){
            data = in.readByte();
            byteWriter.write(data);
        }
        
        short result = ByteBuffer.wrap(byteWriter.toByteArray()).order(ByteOrder.LITTLE_ENDIAN).getShort();
        return result;
        */
    }
    
    public byte readByte(DataInputStream in) throws IOException{
        return in.readByte();
    }
}
