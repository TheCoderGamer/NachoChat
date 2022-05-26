package com.thecoder.nachochat.Client;

import java.awt.Cursor;
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JOptionPane;

import com.thecoder.nachochat.Tools.*;

public class ClientMain {

    private static LoginGUI loginFrame;

    static DataInputStream in;
    static DataOutputStream out;
    static InetAddress ip;
    static Socket sc;
    
    static boolean online = false;
    static ChatGUI chatGUI;

    static Thread read;
    
    public static void main(String[] args) throws Exception {   
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Logger.log("Startig client...");
                    loginFrame = new LoginGUI();
                    loginFrame.setVisible(true);
                } 
                catch (Exception e) {
                    Logger.log("Error starting client", "ERROR", e);
                }
            }
        });
    }

    public static void login(String username, String strIp, int port) {
        Logger.log(String.format("Attempting login in <%s:%s> with user <%s>", strIp, port, username)); 
        loginFrame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try{
            ip = InetAddress.getByName(strIp);
            sc = new Socket(ip, port);
            in = new DataInputStream(sc.getInputStream());
            out = new DataOutputStream(sc.getOutputStream());

            loginFrame.dispose();
            chatGUI = new ChatGUI();
        }
        catch (UnknownHostException e){
            Logger.log("IP address is invalid", "ERROR", e);
        } 
        catch (ConnectException e){
            Logger.log("No server is running on the specified port", "ERROR", e);
            noHost();
        }
        catch (IOException e) {
            Logger.log("Error conecting to server", "ERROR", e);
        } 

        online = true;

        read = new Thread("Read"){
            public void run(){
                while(online){
                    try {
                        chatGUI.sendToHistory(new String(readPacket()));
                    } catch (IOException e) {
                        Logger.log("Error reading packet", "ERROR", e);
                    }
                }
            }
        };
        read.start();
    }
    private static byte[] readPacket() throws IOException {
        int length = in.readInt();
        if(length>0) {
            byte[] message = new byte[length];
            in.readFully(message, 0, message.length);
            return message;
        }
        return null;
    }

    public static void send(String message) {
        message.trim();
        try {
            sendBytes(message.getBytes());
            Logger.log("Message sent: " + message);
        } catch (IOException e) {
            Logger.log("Error sending message", "ERROR", e);
        }
    } 
    private static void sendBytes(byte[] data) throws IOException {
        int len = data.length;
        out.writeInt(len);
        if (len > 0) {
            out.write(data, 0, len);
        }
    }

    public static void disconnect() {
        try {
            sc.close();
            System.exit(0);
        } catch (IOException e) {
            Logger.log("Error closing socket", "ERROR", e);
        }
    }

    private static void noHost(){
        loginFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        JOptionPane.showMessageDialog(loginFrame, "No se ha podido conectar a la direccion y puerto indicados", "Error", JOptionPane.OK_OPTION);
    }
}