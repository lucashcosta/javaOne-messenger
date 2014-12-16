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
import static java.lang.Thread.MIN_PRIORITY;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

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
    private String ownInet;
    private Core core;
    private DefaultListModel listModel;
    
    public ContatosUI() {
        initComponents();
        try {
            keyGen = KeyGenerator.getInstance("DES");
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar SCK", "ERROR", MIN_PRIORITY);
        }
        core = new Core();
        initiateListOfContacts();
        try {
            this.ownInet = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao pegar própio IP", "ERROR", MIN_PRIORITY);
        }
        
    }
    
    //função que vai inicializar o servidor primario
    private void executeRequestListener(){ 
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            while (true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String control;
                Users usr;
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
                                try{
                                    /*final ConversaUIServidor c = new ConversaUIServidor(usr.nome, sKey);
                                    java.awt.EventQueue.invokeLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            c.setVisible(true);
                                        }
                                    });*/
                                    ConversaUIServidor c = new ConversaUIServidor(usr.nome, sKey);
                                    c.setVisible(true);
                                }catch(Exception e){
                                    JOptionPane.showMessageDialog(null, "Falha ao abrir a janela de conversa", "ERROR", MIN_PRIORITY);
                                }
                                //retornar mensagem de controle confirmando
                                JOptionPane.showMessageDialog(null, "Aguarde... 1.3", "System", MIN_PRIORITY);
                                out.writeObject("1.4");
                                out.flush();
                                JOptionPane.showMessageDialog(null, "Aguarde... 1.4", "System", MIN_PRIORITY);
                            }catch (IOException | ClassNotFoundException e){
                                //colocar erro
                                JOptionPane.showMessageDialog(null, "1.2", "ERROR", MIN_PRIORITY);
                            }
                        }catch (IOException | ClassNotFoundException e){
                            //colocar erro
                            JOptionPane.showMessageDialog(null, "1.1", "ERROR", MIN_PRIORITY);
                        }
                    }else{
                        //retorna erro - talves nem seja necessário
                        out.writeObject("mensagem de controle incorreta!");
                        out.flush();
                    }
                }catch (IOException | ClassNotFoundException e){
                    JOptionPane.showMessageDialog(null, "1", "ERROR", MIN_PRIORITY);
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao iniciar servidor", "ERROR", MIN_PRIORITY);
        }
    }
    
    //funçao que vai receber um ip e realizar um pedido de conversa
    private void makeConversationRequest(String ip, SecretKey sctKey){
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
                                    JOptionPane.showMessageDialog(null, "Aguarde...", "System", MIN_PRIORITY);
                                    try{
                                        control = (String)in.readObject();
                                        if(control.equals("1.4")){
                                            //o outro usuario esta pronto, basta iniciar a interface para conversa
                                            try{
                                                /*final ConversaUICliente c = new ConversaUICliente(this.getOwnUser().nome, this.secretKey);
                                                java.awt.EventQueue.invokeLater(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        c.setVisible(true);
                                                    }
                                                });*/
                                                ConversaUICliente c = new ConversaUICliente(this.getOwnUser().nome, sctKey);
                                                c.setVisible(true);
                                            }catch(Exception e){
                                                JOptionPane.showMessageDialog(null, "Falha ao abrir a janela de conversa", "ERROR", MIN_PRIORITY);
                                            }
                                            
                                            
                                        }
                                    }catch(IOException | ClassNotFoundException e){
                                        //colocar erro
                                        JOptionPane.showMessageDialog(null, "1.3", "ERROR", MIN_PRIORITY);
                                    }
                                }
                            }catch(IOException | ClassNotFoundException e){
                                //colocar erro
                                JOptionPane.showMessageDialog(null, "1.2", "ERROR", MIN_PRIORITY);
                            }
                        }
                    }catch(IOException | ClassNotFoundException e){
                        //colocar erro
                        JOptionPane.showMessageDialog(null, "1.1", "ERROR", MIN_PRIORITY);
                    }
                }
            }catch(IOException | ClassNotFoundException e){
                //colocar erro
                JOptionPane.showMessageDialog(null, "1", "ERROR", MIN_PRIORITY);
            }
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao realizar pedido", "ERROR", MIN_PRIORITY);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao realizar pedido", "ERROR", MIN_PRIORITY);
        }
    }
    
    private void initiateListOfContacts(){
        listModel = new DefaultListModel();
        int index = 0;
        ArrayList<Users> list;
        list = core.getList();
        for (Users usr : list){
            listModel.add(index, usr.nome);
            index++;
        }
        jListContatos.setModel(listModel);
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
        jButtonIniciarConversa.setEnabled(false);
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
        jListContatos.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                jListContatosValueChanged(evt);
            }
        });
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
        int index = jListContatos.getSelectedIndex();
        ArrayList<Users> list;
        list = core.getList();
        String ip = list.get(index).ip;
        if (!ip.equals(this.ownInet)){
            makeConversationRequest(ip, this.secretKey);
        }else{
            //retornar mensagem de erro avisando q está tentando conversas com ele propio
            JOptionPane.showMessageDialog(null, "Voce não pode iniciar uma conversa com voce mesmo", "WARNING", MIN_PRIORITY);
        }

        jListContatos.setSelectedIndex(index);
        jListContatos.ensureIndexIsVisible(index);
    }//GEN-LAST:event_jButtonIniciarConversaActionPerformed

    private void jListContatosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_jListContatosValueChanged
        // TODO add your handling code here:
        if (evt.getValueIsAdjusting() == false) {
            if (jListContatos.getSelectedIndex() == -1) {
                jButtonIniciarConversa.setEnabled(false);
            } else {
                jButtonIniciarConversa.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jListContatosValueChanged

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

        final ContatosUI app = new ContatosUI();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                app.setVisible(true);
                app.executeRequestListener();
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
