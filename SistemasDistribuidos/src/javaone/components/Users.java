/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaone.components;

import java.io.Serializable;

/**
 *
 * @author EvandroFBL
 */
public class Users implements Serializable {
    
    public int id;
    public String nome;
    public String login;
    public String senha;
    public String ip;
    
    public Users(){
        
    }
    
    public Users(String nome, String ip){
        this.nome = nome;
        this.ip = ip;
    }
}
