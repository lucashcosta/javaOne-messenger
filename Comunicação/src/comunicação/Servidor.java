/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicação;

/**
 *
 * @author aluno
 */
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class Servidor extends JFrame {

    private final JTextField mensagem;
    private final JTextArea conversa;
    //private final JButton envia;
    //private final JButton anexa;
    //private final JLabel nome;
    public ServerSocket soqueteServidor;
    public Socket soquete;
    public ObjectOutputStream saida;
    public ObjectInputStream entrada;

    public Servidor(String nome) {
        super("Servidor");
        Container c = getContentPane();
        //envia = new JButton();
        //anexa = new JButton();
        mensagem = new JTextField();
        mensagem.setEnabled(false);
        mensagem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        enviaDados(e.getActionCommand());
                    }
                });
        c.add(mensagem, BorderLayout.SOUTH);
        conversa = new JTextArea();
        conversa.setEditable(false);
        c.add(new JScrollPane(conversa), BorderLayout.CENTER);
        //this.nome = new JLabel();
        //this.nome.setText(nome);
        //c.add(this.nome);
        //c.add(envia);
        //c.add(anexa);
        setSize(300, 350);
        setLocation(100, 100);
        this.setVisible(true);
    }

    public void executaServidor() {

        int contador = 1;


        try {
            soqueteServidor = new ServerSocket(5000, 100);
            while (true) {
                conversa.setText("aguardando conexao na porta 5000\n");
                soquete = soqueteServidor.accept();
                conversa.append("Conexao " + contador + " recebida de: " + soquete.getInetAddress().getHostName());
                saida = new ObjectOutputStream(soquete.getOutputStream());
                saida.flush();
                entrada = new ObjectInputStream(soquete.getInputStream());
                conversa.append("\nAguardando...\n");
                String recebido = "Servidor>>>_Conexao Criada !";
                saida.writeObject(recebido);
                saida.flush();
                mensagem.setEnabled(true);

                do {
                    try {
                        recebido = (String) entrada.readObject();
                        conversa.append("\n" + recebido);
                        conversa.setCaretPosition(conversa.getText().length());
                    } catch (ClassNotFoundException cnfex) {
                        conversa.append("\nRecebido objeto e tipo desconhecido");
                    }
                } while (!recebido.equals("Cliente>>> FIM"));

                conversa.append("\nCliente encerrou a conexao.");
                mensagem.setEnabled(false);
                saida.close();
                entrada.close();
                soquete.close();
                ++contador;
            }
        } catch (EOFException eof) {
            conversa.append("\nCliente encerrou a conexao");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void enviaDados(String s) {
        try {
            saida.writeObject("Servidor>>> " + s);
            saida.flush();
            conversa.append("\nServidor>>>"+s);
            conversa.append("\nPorta_local: "+soquete.getLocalPort() + ". Porta Remota: "+ soquete.getPort());
        }catch(IOException cnfex){
            conversa.append("\nErro enviando objeto.");
        }
    }

    public static void main(String args[]) {
        Servidor app = new Servidor("jao");
        app.setDefaultCloseOperation(EXIT_ON_CLOSE);
        app.executaServidor();
    }
}
