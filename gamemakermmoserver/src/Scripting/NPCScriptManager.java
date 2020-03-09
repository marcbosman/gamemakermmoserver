/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Scripting;

import java.io.IOException;

/**
 *
 * @author marcb
 */
public class NPCScriptManager {
    private PythonScriptManager psm;
    
    public NPCScriptManager(PythonScriptManager psm){
        this.psm = psm;
    }
    public void sendNpcOk(String message) throws IOException{
        psm.getClientHandler().getPacketHandler().sendNpcOk(message);
    }
}
