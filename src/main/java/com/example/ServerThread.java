package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread extends Thread{
    private Socket socket;
    public String username;
    private BufferedReader input;
    private Sender sender;
    public Semaforo semaforo;

    public ServerThread(Socket socket) {
        this.socket = socket;
        this.username = "";
        this.sender = new Sender(this.socket);
        this.semaforo = new Semaforo();
        try{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public String getUsername(){
        return this.username;
    }

    public Socket getSocket(){
        return this.socket;
    }

    public void sendMessage(String message){
        semaforo.p();
        sender.addMessage(message);
        semaforo.v();
    }

    public boolean setUsername(String string) throws IOException{
        boolean kek = true;
        for(int i=0; i<App.peopleInChat.size(); i++){
            if(App.peopleInChat.get(i).getUsername().equals(string)){
                kek = false;
            }
        }
        if(kek){
            this.username = string;
        }
        return kek;
    }

    public boolean sendBroadcast(String message) throws IOException{
        boolean kek = false;
        for(int i=0; i<App.peopleInChat.size(); i++){
            if(!this.username.equals(App.peopleInChat.get(i).getUsername()) || !App.peopleInChat.get(i).getUsername().equals("")){
                App.peopleInChat.get(i).sendMessage(message);;
                kek = true;
            }
        }
        return kek;
    }

    public void sendPrivate(String username, String message) throws IOException{
        boolean kek = false;
        for(int i=0; i<App.peopleInChat.size(); i++){
            if(App.peopleInChat.get(i).getUsername().equals(username) && !App.peopleInChat.get(i).getUsername().isEmpty()){
                App.peopleInChat.get(i).sendMessage(this.username + " sent you this message: " + message);
                kek = true;
                return;
            }
        }
        if(!kek){
            sendMessage("This user doesn't exist");
        }
    }

    public void sendList(){
        String users = "";
        for(int i=0; i<App.peopleInChat.size(); i++){
            if(!App.peopleInChat.get(i).getUsername().isEmpty()){
                users += App.peopleInChat.get(i).getUsername() + ", ";
            }
        }
        if(users.isEmpty()){
            users = "No users online";
        }else{
            users = "Users online: " + users;
        }
        sendMessage(users);
    }

    public void sendCommands(){
        String commands = "Here are all commands:   @list = to see all the users that are online,   @everyone / ... = to send a message to every user online,   @exit = to close the connection,   @username / ... = to change your username,   USERNAME / ... = to send a message to a single user";
        sendMessage(commands);
    }

    @Override
    public void run(){
        try {
            sender.start();
            String incoming;
            String message[];
            boolean exit = false;
            boolean kek;
            do{
                incoming = input.readLine();
                kek = setUsername(incoming);
                if(kek){
                    sendMessage("You finally entered the chat"); 
                }else{
                    sendMessage("Your username already exists or is invalid");        
                }
            }while(!kek);
            System.out.println(this.username + " entered the chat");
            sendBroadcast(this.username + " entered the chat");
            while(!exit){
                sendMessage("Insert your next message/command (Write @commands to see all the commands available)");
                incoming = input.readLine();
                message = incoming.split(" / ");
                switch(message[0]){
                    case "@commands":
                        if(message.length == 1){
                            sendCommands();
                        }
                        break;
                    case "@list":
                        if(message.length == 1){
                            sendList();
                        }
                        break;
                    case "@everyone":
                        System.out.println(this.username + " wants to comunicare with everyone");
                        if(message.length == 2){
                            kek = sendBroadcast(this.username + " sent everyone this message: " + message[1]);
                            if(!kek){
                                sendMessage("There are no users online");
                            }
                        }else{
                            sendMessage("This is not the correct wording of the command");
                        }
                        break;
                    case "@exit":
                        System.out.println(this.username + " wants to close the connection");
                        if(message.length == 1){
                            exit = true;
                            sendBroadcast("L'utente " + this.username + " left the chat");
                        }else{
                            sendMessage("This is not the correct wording of the command");
                        }
                        break;
                    case "@username":
                        System.out.println(this.username + " wants to change his/her username");
                        if(message.length == 2){
                            kek = setUsername(message[1]);
                            if(kek){
                                sendMessage("Your username has been successfully changed");
                            }else{
                                sendMessage("Your username already exists or is invalid");
                            }
                        }else{
                            sendMessage("This is not the correct wording of the command");
                        }
                        break;
                    default:
                        System.out.println(this.username + " wants to chat in private mode");
                        if(message.length == 2){
                            sendPrivate(message[0], message[1]);
                        }else{
                            sendMessage("This is not the correct wording of the command");
                        }
                }
            }
            App.peopleInChat.remove(this);
            sender.close();
        } catch (Exception e) {
            App.peopleInChat.remove(this);
            System.out.println(e.getMessage());
        }
    }
}