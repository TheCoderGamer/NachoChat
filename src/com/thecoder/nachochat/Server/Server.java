package com.thecoder.nachochat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.thecoder.nachochat.Tools.*;

public class Server {
    
    ServerSocket servidor = null;
    Thread message;
    Thread connections;
    int ID = 0;

    ArrayList<Client> clients = new ArrayList<Client>();

    private Boolean running = false;

    public Server (){

    }
    public void start(int port) {     
        running = true;

        try {
            servidor = new ServerSocket(port);
            Logger.log("Server started on port " + port);
        } 
        catch (IOException e) {
            Logger.log("Error starting server", "ERROR", e);
        }
        
        connections = new Thread("Connections"){
            public void run(){
                while(running){
                    try {
                        connectClients(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        connections.start();
          
    }
    private void connectClients(int port) throws IOException {
        Socket sc = servidor.accept();
        Logger.log("New client connected");
        DataInputStream in = new DataInputStream(sc.getInputStream());
        DataOutputStream out = new DataOutputStream(sc.getOutputStream());
        ID++;
        clients.add(new Client(this, "test", sc.getInetAddress(), port, ID, sc, in, out));
    }

    public void stop() {
        running = false;
        try {
            servidor.close();
        } catch (IOException e) {
            Logger.log("Error closing server", "ERROR", e);
        }
        Logger.log("Server stopped");
        System.exit(0);
    }
 
    public void sendToAll(String message) {
        for(Client c : clients){
            try {
                c.sendMessage(message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
