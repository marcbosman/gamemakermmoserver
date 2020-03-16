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
    }
    
    public short readLEShort(DataInputStream in) throws IOException{
        //reverse order from in.readShort() to have it as little endian
        return (short)(in.readByte() + (in.readByte() << 8));
    }
    
    public byte readByte(DataInputStream in) throws IOException{
        return in.readByte();
    }
}
