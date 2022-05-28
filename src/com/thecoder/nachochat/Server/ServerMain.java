package com.thecoder.nachochat.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thecoder.nachochat.Tools.Logger;

public class ServerMain {
    private static int port;
    private static int maxUsers; 
   
    public static void main(String[] args) throws Exception {   
        
        if (args.length == 0) {
            helpMsg();
        }
        else if(args.length == 1) {
            if (args[0].equals("-help")){
                System.out.println("--- NachoChatServer help ---");
                System.out.println("'-help': Shows this help");
                System.out.println("'-port 0000 -max 100 -log 1': Starts the server with the specified port, max users and logging level (0 = none)");
            }
            else {
                helpMsg();
            }
        }
        else if(args.length == 6) {
            if (args[0].equals("-port")){
                // Check if port is valid
                String pattern = "^[0-9][0-9][0-9][0-9]$";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(args[1]);
                if (!m.matches()){
                    System.out.println("Invalid port number");
                    helpMsg();
                }
                else {
                    port = Integer.parseInt(args[1]);
                }
            }
            if (args[2].equals("-max")){
                // Check if max users is valid
                maxUsers = 0;
                try {
                    maxUsers = Integer.parseInt(args[3]);
                }
                catch (NumberFormatException e) {
                    System.out.println("Invalid max users number");
                    helpMsg();
                } 
                if (!(maxUsers > 0 && maxUsers < 10000)){
                    System.out.println("Invalid max users number");
                    helpMsg();
                }
            }
            if (args[4].equals("-log")){
                if (args[5].matches("0|1|2|3")) {
                    Logger.setLevel(Integer.parseInt(args[5]));
                }
            }
            else {
                helpMsg();
            }

            // All checks passed, start server
            start(port, maxUsers);
        }
    }
    private static void helpMsg(){
        System.out.println("Invalid arguments, use 'java -jar NachoChatServer -help' for help");
        System.exit(1);
    }
    
    private static void start(int port, int maxUsers) {
        Server server = new Server();
        server.start(port, maxUsers);
    }
}
