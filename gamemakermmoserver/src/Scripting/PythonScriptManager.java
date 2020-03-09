/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scripting;

import Server.ClientHandler;
import org.python.util.PythonInterpreter;

/**
 *
 * @author marcb
 */
public class PythonScriptManager {
    private PythonInterpreter py;
    private NPCScriptManager npcScriptManager;
    private ClientHandler ch;
    
    public PythonScriptManager(ClientHandler ch){
        this.ch = ch;
        
        System.out.println("Loading python script interpreter");
        py = new PythonInterpreter();
        
        //set script manager class so the python interpreter can use it
        npcScriptManager = new NPCScriptManager(this);
        py.set("npc", npcScriptManager);
    }
    
    public void executeNPCScript(String file){
        try {
            py.execfile("src/npcScripts/" + file + ".py");
        } catch (Exception e){
            System.out.println("Couldn't load the requested npc: " + file);
        }
    }
    
    public ClientHandler getClientHandler(){
        return ch;
    }
}
