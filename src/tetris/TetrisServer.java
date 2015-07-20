package tetris;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TetrisServer implements Runnable {
    
    public Socket client;
    private static ServerSocket server;
    public static int port;
    
    public TetrisServer(Socket client) {
        this.client = client;
    }
    
    public static void main ( String[] args ) throws IOException {  
        port = 7375;
        server = new ServerSocket(port);
        System.out.println("Servidor conectado.");
        System.out.println("Ouvindo a porta " + port);
        
        while(true) {
            Socket client = server.accept();
            TetrisServer jogo = new TetrisServer(client);
            Thread t = new Thread(jogo);
            t.start();
        }
    }
    
    @Override
    public void run() {
        System.out.println("Cliente conectado: " + client.getInetAddress());
        DataTransfer dto = DataTransfer.getInstance();
        int player = -1;
        System.out.println("Entrando na fila...");
        while(player == -1) player = dto.addPlayer();
        String msg;
        while(true) {
            try {
                DataInputStream inputStream;
                inputStream = new DataInputStream(client.getInputStream());
                msg = inputStream.readUTF();
                System.out.println("Mensagem Recebida: " + msg + " User: " + player);
                DataOutputStream outputStream = new DataOutputStream(client.getOutputStream());
                switch (msg) {
                    case "getLines":
                        msg = "" + dto.receiveLines(player);
                        outputStream.writeUTF(msg);
                        outputStream.flush();
                        break;
                    case "sendLines":
                        msg = "howMany";
                        outputStream.writeUTF(msg);
                        outputStream.flush();
                        String r = inputStream.readUTF();
                        int l = Integer.parseInt(r);
                        dto.sendLines(player, l);
                        break;
                    case "getState":
                        int status = dto.getState(player);
                        System.out.println("Status do player " + player + ": " + status);
                        msg = "" + dto.getState(player);
                        outputStream.writeUTF(msg);
                        outputStream.flush();
                        break;
                    case "lostGame":
                        dto.setState(player, DataTransfer.DEFEATED);
                        break;
                    /*
                    case "sendGrid":
                        break;
                    case "getGrid":
                        break;
                    */
                    case "close":
                        dto.freePlayer(player);
                        client.close();
                }               
            } catch (IOException ex) {
                //do nothing
            }
        }
    }
}







 /*
        String msg;
        
        while(true) {
            Socket sk = server.accept();
            try {
                DataInputStream inputStream = new DataInputStream(sk.getInputStream());
                msg = inputStream.readUTF();
                System.out.println("Mensagem Recebida: " + msg);
                String implodedString = new String(msg);
                String[] splited = implodedString.split(";");
                
                DataOutputStream outputStream = new DataOutputStream(sk.getOutputStream());
                
                switch (splited[1]) {
                    case "getLines":
                        
                        break;
                    case "sendLines":
                        
                        break;
                }
                
                
                msg = "E ae cliente, what’s going on?";
                outputStream.writeUTF(msg);
                outputStream.flush();   
            } catch (IOException ex) {
                //do nothing
            }
            sk.close();
        }
        */