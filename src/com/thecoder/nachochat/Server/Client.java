package com.thecoder.nachochat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import com.thecoder.nachochat.Tools.Logger;

public class Client {
    private Server server;
    private String username;
    private InetAddress ip;
    private int port;
    private int ID;
    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    boolean online;
    public Thread thread;

    public Client (Server server, String username, InetAddress ip, int port, int ID, Socket sc, DataInputStream in, DataOutputStream out) {
        this.server = server;
        this.username = username;
        this.ip = ip;
        this.port = port;
        this.ID = ID;
        this.sc = sc;
        this.in = in;
        this.out = out;

        this.online = true;

        thread = new Thread("Message") {
            public void run() {
                while (online) {
                    try {
                        String message = new String(readPacket());
                        mannageMessage(message);
                    }
                    catch (IOException e) {
                        Logger.log("Error reading message", "ERROR", e);
                        server.kick(Client.this);
                    }
                } 
            } 
        };
        thread.start();
    }

    private void mannageMessage (String message) {
        if (message.startsWith("/quit")) {
            server.kick(Client.this);
        }
        else if (message.startsWith("/cmd/")) {
            // Manage commands
            message = message.substring(5);
            if (message.equals("getUsers")){
                String users = "/cmd/users/";
                for (String c : server.getUsers()) {
                    users += c + "/n/";
                }
                try {
                    System.out.println("Sending: " + users);
                    sendMessageThread(users.getBytes());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            String fullMessage = String.format("<%s> %s", username, message);
            server.sendToAll(fullMessage);
        }
    }

    private byte[] readPacket() throws IOException   {
        int length = in.readInt();
        if(length>0) {
            byte[] message = new byte[length];
            in.readFully(message, 0, message.length);
            return message;
        }
        return null;
    }

    void closeConnection() {
        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void sendMessage(byte[] data) throws IOException {
        int len = data.length;
        out.writeInt(len);
        if (len > 0) {
            out.write(data, 0, len);
        }
    }
    private void sendMessageThread(byte[] data) throws IOException {
        Thread send = new Thread("Send") {
            public void run() {
                try {
                    sleep(100);
                    sendMessage(data);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        };
        send.start();
    }

    public String getUsername() {
        return username;
    }
    public InetAddress getIP() {
        return ip;
    }
    public int getPort() {
        return port;
    }
    public int getID() {
        return ID;
    }
    public Socket getSocket() {
        return sc;
    }
    public DataInputStream getIn() {
        return in;
    }
    public DataOutputStream getOut() {
        return out;
    }
    public void setID(int ID){
        this.ID = ID;
    }
}
