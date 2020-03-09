/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Packets;

import Models.GameModels.GameCharacter;
import Models.ModelController;
import Scripting.PythonScriptManager;
import Server.ClientHandler;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author marcb
 */
public class PacketHandler {
    private ClientHandler clientHandler;
    private GameCharacter gameCharacter;
    private ByteArrayReader bar;
    private ByteArrayWriter baw;
    private ModelController modelManager;
    private PythonScriptManager scriptManager;
    
    public PacketHandler(ClientHandler ch){
        clientHandler = ch;
        modelManager = ModelController.getInstance();
        scriptManager = new PythonScriptManager(clientHandler);
        bar = new ByteArrayReader();
        baw = new ByteArrayWriter();
    }
    
    //for sending the handshake packet to rejected clients
    public PacketHandler(){
        baw = new ByteArrayWriter();
    }

    public void handlePacket(int packetLength, DataInputStream in, DataOutputStream out) throws IOException{
        String command = bar.readNullTerminatedString(packetLength, in);
        System.out.println("Received a packet with command: " + command);
        
        //handling the command from the received packet
        switch(command){
            case "debug": handleDebug(packetLength, in); break;
            case "login": handleLogin(packetLength, in, out); break;
            case "register": handleRegister(packetLength, in, out); break;
            case "logout": clientLogout(); break;
            case "pos": handlePos(packetLength, in); break;
            case "chat": handleChat(packetLength, in); break;
            case "changemap": handleChangeMap(packetLength, in, out); break;
            case "npc": handleNpcChat(packetLength, in, out); break;
            default: handleUnknown(command, packetLength, in);
        }
    }
    
    public void handleDebug(int packetLength, DataInputStream in) throws IOException{
        String message = bar.readNullTerminatedString(packetLength, in);
        System.out.println("Debug message: " + message);
    }
    
    public void handleLogin(int packetLength, DataInputStream in, DataOutputStream out) throws IOException{
        String username = bar.readNullTerminatedString(packetLength, in);
        String password = bar.readNullTerminatedString(packetLength, in);
        GameCharacter gc = modelManager.getUser(username, password);
        
        boolean result = true;
        
        //the reason if a login attempt fails
        String reason = "";
        if(gc == null){
            reason = "INVALID";
            result = false;
        } else if(gc.isOnline() == true){
            reason = "ONLINE";
            result = false;
        }
        
        //Start the login packet
        baw.writeNullTerminatedString("LOGIN");
        baw.writeNullTerminatedString(Boolean.toString(result));
        
        if(result == false){
            //if the login attempt failed add the reason
            baw.writeNullTerminatedString(reason);
            out.write(baw.getPacket());
            return;
        } else {
            //if the login is successful add the game character to this handler
            gameCharacter = gc;
        }
        
        //add the character information to the packet
        baw.writeNullTerminatedString(gc.getCurrentRoom().getRoomName());
        baw.writeLEInt(gc.getPosX());
        baw.writeLEInt(gc.getPosY());
        baw.writeNullTerminatedString(gc.getUsername());
        out.write(baw.getPacket());
        
        //get all the characters currently in the map already and send them to the player
        for(ClientHandler ch : gc.getCurrentRoom().getAllClients()){
            GameCharacter otherCharacter = ch.getPacketHandler().gameCharacter;
            baw.writeNullTerminatedString("ENTERMAP");
            baw.writeNullTerminatedString(otherCharacter.getUsername());
            baw.writeLEInt(otherCharacter.getPosX());
            baw.writeLEInt(otherCharacter.getPosY());
            out.write(baw.getPacket());
        }
        
        //add our player to the map and login
        gameCharacter.getCurrentRoom().addClient(clientHandler);
        gameCharacter.setOnline(true);
        
        //notify others in the map the player logged in
        baw.writeNullTerminatedString("ENTERMAP");
        baw.writeNullTerminatedString(gc.getUsername());
        baw.writeLEInt(gc.getPosX());
        baw.writeLEInt(gc.getPosY());
        broadcastRoom(baw.getPacket());
    }
    
    public void handleRegister(int packetLength, DataInputStream in, DataOutputStream out) throws IOException{
        String username = bar.readNullTerminatedString(packetLength, in);
        String password = bar.readNullTerminatedString(packetLength, in);
        boolean result = modelManager.registerUser(username, password);
        
        //send a packet back whether or not the registration was successful
        baw.writeNullTerminatedString("REGISTER");
        baw.writeNullTerminatedString(Boolean.toString(result));
        out.write(baw.getPacket());
    }
    
    public void handlePos(int packetLength, DataInputStream in) throws IOException{
        int pos_x = bar.readLEInt(in);
        int pos_y = bar.readLEInt(in);
        
        //change the position of the character
        gameCharacter.setPosX(pos_x);
        gameCharacter.setPosY(pos_y);
        
        //send the new position to other players in the same map
        baw.writeNullTerminatedString("POS");
        baw.writeNullTerminatedString(gameCharacter.getUsername());
        baw.writeLEInt(gameCharacter.getPosX());
        baw.writeLEInt(gameCharacter.getPosY());
        broadcastRoom(baw.getPacket());
    }
    
    public void handleChat(int packetLength, DataInputStream in) throws IOException{
        String message = bar.readNullTerminatedString(packetLength, in);
        
        //send the chat message to other players in the same map
        baw.writeNullTerminatedString("CHAT");
        baw.writeNullTerminatedString(gameCharacter.getUsername());
        baw.writeNullTerminatedString(message);
        broadcastRoom(baw.getPacket());
    }
    
    public void handleChangeMap(int packetLength, DataInputStream in, DataOutputStream out) throws IOException{
        String newMap = bar.readNullTerminatedString(packetLength, in);
        int targetX = bar.readLEInt(in);
        int targetY = bar.readLEInt(in);
        
        //notify players that the player is leaving the map
        baw.writeNullTerminatedString("LEAVEMAP");
        baw.writeNullTerminatedString(gameCharacter.getUsername());
        broadcastRoom(baw.getPacket());
        
        //get all the characters currently in the new map already and send them to the player
        for(ClientHandler ch : modelManager.getMap(newMap).getAllClients()){
            GameCharacter otherCharacter = ch.getPacketHandler().gameCharacter;
            baw.writeNullTerminatedString("ENTERMAP");
            baw.writeNullTerminatedString(otherCharacter.getUsername());
            baw.writeLEInt(otherCharacter.getPosX());
            baw.writeLEInt(otherCharacter.getPosY());
            out.write(baw.getPacket());
        }
        //leave the current room
        gameCharacter.getCurrentRoom().removeClient(clientHandler);
        
        //join the new room and update position
        gameCharacter.setCurrentRoom(modelManager.getMap(newMap));
        gameCharacter.setPosX(targetX);
        gameCharacter.setPosY(targetY);
        gameCharacter.getCurrentRoom().addClient(clientHandler);
        
        //notify others in the map the player logged in
        baw.writeNullTerminatedString("ENTERMAP");
        baw.writeNullTerminatedString(gameCharacter.getUsername());
        baw.writeLEInt(gameCharacter.getPosX());
        baw.writeLEInt(gameCharacter.getPosY());
        broadcastRoom(baw.getPacket());
    }
    
    public void handleNpcChat(int packetLength, DataInputStream in, DataOutputStream out) throws IOException{
        String npc = bar.readNullTerminatedString(packetLength, in);
        scriptManager.executeNPCScript(npc);
    }
    
    public void sendNpcOk(String message) throws IOException{
        baw.writeNullTerminatedString("NPCOK");
        baw.writeNullTerminatedString(message);
        clientHandler.getSocket().getOutputStream().write(baw.getPacket());
    }
    
    public void handleUnknown(String command, int packetLength, DataInputStream in) throws IOException{
        //check how many bytes remain in the packet and skip them
        baw.writeNullTerminatedString(command);
        in.skipBytes(packetLength - baw.getPacket().length);
        System.out.println("Skipped an unknown request: " + command);
    }
    
    //Sending the handshake packet telling Gamemaker studio we connected
    public void sendHandshake(DataOutputStream out, boolean result) throws IOException{
        baw.writeNullTerminatedString("HELLO");
        baw.writeNullTerminatedString(Long.toString(System.currentTimeMillis()));
        baw.writeByte((byte)(result ? 1 : 0 )); //boolean for being accepted online
        out.write(baw.getPacket());
    }
    
    public void sendDebugMessage(String message, DataOutputStream out) throws IOException{
        baw.writeNullTerminatedString("DEBUG");
        baw.writeNullTerminatedString(message);
        out.write(baw.getPacket());
    }
    
    public void broadcastRoom(byte[] packet) throws IOException{
        for(ClientHandler ch : gameCharacter.getCurrentRoom().getAllClients()){
            if(!ch.equals(clientHandler)){
                if(!ch.getSocket().isClosed()){
                    ch.getSocket().getOutputStream().write(packet);
                }
            }
        }
    }
    
    public void clientLogout() throws IOException{
        //if there is no character logged in there is no need to do anything
        if(gameCharacter == null) return;
        
        //notify all players that we are leaving the map
        baw.writeNullTerminatedString("LEAVEMAP");
        baw.writeNullTerminatedString(gameCharacter.getUsername());
        broadcastRoom(baw.getPacket());
        
        //remove the client from the map he is logged into, logout and save
        gameCharacter.getCurrentRoom().removeClient(clientHandler);
        gameCharacter.setOnline(false);
        modelManager.saveUser(gameCharacter);
        
        //remove reference to the gameCharacter from this handler
        gameCharacter = null;
    }
}
