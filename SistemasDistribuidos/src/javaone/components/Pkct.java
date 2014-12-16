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
public class Pkct implements Serializable{
    
    public byte[] cryp;
    
    public Pkct(byte[] pk){
        this.cryp = pk;
    }
    
}
