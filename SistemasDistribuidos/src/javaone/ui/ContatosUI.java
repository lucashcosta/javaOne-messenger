/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.ui;

import javaone.components.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 *
 * @author EvandroFBL
 */
public class ContatosUI extends javax.swing.JFrame {

    /**
     * Creates new form ContatosUI
     */
    private SecretKey secretKey;
    private KeyGenerator keyGen;
    private Users ownUser;
    private Core core;
    
    public ContatosUI() {
        initComponents();
        try {
            keyGen = KeyGenerator.getInstance("DES");
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ContatosUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //função que vai inicializar o servidor primario
    public void executeRequestListener(){ 
        try {
            ServerSocket serverSocket = new ServerSocket(6000, 100);
            while (true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String control;
                Users usr = new Users();
                SecretKey sKey;
                try{
                    control = (String) in.readObject();
                    if (control.equals("1")){
                        //abrir conversa
                        out.writeObject("1.1");
                        out.flush();
                        try{
                            usr = (Users) in.readObject();
                            out.writeObject("1.2");
                            out.flush();
                            try {
                                sKey = (SecretKey) in.readObject();
                                out.writeObject("1.3");
                                out.flush();
                                ConversaUIServidor c = new ConversaUIServidor(usr.nome, sKey);
                                //retornar mensagem de controle confirmando
                                out.writeObject("1.4");
                                out.flush();
                                c.setVisible(true);
                            }catch (IOException | ClassNotFoundException e){
                                //colocar erro
                            }
                        }catch (IOException | ClassNotFoundException e){
                            //colocar erro
                        }
                    }else{
                        //retorna erro - talves nem seja necessário
                        out.writeObject("mensagem de controle incorreta!");
                        out.flush();
                    }
                }catch (IOException | ClassNotFoundException e){
                    
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ContatosUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //funçao que vai receber um ip e realizar um pedido de conversa
    public void makeConversationRequest(String ip, SecretKey sctKey){
        try {
            Socket clientSocket = new Socket(InetAddress.getByName(ip), 6000);
            ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
            out.flush();
            ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            String control;
            out.writeObject("1");
            out.flush();
            try{
                control = (String)in.readObject();
                if (control.equals("1.1")){
                    out.writeObject(this.getOwnUser());
                    out.flush();
                    try{
                        control = (String)in.readObject();
                        if (control.equals("1.2")){
                            out.writeObject(sctKey);
                            out.flush();
                            try{
                                control = (String)in.readObject();
                                if (control.equals("1.3")){
                                    //retornar algum feedback para o usuário talves?
                                    try{
                                        control = (String)in.readObject();
                                        if(control.equals("1.4")){
                                            //o outro usuario esta pronto, basta iniciar a interface para conversa
                                            ConversaUICliente c = new ConversaUICliente(this.getOwnUser().nome, sctKey);
                                            c.setVisible(true);
                                        }
                                    }catch(IOException | ClassNotFoundException e){
                                        //colocar erro
                                    }
                                }
                            }catch(IOException | ClassNotFoundException e){
                                //colocar erro
                            }
                        }
                    }catch(IOException | ClassNotFoundException e){
                        //colocar erro
                    }
                }
            }catch(IOException | ClassNotFoundException e){
                //colocar erro
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(ContatosUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ContatosUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jSeparator1 = new javax.swing.JSeparator();
        jLabelContatosDisponiveis = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButtonIniciarConversa = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jListContatos = new javax.swing.JList();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabelContatosDisponiveis.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabelContatosDisponiveis.setText("Contatos Disponíveis");

        jButtonIniciarConversa.setText("Iniciar Conversa");
        jButtonIniciarConversa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIniciarConversaActionPerformed(evt);
            }
        });

        jListContatos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jListContatos.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jListContatos.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jListContatos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(jListContatos);

        jMenu1.setText("Sistema Mensageiro");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Opções");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabelContatosDisponiveis)
                        .addComponent(jButtonIniciarConversa, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelContatosDisponiveis)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonIniciarConversa)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonIniciarConversaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIniciarConversaActionPerformed
        // TODO add your handling code here:
        ConversaUIServidor c = new ConversaUIServidor();
        c.setVisible(true);
    }//GEN-LAST:event_jButtonIniciarConversaActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ContatosUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ContatosUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ContatosUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ContatosUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ContatosUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonIniciarConversa;
    private javax.swing.JLabel jLabelContatosDisponiveis;
    private javax.swing.JList jListContatos;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

    public Users getOwnUser() {
        return this.ownUser;
    }
}
