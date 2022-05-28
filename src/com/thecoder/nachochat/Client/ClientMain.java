package com.thecoder.nachochat.Client;

import java.awt.Component;
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
    static UserlistGUI userlistGUI;

    static Thread read;
    
    public static void main(String[] args) throws Exception {   
        Logger.level = Logger.Level.NONE;
        if (args.length == 1) {
            if (args[0].matches("0|1|2|3")) {
                Logger.setLevel(Integer.parseInt(args[0]));
            }
        }
        requestLogin();
    }
    
    private static void requestLogin() {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Logger.log("Startig client...");
                    loginFrame = new LoginGUI();
                    loginFrame.setVisible(true);
                } 
                catch (Exception e) {
                    Logger.log("Error starting client", "ERROR", e);
                    System.exit(1);
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
        }
        catch (UnknownHostException e){
            Logger.log("IP address is invalid", "ERROR");
            noConnection();
            return;
        } 
        catch (ConnectException e){
            Logger.log("No server is running on the specified port", "ERROR");
            noConnection();
            return;
        }
        catch (IOException e) {
            Logger.log("Error conecting to server", "ERROR", e);
            noConnection();
            return;
        } 
        
        String status = initializeConnection(username);
        if (status.startsWith("/err/")){
            String errorMsg = "Error: " + status.substring(5);
            Logger.log(errorMsg, "ERROR");
            msgErrorBox(loginFrame, errorMsg);
            return;
        }
        // Conexion exitosa
        loginFrame.dispose();
        chatGUI = new ChatGUI();
        Logger.log("Succesfully connected to server");
        online = true;


        read = new Thread("Read"){
            public void run(){
                while(online){
                    try {
                        manageIncomingMessages(new String(readPacket()));
                    }
                    catch (Exception e) {
                        if (online) { 
                            disconnect(); 
                            Logger.log("Error reading packet, disconecting", "ERROR");
                        }
                    }
                }
            }
        };
        read.start();
    }
    private static byte[] readPacket() throws Exception {
        int length = in.readInt();
        if(length>0) {
            byte[] message = new byte[length];
            in.readFully(message, 0, message.length);
            return message;
        }
        return null;
    }

    private static String initializeConnection(String username) {
        try {
           // Wait for server to accept connection
           String data = new String(readPacket());
           if (!data.equals("/ack/")) { 
                return data;
            }
           
            // Send username
            sendBytes(username.getBytes());
            Logger.log("Sending username");
            return "";
        }
        catch (Exception e) {
            return "/err/Error conectando al servidor";
        }
    }

    public static void send(String message) {
        message = message.trim();
        message = message.replaceAll("/cmd/", "");

        try {
            sendBytes(message.getBytes());
            Logger.log(message, "CHAT");
        }
        catch (IOException e) {
            Logger.log("Error sending message", "ERROR", e);
            msgErrorBox(chatGUI, "Error sending message");
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
        online = false;
        try {
            sc.close();
            Logger.log("Disconnected from server");
            msgBox(chatGUI, "Desconectado del servidor");
            System.exit(0);
        }
        catch (IOException e) {
            Logger.log("Error closing socket", "ERROR", e);
            System.exit(1);
        }
    }

    private static void manageIncomingMessages (String message) {
        if (message.startsWith("/err/")) {
            String errorMsg = "Error: " + message.substring(5);
            Logger.log(errorMsg, "ERROR");
            msgErrorBox(chatGUI, errorMsg);
            return;
        }
        else if (message.startsWith("/cmd/")) {
            String cmd = message.substring(5);
            if (cmd.startsWith("users/")) {
                String usersMsg = cmd.substring(6);
                System.out.println(usersMsg);
                String[] users = usersMsg.split("/n/");
                userlistGUI.setUsers(users);
            }
        }
        else {
            chatGUI.sendToHistory(message);
        }
    }

    public static void showUserlist(){
        try {
            sendBytes("/cmd/getUsers".getBytes());
            userlistGUI = new UserlistGUI();
            userlistGUI.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void noConnection(){
        loginFrame.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        msgErrorBox(loginFrame, "No se ha podido conectar a la direccion y puerto indicados");
    }

    private static void msgErrorBox(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.OK_OPTION);
    }
    private static void msgBox(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "", JOptionPane.OK_OPTION);
    }
}