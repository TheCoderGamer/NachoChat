package com.thecoder.nachochat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;

import com.thecoder.nachochat.Tools.*;

public class Server {
    
    ServerSocket servidor = null;
    Thread message;
    Thread connections;

    ArrayList<Client> clients = new ArrayList<Client>();

    private Boolean running = false;
    private IDManager IDmgr;
    private int maxConnections;

    public Server (){}
    public void start(int port, int maxConnections) {
        running = true;
        this.maxConnections = maxConnections;
        IDmgr = new IDManager(maxConnections);

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

        // Assing ID and chek if server is full
        int id = getID();
        if (id == -1) {
            Logger.log("Server is full", "CONNECTION");
            sendData("/err/El servidor esta lleno".getBytes(), in, out);
            sc.close();
            return;
        }
        
        // Accept connection
        sendData("/ack/".getBytes(), in, out);

        // Read username
        int length = in.readInt();
        byte[] message = new byte[length];
        in.readFully(message, 0, message.length);
        String username = new String(message);
        
        clients.add(new Client(this, username, sc.getInetAddress(), port, id, sc, in, out));
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

    private int getID() {
        int id = IDmgr.getID();
        if (id == -1) {
            if (clients.size() >= maxConnections){
                // Server full
                return -1;
            }
            else {
                IDmgr.disposeAllIDs();
                for (Client c : clients) {
                    c.setID(IDmgr.getID());
                }
                return IDmgr.getID();
            }
        }
        else {
            return id;
        }
    }

    private void sendData(byte[] data, DataInputStream in, DataOutputStream out) throws IOException {
        int len = data.length;
        out.writeInt(len);
        if (len > 0) {
            out.write(data, 0, len);
        }
    }

}

class IDManager {
    private ArrayList<Integer> id = new ArrayList<Integer>();
    private int index = 0;
    
    public IDManager(int maxIDs){
        for (int i = 0; i <= maxIDs; i++) {
            id.add(i);
        }
        Collections.shuffle(id);
        index = 0;
    }

    public int getID(){
        if (index == id.size()-1) {
            return -1;
        }
        return id.get(index++);
    }

    public void disposeAllIDs(){
        id.clear();
    }
}
