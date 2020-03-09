/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Packets.PacketHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author marcb
 */
public class ClientHandler implements Runnable {
    private Socket client = null;
    private boolean running = false;
    private DataOutputStream out;
    private DataInputStream in;
    
    private PacketHandler ph = new PacketHandler(this);
    
    public ClientHandler(Socket socket){
        client = socket;
    }

    @Override
    public void run() {
        try {
            running = true;
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
            
            //upon connecting send out a packet saying we connected successfully
            ph.sendHandshake(out, true);
            
            while(running){
                //each packet we send starts with a byte indicating the length
                int packetLength = in.read();
                
                //DataInputStream read() will return -1 if the end is reached indicating the client is closed
                if(packetLength == -1){
                    System.out.println("Connection with the client closed");
                    client.close();
                    running = false;
                    ph.clientLogout();
                    break;
                }
                
                //read the data in the packet and handle it
                ph.handlePacket(packetLength, in, out);
            }
        } catch (IOException e) {
            try {
                System.out.println("Client caused an error. Logging out and closing the connection");
                ph.clientLogout();
                client.close();
            } catch (IOException ex) {
                System.out.println("Error closing client");
            }
        }
    }
    
    public Socket getSocket(){
        return client;
    }
    
    public PacketHandler getPacketHandler(){
        return ph;
    }
}
