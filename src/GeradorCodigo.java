import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigo {

	private String code = "";
	private int variableInMemory = 0;
	private List<Integer> variableAlloc = new ArrayList<Integer>();

	public void createCode(String value1, String value2, String value3) {

		code = code.concat(value1 + " ").concat(value2 + " ").concat(value3 + "\r\n");

	}

	public void createCode(String expressionPosFix) {
		String[] aux = expressionPosFix.split(" ");
		

		for (int a = 0; a < aux.length; a++) {
			
			debugCode();
			
			if (aux[a].contains("p")) {

				String[] value = aux[a].split("p");
				code = code.concat(Constantes.LDV + " ").concat(value[1]).concat("\r\n");

			} else if (aux[a].contains("funcao")) {

				String[] value = aux[a].split("funcao");
				code = code.concat(Constantes.CALL + " ").concat(Constantes.LABEL + value[1]).concat("\r\n");
				
			} else if (aux[a].equals(Constantes.MAIS_SINAL)) {
				code = code.concat(Constantes.ADD).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOS_SINAL)) {
				code = code.concat(Constantes.SUB).concat("\r\n");
			} else if (aux[a].equals(Constantes.MULTIPLICACAO_SINAL)) {
				code = code.concat(Constantes.MULT).concat("\r\n");
			} else if (aux[a].equals(Constantes.DIVISAO_SINAL)) {
				code = code.concat(Constantes.DIVI).concat("\r\n");
			} else if (aux[a].equals(Constantes.E_SINAL)) {
				code = code.concat(Constantes.AND).concat("\r\n");
			} else if (aux[a].equals(Constantes.OU_SINAL)) {
				code = code.concat(Constantes.OR).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOR_SINAL)) {
				code = code.concat(Constantes.CME).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIOR_SINAL)) {
				code = code.concat(Constantes.CMA).concat("\r\n");
			} else if (aux[a].equals(Constantes.IGUAL_SINAL)) {
				code = code.concat(Constantes.CEQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.DIFERENTE_SINAL)) {
				code = code.concat(Constantes.CDIF).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOR_IGUAL_SINAL)) {
				code = code.concat(Constantes.CMEQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIOR_IGUAL_SINAL)) {
				code = code.concat(Constantes.CMAQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOS_UNARIO)) {
				code = code.concat(Constantes.INV).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIS_UNARIO)) {
				// do nothing
			} else if (aux[a].equals(Constantes.NAO)) {
				code = code.concat(Constantes.NEG).concat("\r\n");
			} else {
				if(aux[a].equals(Constantes.VERDADEIRO)) {
					code = code.concat(Constantes.LDC).concat(" 1").concat("\r\n");
				} else if(aux[a].equals(Constantes.FALSO)) {
					code = code.concat(Constantes.LDC).concat(" 0").concat("\r\n");
				} else if(aux[a].equals("")){
					// não faz nada
				} else {
					code = code.concat(Constantes.LDC + " ").concat(aux[a]).concat("\r\n");
				}
			}
		}
	}

	public void createCode(String command, int countVariable) {
		if (Constantes.ALLOC.equals(command)) {
			code = code.concat(command + " ").concat(variableInMemory + " ").concat(countVariable + "\r\n");
			variableInMemory = variableInMemory + countVariable;
			variableAlloc.add(countVariable);
		}
		else {		
			
			if (countVariable == 0) {
				code = code.concat(command + "\r\n");
			} else {
				int position = variableAlloc.size() - 1;
				int countVariableToDalloc = variableAlloc.get(position);
				
				variableInMemory = variableInMemory - countVariableToDalloc;
				code = code.concat(command + " ").concat(variableInMemory + " ").concat(countVariableToDalloc + "\r\n");
				variableAlloc.remove(position);	
			}		
				
			
		}
	}
	
	public void createFile() {
		try {
			File directory = new File("code.txt");
			directory.createNewFile();

			FileWriter file = new FileWriter(directory);
			file.write(code);
			file.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void debugCode() {
		System.out.println("Code = " + this.code);
	}

}