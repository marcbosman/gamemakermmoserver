/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models.GameModels;

/**
 *
 * @author marcb
 */
public class GameCharacter {
    private String username;
    private String password;
    
    private GameRoom currentRoom;
    private int posX;
    private int posY;
    
    private boolean online;
    
    public GameCharacter(String username, String password, GameRoom currentRoom){
        this.username = username;
        this.password = password;
        this.currentRoom = currentRoom;
        posX = currentRoom.getStartX();
        posY = currentRoom.getStartY();
        online = false;
    }
    
    public String getUsername(){
        return username;
    }

    public String getPassword() {
        return password;
    }

    public GameRoom getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(GameRoom currentRoom) {
        this.currentRoom = currentRoom;
    }

    public int getPosX() {
        return posX;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public int getPosY() {
        return posY;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }
    
    public boolean isOnline(){
        return online;
    }
    
    public void setOnline(boolean online){
        this.online = online;
    }
}
