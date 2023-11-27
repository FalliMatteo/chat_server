package com.example;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class App {
    static ArrayList <ServerThread> peopleInChat = new ArrayList<ServerThread>();
    public static void main( String[] args ) {
        try {
            ServerSocket servsock= new ServerSocket(3000);
            boolean loop = true;
            while(loop){
                System.out.println("The server is waiting");
                Socket s = servsock.accept();
                ServerThread thread=new ServerThread(s);
                System.out.println("A new client is connected with the thread " + thread.getName());
                thread.start();
                peopleInChat.add(thread);
            }
            servsock.close();
            System.out.println("Server shutdown");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}