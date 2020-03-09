/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 *
 * @author marcb
 */
public class ByteArrayWriter {
    private ByteArrayOutputStream byteWriter = new ByteArrayOutputStream();;
    
    public void writeByte(byte b){
        byteWriter.write(b);
    }
    
    public void writeLEInt(int i) throws IOException {
        byteWriter.write((byte)(i & 0xFF));
        byteWriter.write((byte)((i >> 8) & 0xFF));
        byteWriter.write((byte)((i >> 16) & 0xFF));
        byteWriter.write((byte)((i >> 24) & 0xFF));
    }
    
    public void writeLEShort(short s) throws IOException {
        byteWriter.write((byte)(s & 0xFF));
        byteWriter.write((byte)((s >> 8) & 0xFF));
    }
    
    public void writeNullTerminatedString(String string) throws IOException {
        byteWriter.write(string.getBytes());
        byteWriter.write((byte)0);
    }
    
    public byte[] getPacket() throws IOException {
        //Return a packet with length at the start followed by the content
        byte[] content = byteWriter.toByteArray();
        //reset the byteWriter for the next packet
        byteWriter.reset();
        
        ByteArrayOutputStream finalPacket = new ByteArrayOutputStream();
        finalPacket.write((byte)(content.length + 1));
        finalPacket.write(content);
        return finalPacket.toByteArray();
    }
}
