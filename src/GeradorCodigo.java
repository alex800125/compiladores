import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GeradorCodigo {

	private String codigoGerado = "";
	private int variableInMemory = 0;
	private List<Integer> variableAlloc = new ArrayList<Integer>();

	public void criarCodigo(String valor1, String valor2, String valor3) {

		codigoGerado = codigoGerado.concat(valor1 + " ").concat(valor2 + " ").concat(valor3 + "\r\n");

	}

	public void criarCodigo(String expressaoPosFix) {
		String[] aux = expressaoPosFix.split(" ");

		for (int a = 0; a < aux.length; a++) {

			debugCode();

			if (aux[a].contains("p")) {

				String[] value = aux[a].split("p");
				codigoGerado = codigoGerado.concat(Constantes.LDV + " ").concat(value[1]).concat("\r\n");

			} else if (aux[a].contains("funcao")) {

				String[] value = aux[a].split("funcao");
				codigoGerado = codigoGerado.concat(Constantes.CALL + " ").concat(Constantes.LABEL + value[1]).concat("\r\n");

			} else if (aux[a].equals(Constantes.MAIS_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.ADD).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOS_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.SUB).concat("\r\n");
			} else if (aux[a].equals(Constantes.MULTIPLICACAO_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.MULT).concat("\r\n");
			} else if (aux[a].equals(Constantes.DIVISAO_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.DIVI).concat("\r\n");
			} else if (aux[a].equals(Constantes.E_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.AND).concat("\r\n");
			} else if (aux[a].equals(Constantes.OU_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.OR).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOR_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CME).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIOR_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CMA).concat("\r\n");
			} else if (aux[a].equals(Constantes.IGUAL_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CEQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.DIFERENTE_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CDIF).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOR_IGUAL_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CMEQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIOR_IGUAL_SINAL)) {
				codigoGerado = codigoGerado.concat(Constantes.CMAQ).concat("\r\n");
			} else if (aux[a].equals(Constantes.MENOS_UNARIO)) {
				codigoGerado = codigoGerado.concat(Constantes.INV).concat("\r\n");
			} else if (aux[a].equals(Constantes.MAIS_UNARIO)) {
				// do nothing
			} else if (aux[a].equals(Constantes.NAO)) {
				codigoGerado = codigoGerado.concat(Constantes.NEG).concat("\r\n");
			} else {
				if (aux[a].equals(Constantes.VERDADEIRO)) {
					codigoGerado = codigoGerado.concat(Constantes.LDC).concat(" 1").concat("\r\n");
				} else if (aux[a].equals(Constantes.FALSO)) {
					codigoGerado = codigoGerado.concat(Constantes.LDC).concat(" 0").concat("\r\n");
				} else if (aux[a].equals("")) {
					// não faz nada
				} else {
					codigoGerado = codigoGerado.concat(Constantes.LDC + " ").concat(aux[a]).concat("\r\n");
				}
			}
		}
	}

	public void criarCodigo(String comando, int countVariavel) {
		if (Constantes.ALLOC.equals(comando)) {
			codigoGerado = codigoGerado.concat(comando + " ").concat(variableInMemory + " ").concat(countVariavel + "\r\n");
			variableInMemory = variableInMemory + countVariavel;
			variableAlloc.add(countVariavel);
		} else {

			if (countVariavel == 0) {
				codigoGerado = codigoGerado.concat(comando + "\r\n");
			} else {
				int posicao = variableAlloc.size() - 1;
				int countVariavelDalloc = variableAlloc.get(posicao);

				variableInMemory = variableInMemory - countVariavelDalloc;
				codigoGerado = codigoGerado.concat(comando + " ").concat(variableInMemory + " ").concat(countVariavelDalloc + "\r\n");
				variableAlloc.remove(posicao);
			}

		}
	}

	public void gerarArquivo() {
		try {
			File path = new File(MaquinaVirtual.NomeDoArquivoTXT.replace(".txt", "")+ " " +"CodigoGerado.txt");
			path.createNewFile();

			FileWriter arquivo = new FileWriter(path);
			arquivo.write(codigoGerado);
			arquivo.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void debugCode() {
		System.out.println("Code = " + this.codigoGerado);
	}

}