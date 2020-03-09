/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models.GameModels;

import Server.ClientHandler;
import java.util.ArrayList;

/**
 *
 * @author marcb
 */
public class GameRoom {
    private String roomName;
    private int startX;
    private int startY;
    
    private ArrayList<ClientHandler> allClients;
    
    public GameRoom(String roomName, int startX, int startY){
        this.roomName = roomName;
        this.startX = startX;
        this.startY = startY;
        allClients = new ArrayList<>();
    }
    
    public void addClient(ClientHandler user){
        allClients.add(user);
    }
    
    public ArrayList<ClientHandler> getAllClients(){
        return allClients;
    }
    
    public void removeClient(ClientHandler clientHandler){
        allClients.remove(clientHandler);
    }
    
    public String getRoomName(){
        return roomName;
    }
    
    public int getStartX(){
        return startX;
    }
    
    public int getStartY(){
        return startY;
    }
}
