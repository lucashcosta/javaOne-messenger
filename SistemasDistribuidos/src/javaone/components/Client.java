/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.components;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author EvandroFBL
 */
public class Client{
    
    public Socket socket;
    public String serverIp;
    public String Message;
    
    public Client(String serverIp, String message){
        this.serverIp = serverIp;
        this.Message = message;
        runClient();
    }
    
    private void runClient(){
        try {
            //initialize socket with server ip
            this.socket = new Socket(this.serverIp , 10007);
            
            //send message to server
            //PrintWriter out = new PrintWriter(this.socket.getOutputStream());
            //out.println(this.Message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
