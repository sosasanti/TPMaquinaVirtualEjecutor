package paquete;

import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Scanner;

public class ALU {
	
	private final int mask0 = 0xFFFFFFFF;
	private final int maskf = 0x0000000F;
		
	public void mov(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){ //parametros en decimal

		int b = valor2(memoria,reg,topB,vopB), copia = vopA, accesoA = copia>>4;
		int maskAccRegistro = accesoRegistro(accesoA);
		int nuevovalor,numreg;
		if (topA == 1) { //Registro		
			if (accesoA == 2) {//modifico valor registro (caso 3byte)		
				numreg=vopA & maskf;
				nuevovalor=(reg.getReg(numreg)&~maskAccRegistro)| (b<<8 & maskAccRegistro);
				reg.modificaReg(numreg,nuevovalor);
			}
			else {
				numreg=vopA & maskf;
				nuevovalor=((reg.getReg(vopA & maskf)& ~maskAccRegistro))| (b & maskAccRegistro);
				reg.modificaReg(numreg, nuevovalor);

			}
		}
		else{
			memoria.modificaRAM(vopA + reg.getDS(),b);
		}
	}

	public void add(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf)| ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor + b;
			reg.modificaReg(vopA & maskf,(((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0));
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS()) + b;
			memoria.modificaRAM(vopA + reg.getDS(),resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void sub(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor - b;
			reg.modificaReg(vopA & maskf, (((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0));
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())- b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void mul(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			if (accesoA == 2) b = b<<8;
			resultado = valor * b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);

		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS()) * b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void div(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA,accesoA = copia>>4, maskAccRegistro = accesoRegistro(accesoA),resultado;
		if (b!=0) {	
			if (topA == 1) {  	//Registro
				if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
					valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
				else 
					valor = reg.getReg(vopA & maskf) & maskAccRegistro;
				if (accesoA == 2) b = b<<8;
				resultado = valor / b;
				reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
			}
			else{
				resultado = memoria.getValorRAM(vopA + reg.getDS()) / b;
				memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
			}
			this.seteaCC(reg, resultado);
			reg.setRegistro("AC",resultado % b);
		}
	}
	

	public void swap(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int copiaA = vopA,copiaB = vopB,accesoA = copiaA>>4, maskAccRegistroA = accesoRegistro(accesoA),aux,accesoB = copiaB>>4,maskAccRegistroB = accesoRegistro(accesoB);
		if (topA == 1) {  	//Registro
				if (topB == 1){
					aux= (reg.getReg(vopA & maskf) & maskAccRegistroA);
					if (accesoB == 2)
						reg.modificaReg(vopA & maskf, ((reg.getReg(vopB & maskf) & maskAccRegistroB)>>8 & maskAccRegistroA));
					else {
						reg.modificaReg(vopA & maskf, (reg.getReg(vopB & maskf) & maskAccRegistroB) & maskAccRegistroA);
						reg.modificaReg(vopB & maskf, aux & maskAccRegistroB);
					}

				}
				else {
					aux= memoria.getValorRAM(vopB + reg.getDS());
					memoria.modificaRAM(vopB + reg.getDS(), reg.getReg(vopA & maskf) & maskAccRegistroA);
					reg.modificaReg(vopA & maskf, aux);
				}
			}
			else{
				if (topB == 1){	
					aux=reg.getReg(vopB & maskf) & maskAccRegistroB;
					reg.modificaReg(vopB & maskf, memoria.getValorRAM(vopA + reg.getDS()));
					memoria.modificaRAM( vopA + reg.getDS(),aux);
				}
				else {
					aux=memoria.getValorRAM(vopB + reg.getDS());
					memoria.modificaRAM(vopB+ reg.getDS(), memoria.getValorRAM(vopA+ reg.getDS())); //VOPA O VOPA + REG.GETDS()??
					memoria.modificaRAM(vopA+ reg.getDS(), aux);

				}
			}
	}
	
	public void cmp(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (b!=0) {	
			if (topA == 1) {  	//Registro
				if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
					valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
				else 
					valor = reg.getReg(vopA & maskf) & maskAccRegistro;
				resultado = valor - b;
			}
			else
				resultado=memoria.getValorRAM(vopA + reg.getDS())-b;
			this.seteaCC(reg, resultado);
		}
	}
	
	public void and(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor & b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			
			resultado =memoria.getValorRAM(vopA + reg.getDS())+b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void or(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor | b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado=memoria.getValorRAM(vopA + reg.getDS())|b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void xor(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor ^ b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())^ b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void shl(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor << b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro)) & mask0);
		}
		else{
			resultado = memoria.getValorRAM(vopA + reg.getDS())<< b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void shr(Memoria memoria, Registros reg, int topA, int topB, int vopA, int vopB){
		int b = valor2(memoria,reg,topB,vopB),valor, copia = vopA, maskAccRegistro = accesoRegistro(copia>>4),resultado;
		if (topA == 1) {  	//Registro
			if (esNegativo(reg.getReg(vopA & maskf),maskAccRegistro)) 
				valor = reg.getReg(vopA & maskf) | ~maskAccRegistro;
			else 
				valor = reg.getReg(vopA & maskf) & maskAccRegistro;
			resultado = valor >> b;
			reg.modificaReg(vopA & maskf, ((reg.getReg(vopA & maskf) & ~maskAccRegistro) | ((resultado) & maskAccRegistro) & mask0));
		}
		else{
			resultado=memoria.getValorRAM(vopA + reg.getDS()) >> b;
			memoria.modificaRAM(vopA + reg.getDS(), resultado & mask0);
		}
		this.seteaCC(reg, resultado);
	}
	
	public void sys (Memoria memoria,Registros reg,int vopA) {
		String cad=null;
		boolean prompt = true, saltoLinea = true, caracter = false;
		int a,comienzo, cantidad,salida,aux;
		comienzo = reg.getEDX();
		cantidad = reg.getCX();
		if (vopA == 0x1) {  //lectura
			if (reg.getAX()>>11 == 1)
				prompt = false;
			if (((reg.getAX()>>8) & 0X00000003) == 1)
				saltoLinea = false;	
			switch (reg.getAX() &  maskf ) {
					case 8 : cad = "%X "; break;
					case 4 : cad = "%o "; break;
					case 1 : cad = "%d "; break;
			}
			Scanner leer = new Scanner(System.in);
			if (saltoLinea) {
				aux = leer.nextInt(); 
				for (a = comienzo;a<(comienzo+cantidad);a++) {
						
					// FALTA COMO LEER CUANDO INGRESA LA PALABRA ENTERA
					// esto dice la especificacion
					// Interpreta el contenido luego del endline <Enter> según la especificación de
					// los 4 bits menos significativos de este formato (Hexa, Octal, Decimal).
				}
			}	
			else {
				for (a = comienzo;a<(comienzo+cantidad);a++) {		
					if (prompt)
						System.out.print("["+a+"]");
					memoria.modificaRAM(a, leer.nextInt());
					//memoria.setRam(memoria.getRam());
				}
			}
		}
		else { 			//escritura
			if (vopA == 0x2) {
				if (reg.getAX()>>11 == 1)
					prompt = false;
				if (((reg.getAX()>>8) & 0X00000003) == 1)
					saltoLinea = false;
				if (reg.getAX()>>4 == 1){
					cad = "%c ";
					caracter = true;
				}
				else {
					switch (reg.getAX() &  maskf ) {
						case 8 : cad = "%X "; break;
						case 4 : cad = "%o "; break;
						case 1 : cad = "%d "; break;
					}
				}
				for (a = comienzo;a<(comienzo+cantidad);a++) {		
					if (prompt)
						System.out.print("["+a+"]");
					if (caracter) {
						
						salida = memoria.getValorRAM(a) & 0X000000FF;
						if (((salida>=0) && (salida<=31)) || (salida ==127)) {
							salida = 46;
						}
					}
					else
						salida = memoria.getValorRAM(a);
					System.out.format(cad,memoria.getValorRAM(a));
					if (saltoLinea)
						System.out.format("\n");
				}
			}
			else {    //breakpoint
				
			}
		}
	}
	
	
	public void jmp (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (topA == 1 ) {
			if ( aRegistroA == 2) 
				valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
			else
				valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
		}
		else {
			if (topA == 2 )    //directo 
				valor = memoria.getValorRAM(vopA);
			else
				valor = vopA;
		}
		reg.setIP(valor);
	}
	
	public void jz (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.ceroCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jp (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 0) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jn (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jnn (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 0 || reg.ceroCC()==1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}
	
	public void jnz (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.ceroCC() == 0) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}	
	
	public void jnp (Memoria memoria,Registros reg,int topA,int vopA) { 
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
		if (reg.signoCC() == 1 || reg.ceroCC() == 1) {
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor = memoria.getValorRAM(vopA);
				else
					valor = vopA;
			}
			reg.setIP(valor);
		}
	}

	public void ldh (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aRegistroA = (vopA>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo
					valor = memoria.getValorRAM(vopA);         //SOLO VOPA ?????
				else
					valor = vopA;
			}
			reg.setAC(((valor & 0X0000FFFF)<<16)|((reg.getAC() & 0X0000FFFF)));
	}

	public void ldl (Memoria memoria,Registros reg,int topA,int vopA) {
		int valor;
		int aux = vopA;
		int aRegistroA = (aux>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2) 
					valor = ((reg.getReg(vopA & maskf) & maskAccRegistro)>>8);
				else
					valor = (reg.getReg(vopA & maskf) & maskAccRegistro);
			}
			else {
				if (topA == 2 )    //directo 
					valor =memoria.getValorRAM(vopA + reg.getDS());

				else
					valor = vopA;
			}
			reg.setAC((valor & 0X0000FFFF)|(reg.getAC() & 0XFFFF0000));
	}
	
	public void not (Memoria memoria,Registros reg,int topA,int vopA) {
		int aux = vopA;
		int aRegistroA = (aux>>4);
		int maskAccRegistro = accesoRegistro(aRegistroA);
			if (topA == 1 ) {
				if ( aRegistroA == 2)
					reg.modificaReg(vopA & maskf, ~((reg.getReg(vopA & maskf) & maskAccRegistro)>>8));
				else
					reg.modificaReg(vopA & maskf, ~(reg.getReg(vopA & maskf) & maskAccRegistro));
			}
			else  
				memoria.modificaRAM(vopA + reg.getDS(),~ memoria.getValorRAM(vopA + reg.getDS()));
			
	}
	
	public void stop() {
		System.exit(0);
	}
	
	
public void ejecutaInstruccion(int a, Memoria memoria, Registros registros) {
	int vopA,vopB,topA,topB;
	if ((a>>28 & maskf)!= maskf){
		vopA = (a>>12) & 0x00000fff;
		vopB = a & 0x00000fff;
		topA = (a & 0X0C000000)>>26;
		topB = (a & 0X03000000)>>24;
		System.out.println("numoperacion "+ (a>>28 & maskf)+"    topA " + topA  +" topB "+topB+" opA "+vopA+" opB "+vopB);   
        switch (a>>28 & maskf){
            case 0: mov(memoria,registros,topA, topB, vopA, vopB); break;
            case 1: add(memoria,registros,topA, topB, vopA, vopB);break;
            case 2: sub(memoria,registros,topA, topB, vopA, vopB);break;
            case 3: swap(memoria,registros,topA, topB, vopA, vopB);break;
            case 4: mul(memoria,registros,topA, topB, vopA, vopB);break;
            case 5: div(memoria,registros,topA, topB, vopA, vopB);break;
            case 6: cmp(memoria,registros,topA, topB, vopA, vopB);break;
            case 7: shl(memoria,registros,topA, topB, vopA, vopB);break;
            case 8: shr(memoria,registros,topA, topB, vopA, vopB);break;
            case 9:and(memoria,registros,topA, topB, vopA, vopB);break;
            case 10:or(memoria,registros,topA, topB, vopA, vopB);break;
            case 11:xor(memoria,registros,topA, topB, vopA, vopB);break;
        }
    }
    else{
        if (((a>>24 & maskf) == maskf) && ((a>>28 & maskf) == maskf)) {
        	System.out.println("STOP\n");
        	stop();
        }
        else{
      	   topA = (a & 0X00C00000)>>22;
      	   vopA = a & 0X0000ffff;
      	   //System.out.println("topa :  "+topA+" vopA  "+vopA);
            switch (a>>24 & maskf){
                case 0:System.out.println("SYS\n");sys(memoria,registros,vopA);break;
                case 1:System.out.println("JMP\n");jmp(memoria,registros,topA,vopA);break;
                case 2:System.out.println("JZ\n");jz(memoria,registros,topA,vopA);break;
                case 3:System.out.println("JP\n");jp(memoria,registros,topA,vopA);break;
                case 4:System.out.println("JN\n");jn(memoria,registros,topA,vopA);break;
                case 5:System.out.println("JNZ\n");jnz(memoria,registros,topA,vopA);break;
                case 6:System.out.println("JNP\n");jnp(memoria,registros,topA,vopA);break;
                case 7:System.out.println("JNN\n");jnn(memoria,registros,topA,vopA);break;
                case 8:System.out.println("LDL\n");ldl(memoria,registros,topA,vopA);break;
                case 9:System.out.println("LDH\n");ldh(memoria,registros,topA,vopA);break;
                case 11:System.out.println("NOT\n");not(memoria,registros,topA,vopA);break;
            }   
        }
    }
}

	public int valor2 (Memoria memoria,Registros reg, int topB,int vopB) {
		int valor;
		int copiaB = vopB;
		if (topB == 1){      //Registro
			int aRegistroB = (copiaB>>4);
			int mask1 = accesoRegistro(aRegistroB);
			if (aRegistroB == 2) {
				valor = (reg.getReg(vopB & maskf) & mask1)>>8;
			}
			else
				valor = reg.getReg(vopB & maskf) & mask1;
			
			if (esNegativo(valor,mask1)) {
				//System.out.println("Es negativo");
				valor = valor | ~mask1;
			}
		}
		else
			if (topB == 2){ // Directo
				//System.out.println(reg.getDs());
				valor=memoria.getValorRAM(vopB + reg.getDS()); //obtengo direccion de memoria
			}
			else{
				if (copiaB>>11 == 1) {
					valor = vopB | 0xFFFFF000;
				}
				else
				valor = vopB;
			}
		//System.out.println(valor);
		//System.out.format("\n %02X ",valor);
		return valor;
	}

	
	int  accesoRegistro (int a) {
		int mask=0;
		switch (a) {
		case 0: mask = 0xFFFFFFFF;break;
		case 1: mask = 0x000000FF;break;
		case 2: mask = 0x0000FF00;break;
		case 3: mask = 0x0000FFFF;break;
		}
		return mask; 
	}
	

	public int bitMasSignificativo(int mask) {
		int aux=0;
		switch (mask) {
		case 0xFFFFFFFF: aux = 31;break;
		case 0x000000FF: aux = 7;break;
		case 0x0000FF00: aux = 7;break;
		case 0x0000FFFF: aux = 15;break;
		}
		return aux; 
	}
	
	public boolean esNegativo(int valor, int mask) {
		return (((valor >>(bitMasSignificativo(mask)))& 0x1) == 1);
	}
	
	public void seteaCC (Registros reg,int resultado) {
		if (resultado == 0) 
			reg.setMenosSignificativoCC(1);
		else 
			reg.setMenosSignificativoCC(0);
		if (resultado < 0) 
			reg.setMasSignificativoCC(1);
		else
			reg.setMasSignificativoCC(0);
	}
}
