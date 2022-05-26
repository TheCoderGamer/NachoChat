package com.thecoder.nachochat.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    private String username;
    private InetAddress ip;
    private int port;
    private final int ID;
    private Socket sc;
    private DataInputStream in;
    private DataOutputStream out;
    private boolean online;
    public Thread thread;

    public Client (Server server, String username, InetAddress ip, int port, int ID, Socket sc, DataInputStream in, DataOutputStream out) {
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
                        System.out.println(message);
                        server.sendToAll(message);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                } 
            }
            private byte[] readPacket() throws IOException {
                int length = in.readInt();
                if(length>0) {
                    byte[] message = new byte[length];
                    in.readFully(message, 0, message.length);
                    System.out.println("Packet received");
                    return message;
                }
                return null;
            }
        };
        thread.start();
    }

    public void sendMessage(byte[] data) throws IOException {
        int len = data.length;
        out.writeInt(len);
        if (len > 0) {
            out.write(data, 0, len);
        }
    }

    public void kick(){
        online = false;
        try {
            sc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // necesita ser eliminado desde fuera
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
}
