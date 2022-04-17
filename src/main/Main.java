package main;

import java.io.BufferedReader;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import paquete.ALU;

import paquete.MaquinaVirtual;

public class Main {
	
	public static void main(String[] args){
		
		
		try {
			MaquinaVirtual maquinavirtual = new MaquinaVirtual();
			
			String binFilename="fibonacci.mv1";
			FileInputStream arch = new FileInputStream(binFilename);
			DataInputStream entrada = new DataInputStream(arch);
			
			int inst,CS=0,i;
			
			//leo el header. ReadInt lee de a 4 bytes
			
			for (i=0;i<6;i++) { 
				inst=entrada.readInt();
				if (i==1) { //2do bloque. Tamaño del codigo
					CS=inst;   
				}
			}

			maquinavirtual.setDS(CS);
			
			//cargo a la RAM
			for (i=0;i<CS;i++) {
				inst=entrada.readInt();
				maquinavirtual.cargainstruccion(inst,i);
			}    
			arch.close();
			entrada.close();
								
		    //leo el codigo desde la RAM y lo ejecuto
		      do {
		      	inst=maquinavirtual.getInstruccion();	      	
		      	maquinavirtual.incrementaIP();
		    	maquinavirtual.ejecutaInstruccion(inst);
		    }while ((0<=maquinavirtual.getIP()) && (maquinavirtual.getIP() < maquinavirtual.getDS()));
		      
		
		}
		catch(IOException e) {
			
		}
		
	}
}

