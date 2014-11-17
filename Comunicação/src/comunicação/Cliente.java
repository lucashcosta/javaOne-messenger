/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicação;

/**
 *
 * @author aluno
 */
import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Cliente extends JFrame {

    private final JTextField mensagem;
    private final JTextArea conversa;
    ObjectOutputStream saida;
    ObjectInputStream entrada;
    String recebido = "";
    Socket soquete;

    public Cliente() {
        super("Cliente");

        Container c = getContentPane();

        mensagem = new JTextField();
        mensagem.setEnabled(false);
        mensagem.addActionListener(
                new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        enviaDados(e.getActionCommand());
                    }
                });
        c.add(mensagem, BorderLayout.NORTH);

        conversa = new JTextArea();
        conversa.setEditable(false);
        c.add(new JScrollPane(conversa),
                BorderLayout.CENTER);

        setSize(300, 350);
        setLocation(300, 150);
        this.setVisible(true);
    }

    public void executaCliente() {
        try {
            conversa.setText("Tentando conectar.\n");
            soquete = new Socket(InetAddress.getByName("177.105.51.203"), 5000);
            conversa.append("Conectado a: " + soquete.getInetAddress().getHostName());
            saida = new ObjectOutputStream(soquete.getOutputStream());
            saida.flush();
            entrada = new ObjectInputStream(soquete.getInputStream());
            conversa.append("\nAguardando...\n");
            mensagem.setEnabled(true);
            do {
                try {
                    recebido = (String) entrada.readObject();
                    conversa.append("\n" + recebido);
                    conversa.setCaretPosition(conversa.getText().length());
                } catch (ClassNotFoundException cnfex) {
                    conversa.append("\nRecebido objeto de tipo desconhecido.");
                }
            } while (!recebido.equals("Servidor>>> FIM"));
            conversa.append("\nServidor encerrou a conexao.\n");
            saida.close();
            entrada.close();
            soquete.close();
        } catch (EOFException eof) {
            conversa.append("\nServidor encerrou a conexao\n");
        } catch (IOException e) {
            conversa.append("\nErro tentando conectar com Servidor\n");
        }
    }

    private void enviaDados(String s) {
        try {
            recebido = s;
            saida.writeObject("Cliente>>> " + s);
            saida.flush();
            conversa.append("\nCliente>>>" + s);
            conversa.append("\nPorta local: " + soquete.getLocalPort() + ". Porta remota:" + soquete.getPort());
        } catch (IOException cnfex) {
            conversa.append("\nErro enviando objeto");
        }
    }

    public static void main(String args[]) {
        Cliente app = new Cliente();
        app.setDefaultCloseOperation(EXIT_ON_CLOSE);
        app.executaCliente();
    }
}
