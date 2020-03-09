/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Constants.ServerConstants;

/**
 *
 * @author marcb
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Starting " + ServerConstants.NAME + " server, version: " + ServerConstants.VERSION);
        Thread serverThread = new Thread(new Server());
        serverThread.start();
    }
}
