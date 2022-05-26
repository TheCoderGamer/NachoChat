package com.thecoder.nachochat.Server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerMain {
    public static void main(String[] args) throws Exception {
        if (args.length == 2) {
            if (!args[0].equals("-port")){
                System.out.println("Usage: java -jar ServerMain -port <port>");
                System.exit(1);
            }
            
            String text = args[1];
            String pattern = "^[0-9][0-9][0-9][0-9]$";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(text);
            if (!m.matches()) { 
                System.out.println("Usage: java -jar ServerMain -port <port>");
                System.out.println("port must be a number between 0000 and 9999");
                System.exit(1);
             }
        }
        else {
            System.out.println("Usage: java -jar ServerMain -port <port>");
            System.exit(1);
        }
        
        int port = 0;
        try {
            port = Integer.parseInt(args[1]);
        }catch(NumberFormatException e) {
            System.out.println("Usage: java -jar ServerMain -port <port>");
            System.out.println("port must be a number between 0000 and 9999");
            System.exit(1);
        }

        Server server = new Server();
        server.start(port);
    }
}
