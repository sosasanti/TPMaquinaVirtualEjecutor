package paquete;

public class MaquinaVirtual {

	private Memoria memoria;
	private Registros registros;
	private InstruccionDecodificada instrucciondecodificada;
	private ALU alu;
	
	public MaquinaVirtual() {
	
		this.memoria = new Memoria();
		this.registros = new Registros();
		this.alu = new ALU();
		this.instrucciondecodificada = new InstruccionDecodificada();
	
	}
	
	public void cargainstruccion(int instruccion,int i) {
		this.memoria.cargainstruccion(instruccion,i);
	}
	
	public int getInstruccion() {
		return this.memoria.getInstruccion(registros.getIP());
	}
	
	public void decodificarInstruccion() { //Aplico mascaras y guardo los valores en instrucciondecodificada
		
		int instruccion = this.getInstruccion(); //tomo instruccion en binario (int)
		String stringhexa = Integer.toHexString(instruccion); //la paso a string hexa
		int instruccionhexa = Integer.decode(stringhexa); //paso el string hexa a int hexa
		
		int codOp,tipoOpA=0,tipoOpB=0,operandoA=0,operandoB=0; //inicio en 0 por si no se actualiza (1 o 0 operandos)
		
		
		int auxcantop = (instruccionhexa>>28) & 0xF; //me quedo con los primeros 4 bits

		if (auxcantop==15) { //1 operandos o ninguno
			
			int aux=(instruccionhexa>>24) & 0xFF; //me quedo con los 8 primeros
			
			if (aux<=251) { // 1 operando
			    codOp= instruccionhexa>>24 & 0xFF;
			    tipoOpA = instruccionhexa >> 20 & 0xC;
			    operandoA = instruccionhexa & 0xFFFF;
					
			}
			else { //ningun operando
				codOp=aux;
			}
		}
		else { //2 operandos
		
		    tipoOpA= ((instruccionhexa>>24) & 0xC)>>2; // BIEN
			tipoOpB= (instruccionhexa>>24) & 0x03; //me quedo con el codopB   //BIEN
			codOp=auxcantop;
				
			int auxcorrimiento=(instruccionhexa>>16) & 0x00FF;
				
			//para concatenar hago un left shift para dejar espacio a los nuevos bits que ingresan
			operandoA = (auxcorrimiento<<4) | (instruccionhexa >>12) & 0xF; //BIEN
			
			operandoB = (instruccionhexa) & 0xFFF; //BIEN

		}
		//cargo los valores al hashtable instrucciondecodificada
		this.instrucciondecodificada.set("codoperacion", codOp);
		this.instrucciondecodificada.set("tipoOpA", tipoOpA);
		this.instrucciondecodificada.set("tipoOpB", tipoOpB);
		this.instrucciondecodificada.set("operandoA", operandoA);
		this.instrucciondecodificada.set("operandoB", operandoB);
	}
	
	

	
	public void ejecutaInstruccion(int instruccion) {
		alu.ejecutaInstruccion(instruccion, this.memoria, this.registros);
	}
	
	public void setDS(int DS) {
		this.registros.setDS(DS);
	}
	
	public int getDS() {
		return this.registros.getDS();
	}
	
	public int getIP() {
		return this.registros.getIP();
	}
	
	
	public void incrementaIP() {
		this.registros.incrementaIP();
	}
	
	public int getEAX() {
		return this.registros.getAX();
	}
	
	public void setIP(int valor) {
		this.registros.setIP(valor);
	}
	
	public void setregistroo(String nombre,int valor) {
		registros.setRegistro(nombre, valor);
	}
	
	public int devuelveCelda(int numcelda) {
		return memoria.getValorRAM(numcelda);
	}
	
	public boolean esNegativoo(int numero) {
		return this.alu.esNegativo(numero,0xFF);
	}
	

}