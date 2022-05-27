package com.thecoder.nachochat.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thecoder.nachochat.Tools.Logger;

public class ServerMain {
   
    public static void main(String[] args) throws Exception {   
        if (args.length == 0) {
            helpMsg();
        }
        else if(args.length == 1) {
            if (args[0].equals("-help")){
                System.out.println("--- NachoChatServer help ---");
                System.out.println("'-help': Shows this help");
                System.out.println("'-port 0000': Sets the port to listen to");
                System.out.println("'-port 0000 -log': Sets the port and enables logging");
            }
            else {
                helpMsg();
            }
        }
        else if(args.length == 2) {
            if (args[0].equals("-port")){
                // Check if port is valid
                String pattern = "^[0-9][0-9][0-9][0-9]$";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(args[1]);
                if (m.matches()){
                    int port = Integer.parseInt(args[1]);
                    start(port);
                }
                else {
                    System.out.println("Invalid port number");
                    helpMsg();
                }
            }
            else {
                helpMsg();
            }
        }
        else if (args.length == 3) {
            if (args[0].equals("-log")){
                Logger.debug = true;
            }
            else {
                helpMsg();
            }
        }
        else {
            helpMsg();
        }
    }
    private static void helpMsg(){
        System.out.println("Invalid arguments, use 'java -jar NachoChatServer -help' for help");
        System.exit(1);
    }
    
    private static void start(int port) {
        Server server = new Server();
        server.start(port);
    }
}
