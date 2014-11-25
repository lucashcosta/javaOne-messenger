/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.components;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import static java.lang.Thread.MIN_PRIORITY;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author EvandroFBL
 */
public class Server{
    
    public String receivedMessage;
    public ServerSocket serverSocket;
    public Socket clientSocket = null;
    public boolean fNewMessage;
    
    public Server(){
        this.fNewMessage = false;
        runServer();
    }
    
    private void runServer(){
        try {
            //initialize server socket
            this.serverSocket = new ServerSocket(10007);
            if(this.clientSocket == null){
                //listen for conection
                this.clientSocket = this.serverSocket.accept();            
            }else{
                JOptionPane.showMessageDialog(null, "Cliente ocupado", "Erro ao conectar", MIN_PRIORITY);
            }   
                //listen message
                //Scanner in = new Scanner(this.clientSocket.getInputStream());
                //this.receivedMessage = in.nextLine();
                //this.fNewMessage = true;
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex, "erro", MIN_PRIORITY);
        }finally{
            this.fNewMessage = false;
        }
    }
    
}
