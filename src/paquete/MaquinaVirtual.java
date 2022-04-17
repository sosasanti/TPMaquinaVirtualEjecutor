package paquete;

public class MaquinaVirtual {

	private Memoria memoria;
	private Registros registros;
	private ALU alu;
	
	public MaquinaVirtual() {
	
		this.memoria = new Memoria();
		this.registros = new Registros();
		this.alu = new ALU();
	
	}
	
	public void cargainstruccion(int instruccion,int i) {
		this.memoria.cargainstruccion(instruccion,i);
	}
	
	public int getInstruccion() {
		return this.memoria.getInstruccion(registros.getIP());
	}
	

	public void ejecutaInstruccion(int instruccion) {
		alu.ejecutaInstruccion(instruccion, this.memoria, this.registros);
//		if (alu.breakpointP()) {
//			alu.ejecutaInstruccion(instruccion, this.memoria, this.registros);
//			alu.sys(memoria, registros, instruccion);
//		}
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
	
	public int devuelveCelda(int numcelda) {
		return memoria.getValorRAM(numcelda);
	}
	

}