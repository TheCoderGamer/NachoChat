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
            System.exit(1);
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
        Logger.log("New client connected", "CONNECTION");
        DataInputStream in = new DataInputStream(sc.getInputStream());
        DataOutputStream out = new DataOutputStream(sc.getOutputStream());
        ID++; // TODO: buscar otra manera para evitar overflow
        // Read username
        int length = in.readInt();
        byte[] message = new byte[length];
        in.readFully(message, 0, message.length);
        String username = new String(message);
        clients.add(new Client(this, username, sc.getInetAddress(), port, ID, sc, in, out));
        Logger.log("Successfully connected to client <" + username + ">", "CONNECTION");
        sendToAll(String.format("<%s> se ha conectado!", username));
    }

    public void stop() {
        running = false;
        sendToAll("Server stopping...");
        try {
            for (Client c : clients) {
                kick(c);
            }
            servidor.close();
        } catch (IOException e) {
            Logger.log("Error closing server", "ERROR", e);
            System.exit(1);
        }
        Logger.log("Server stopped");
        System.exit(0);
    }
 
    public void sendToAll(String message) {
        Logger.log(message, "CHAT");
        for(Client c : clients){
            try {
                c.sendMessage(message.getBytes());
            }
            catch (IOException e) {
                Logger.log("Error sending message to client", "ERROR", e);
                e.printStackTrace();
            }
        }
    }

    public String[] getUsers() {
        String[] users = new String[clients.size()];
        for(int i = 0; i < clients.size(); i++){
            users[i] = clients.get(i).getUsername();
        }
        return users;
    }

    public void kick(Client c) {
        if (!c.online) { return; }
        c.online = false;
        String user = c.getUsername();
        c.closeConnection();
        clients.remove(c);
        Logger.log("Client <" + user + "> disconected", "CONNECTION");
        sendToAll("<" + user + "> se ha desconectado!");
    }

}
