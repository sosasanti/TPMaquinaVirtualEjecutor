package paquete;

import java.util.HashMap;

public class InstruccionDecodificada {


	private HashMap <String, Integer> instrucciondecod = new HashMap <String, Integer> ();
	
	public InstruccionDecodificada() {
	
		instrucciondecod.put("codoperacion",0);
		instrucciondecod.put("tipoOpA",0);
		instrucciondecod.put("tipoOpB",0);
		instrucciondecod.put("operandoA",0);
		instrucciondecod.put("operandoB",0);
	}
	
	public void set(String string,int valor) { //Permite modificar el valor de cualquier elemento 
		this.instrucciondecod.replace(string,valor);
	}


}

