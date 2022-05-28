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
    public static enum Level {
        NONE, ERROR, INFO, DEBUG;
    }
    public static Level level = Level.NONE;

    public static void setLevel(int level) {
        switch (level) {
            case 0:
                Logger.level = Level.NONE;
                break;
            case 1:
                Logger.level = Level.ERROR;
                break;
            case 2:
                Logger.level = Level.INFO;
                break;
            case 3:
                Logger.level = Level.DEBUG;
                break;
        }
    }

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
        
        String debugMsg = "";
        if (Logger.level == Level.DEBUG) {
            debugMsg = " <" + KDebug.getCallerCallerClassName()  + ">";
        }
        
        if (type != null) {
            logMSG = String.format("[%s]%s [%s] %s",formatter.format(date), debugMsg, type, message);
        }
        else {
            logMSG = String.format("[%s]%s [INFO] %s",formatter.format(date), debugMsg, message);
        }

        if (e != null) {
            Writer buffer = new StringWriter();
            PrintWriter pw = new PrintWriter(buffer);
            e.printStackTrace(pw);
            logMSG += "\n" + buffer.toString() + "-- ENDSTACKTRACE --";
        }
        System.out.println(logMSG);

        // Write to file
        if (Logger.level == Level.NONE) { return; }
        else if (Logger.level == Level.INFO) { 
           if (type != null) { return; }
        }
        else if (Logger.level == Level.ERROR) {
            if (type != "ERROR") { return; }
        }

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

class KDebug {
    public static String getCallerCallerClassName() { 
       StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
       String callerClassName = null;
       for (int i=1; i<stElements.length; i++) {
           StackTraceElement ste = stElements[i];
           if (!ste.getClassName().equals(KDebug.class.getName())&& ste.getClassName().indexOf("java.lang.Thread")!=0) {
               if (callerClassName==null) {
                   callerClassName = ste.getClassName();
               } else if (!callerClassName.equals(ste.getClassName())) {
                   return ste.getClassName();
               }
           }
       }
       return null;
    }
}