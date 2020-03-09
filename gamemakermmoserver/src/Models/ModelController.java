/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Models;

import Database.DatabaseConnection;
import Models.GameModels.GameCharacter;
import Models.GameModels.GameRoom;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;

/**
 *
 * @author marcb
 */
public class ModelController {
    private static ModelController instance = null;
    
    private ArrayList<GameCharacter> allUsers;
    private ArrayList<GameRoom> allMaps;
    private DatabaseConnection database;
    
    public static ModelController getInstance(){
        if(instance == null){
            instance = new ModelController();
        }
        return instance;
    }
    
    public ModelController(){
        allUsers = new ArrayList<>();
        allMaps = new ArrayList<>();
        database = new DatabaseConnection();
    }
    
    public boolean registerUser(String username, String password){
        //check if user already exists on the server
        for(GameCharacter gc : allUsers){
            if(gc.getUsername().equals(username)) return false;
        }
        
        //if user doesn't exist on server check the database
        Connection c = database.getConnection();
        if(c != null){
            //try-with the connection so it will be closed automaticly
            try (c){
                PreparedStatement ps = c.prepareStatement("SELECT * FROM accounts WHERE username=?");
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();
                
                //if user exists in the database stop the register progress
                if(rs.next()){
                    return false;
                }
                
                //if the user didn't already exist on the server and database create it
                GameCharacter gc = new GameCharacter(username, password, getMap("rm_map_1"));

                //and don't forget to save the new user in the database
                ps = c.prepareStatement("INSERT INTO accounts VALUES(?,?,?,?,?)");
                ps.setString(1, gc.getUsername());
                ps.setString(2, gc.getPassword());
                ps.setInt(3, gc.getPosX());
                ps.setInt(4, gc.getPosY());
                ps.setString(5, gc.getCurrentRoom().getRoomName());
                int result = ps.executeUpdate();
                if(result == 1) System.out.println("Added the account to the database");
                
                //finally add the user to the server
                allUsers.add(gc);
            } catch (SQLException e) {
                System.out.println("Error creating new user in the database");
                return false;
            }
        }
        return true;
    }
    
    public GameCharacter getUser(String username, String password){
        //check if user is already loaded on the server
        for(GameCharacter gc : allUsers){
            if(gc.getUsername().equals(username)){
                if(gc.getPassword().equals(password)) return gc;
            }
        }
        
        //if user isn't already loaded check the database
        Connection c = database.getConnection();
        if(c == null) return null;
        
        try (c){
            PreparedStatement ps = c.prepareStatement("SELECT * FROM accounts WHERE username=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            
            if(rs.next()){
                if(rs.getString("password").equals(password)){
                    GameCharacter gc = new GameCharacter(username, password, getMap(rs.getString("map")));
                    //also set their current position
                    gc.setPosX(rs.getInt("posX"));
                    gc.setPosY(rs.getInt("posY"));
                    allUsers.add(gc);
                    return gc;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading user from the database");
        }
        return null;
    }
    
    public boolean saveUser(GameCharacter gc){
        //save the user in the database and return if it was successful
        Connection c = database.getConnection();
        if(c == null) return false;
        
        try (c){
            PreparedStatement ps = c.prepareStatement("UPDATE accounts SET posX=?, posY=?, map=? WHERE username=?");
            ps.setInt(1, gc.getPosX());
            ps.setInt(2, gc.getPosY());
            ps.setString(3, gc.getCurrentRoom().getRoomName());
            ps.setString(4, gc.getUsername());
            int result = ps.executeUpdate();
            if(result == 1) return true;
        } catch (SQLException e) {
            System.out.println("Error saving user to the database");
        }
        //if there was an error or no rows were updated return false
        return false;
    }
    
    public void createMaps(){
        //get the maps from the database
        Connection c = database.getConnection();
        try (c){
            String query = "SELECT * FROM maps";
            Statement st = c.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            //load the maps from the database and show how many maps are successfully loaded
            int count = 0;
            while(rs.next()){
                GameRoom gr = new GameRoom(rs.getString("roomName"), rs.getInt("startX"), rs.getInt("startY"));
                allMaps.add(gr);
                count += 1;
            }
            System.out.println("Loaded " + count + " maps from the database");
        } catch (SQLException e) {
            System.out.println("Error loading maps from the database");
        }
    }
    
    public GameRoom getMap(String roomName){
        for(GameRoom gr : allMaps){
            if(gr.getRoomName().equals(roomName)){
                return gr;
            }
        }
        return null;
    }
}
