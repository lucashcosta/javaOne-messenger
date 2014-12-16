/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.ui;

import java.awt.HeadlessException;
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
import java.security.SecureRandom;
import java.util.ArrayList;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
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
    private SecretKeySpec sctKeySpec;
    private byte[] IV;
    private KeyGenerator keyGen;
    private Users ownUser;
    private String ownInet;
    private Core core;
    private DefaultListModel listModel;
    public  Pkct pkct;
    
    public ContatosUI() {
        initComponents();
        try {
            keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            byte[] key = keyGen.generateKey().getEncoded();
            sctKeySpec = new SecretKeySpec(key, "AES");
            
            SecureRandom random = new SecureRandom();
            IV = new byte[16];
            random.nextBytes(IV);
            this.pkct = new Pkct(IV);
            
        } catch (NoSuchAlgorithmException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao criar SCKS", "ERROR", MIN_PRIORITY);
        }
        core = new Core();
        initiateListOfContacts();  
        //inicializar ownInet com o ip da máquina
        try {
            this.ownInet = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            JOptionPane.showMessageDialog(null, "Erro ao pegar própio IP", "ERROR", MIN_PRIORITY);
        }
        //inicializar ownUser
        Users user = new Users();
        user.ip = this.ownInet;
        ArrayList<Users> list;
        list = core.getList();
        for (Users usr : list){
            if (usr.ip.equals(user.ip))
                user.nome = usr.nome;
        }
        setOwnUser(user);
        
    }
    
    //função que vai inicializar o servidor primario
    public void executeRequestListener(){ 
        try {
            ServerSocket serverSocket = new ServerSocket(6000);
            while (true){
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                String control;
                Users usr;
                SecretKeySpec sKeySpec;
                Pkct pk;
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
                                sKeySpec = (SecretKeySpec)in.readObject();
                                out.writeObject("1.3");
                                out.flush();
                                try{
                                    pk = (Pkct)in.readObject();
                                    out.writeObject("1.4");
                                    out.flush();
                                    try{
                                        ConversaUIServidor c = new ConversaUIServidor(usr.nome, sKeySpec, pk.cryp);
                                        c.setVisible(true);
                                        //retornar mensagem de controle confirmando
                                        out.writeObject("1.5");
                                        out.flush();
                                    }catch(Exception e){
                                        JOptionPane.showMessageDialog(null, "Falha ao abrir a janela de conversa", "ERROR", MIN_PRIORITY);
                                    }
                                }catch(IOException | ClassNotFoundException | HeadlessException e){
                                    JOptionPane.showMessageDialog(null, "1.3", "ERROR", MIN_PRIORITY);
                                }
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
                        JOptionPane.showMessageDialog(null, "mensagem de controle incorreta!", "ERROR", MIN_PRIORITY);
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
    public void makeConversationRequest(String ip, String name, SecretKeySpec sctKeySpec, Pkct pk){
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
                    Users u = this.getOwnUser();
                    out.reset();
                    out.writeObject(u);
                    out.flush();
                    try{
                        control = (String)in.readObject();
                        if (control.equals("1.2")){
                            out.writeObject(sctKeySpec);
                            out.flush();
                            try{
                                control = (String)in.readObject();
                                if (control.equals("1.3")){
                                    out.writeObject(pk);
                                    out.flush();
                                    try{
                                        control = (String)in.readObject();
                                        if (control.equals("1.4")){
                                            try{
                                                control = (String)in.readObject();
                                                if(control.equals("1.5")){
                                                    //o outro usuario esta pronto, basta iniciar a interface para conversa
                                                    try{
                                                        ConversaUICliente c = new ConversaUICliente(this.getOwnUser().nome, name, ip, sctKeySpec, pk.cryp);
                                                        c.setVisible(true);
                                                        //c.executeClient();
                                                    }catch(Exception e){
                                                        JOptionPane.showMessageDialog(null, "Falha ao abrir a janela de conversa", "ERROR", MIN_PRIORITY);
                                                    }  
                                                }
                                            }catch(IOException | ClassNotFoundException e){
                                                //colocar erro
                                                JOptionPane.showMessageDialog(null, "1.4", "ERROR", MIN_PRIORITY);
                                            }
                                        }
                                    }catch(IOException | ClassNotFoundException | HeadlessException e){
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        String name = list.get(index).nome;
        if (!ip.equals(this.ownInet)){
            makeConversationRequest(ip, name, this.sctKeySpec, this.pkct);
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
            }
        });
        app.executeRequestListener();
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
    
    private void setOwnUser(Users usr){
        this.ownUser = usr;
    }
    
    public void testaUser(){
        Users user;
        user = this.getOwnUser();
        JOptionPane.showMessageDialog(null, user.nome, "System", MIN_PRIORITY);
    }
}
