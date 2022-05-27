package com.thecoder.nachochat.Tools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;  
import java.util.Date; 

public class Logger {
    public static boolean debug = false;

    public static void log(String message) {
        print(message, null, null);
    }
    public static void log(String message, Exception e) {
        print(message, null, e);
    }
    public static void log(String message, String type) {
        print(message, type, null);
    }
    public static void log(String message, String type, Exception e) {
        print(message, type, e);
    }

    private static void print(String message, String type, Exception e) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
        Date date = new Date();
        String logMSG;
        
        String debugData;
        if (debug){
            debugData = " <"+KDebug.getCallerCallerClassName()+">";
        }
        else {
            debugData = "";
        }
        
        if (type != null) {
            logMSG = String.format("[%s]%s [%s] %s",formatter.format(date), debugData, type, message);
        }
        else {
            logMSG = String.format("[%s]%s [INFO] %s",formatter.format(date), debugData, message);
        }

        if (e != null) {
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            e.printStackTrace(pw);
            logMSG += "\n" + buffer.toString() + "-- ENDSTACKTRACE --";
        }
        System.out.println(logMSG);

        File file = new File("log.txt");
        if (file.length() > 1000000) {
            file.renameTo(new File("log.txt.old"));
            file = new File("log.txt");
        }

        try {
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(logMSG);
            bufferedWriter.newLine();
            bufferedWriter.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error al escribir en el log");
        }
    }
}