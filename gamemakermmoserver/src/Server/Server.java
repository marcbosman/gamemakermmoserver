/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Constants.ServerConstants;
import Models.ModelController;
import Packets.PacketHandler;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author marcb
 */
public class Server implements Runnable {
    private ServerSocket serverSocket;
    private boolean running = false;
    private Socket client = null;
    BlockingQueue<Runnable> queue = new SynchronousQueue<>();
    private ExecutorService fixedThreadPool = new ThreadPoolExecutor(ServerConstants.MAX_PLAYERS, ServerConstants.MAX_PLAYERS, 0L, TimeUnit.MILLISECONDS, queue);
    
    public void launch() throws IOException {
        serverSocket = new ServerSocket(ServerConstants.PORT);
        
        //load all the resources for the server
        ModelController.getInstance().createMaps();
        
        //if everything is done loading start listening for clients
        System.out.println("Done loading, server started!");
        running = true;
        
        while(running){
            client = serverSocket.accept();
            try {
                fixedThreadPool.execute(new ClientHandler(client));
                System.out.println("Client connected: " + client.getInetAddress());
            } catch(RejectedExecutionException e){
                System.out.println("Client: " + client.getInetAddress() + " tried to connect but server is full");
                //write the handshake packet to the client telling the server is full and close the connection
                PacketHandler ph = new PacketHandler();
                ph.sendHandshake(new DataOutputStream(client.getOutputStream()), false);
                client.close();
            }
        }
    }

    @Override
    public void run() {
        try {
            launch();
        } catch (IOException e) {
            System.out.println("Couldn't start the server, check if port " + ServerConstants.PORT + " is already in use");
        }
    }
}
