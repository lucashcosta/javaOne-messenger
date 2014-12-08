/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.components;

import java.awt.List;
import java.util.ArrayList;

/**
 *
 * @author EvandroFBL
 */

/**
* função: atualizar a lista de contatos e armazenar na lista de clientes online (nome, ip)
* funçao: vai fazer a chamada da interface de conversa conversa()
* função: verifica se o cliente que vai receber a chamada está ocupado
* 
*/
public class Core {
   
    private ArrayList<Users> online;
    
    public Core(){
        online = new ArrayList<Users>();
        online.add(new Users("João", "192.168.0.100"));
        online.add(new Users("Maria", "192.168.0.107"));
    }
    
    public ArrayList getList(){
        return this.online;
    }
}
