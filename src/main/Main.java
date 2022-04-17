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
				System.out.println(String.format("%x", inst));

				if (i==1) { //2do bloque. Tamaño del codigo
					CS=inst;   
				}
			}

			maquinavirtual.setDS(CS);
			
			//cargo a la RAM
			for (i=0;i<CS;i++) {
				inst=entrada.readInt();
				maquinavirtual.cargainstruccion(inst,i);
				//System.out.println(inst);
			}    
			arch.close();
			entrada.close();
			
//			while (entrada.available()>0) {
//				inst=entrada.readInt();
//				maquinavirtual.cargainstruccion(inst,i);
//				System.out.println(inst);
//			}    
//			
//			
			if (maquinavirtual.esNegativoo(0b11110000)) {
				System.out.println("true");
			}
			else
				System.out.println("false");
				
		    //leo el codigo desde la RAM y lo ejecuto
		      do {
		      	inst=maquinavirtual.getInstruccion();	      	
		      	maquinavirtual.incrementaIP();
		    	//maquinavirtual.decodificarInstruccion();
		    	System.out.println("IP "+maquinavirtual.getIP()+"    inst hexa: "+String.format("%x", inst));
		    	if (maquinavirtual.getIP()==2)
		    		inst=0x8014001;
		    	maquinavirtual.ejecutaInstruccion(inst);
		    	System.out.println("celda 10 NUM FIBO: "+ maquinavirtual.devuelveCelda(22));
		    	System.out.println("celda 20  "+ maquinavirtual.devuelveCelda(32));
		    	System.out.println();
		    }while ((0<=maquinavirtual.getIP()) && (maquinavirtual.getIP() < maquinavirtual.getDS()));
		      
		
		}
		catch(IOException e) {
			
		}
		
	}
}

