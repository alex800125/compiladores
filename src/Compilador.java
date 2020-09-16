import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Compilador extends MaquinaVirtual {
	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	protected Vector<String> MErro = new Vector<String>();

	BufferedReader br;
	String strLine = null;
	boolean erroDetectado = false;
	int nCaracter = -1;

	void AnalisadorEntrada() {
		System.out.println("new Compilador");

		ArrayList<String> linha = new ArrayList();

		JFileChooser fileChooser = new JFileChooser();

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {

			File selectedFile = fileChooser.getSelectedFile();

			try {
				FileInputStream fstream = new FileInputStream(selectedFile);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));

				// roda o arquivo inteiro fazendo a analise lexica
				while ((strLine = br.readLine()) != null) {
					while (strLine.length() > nCaracter) {
						if (!strLine.equals("")) // linha vazia
						{
							nCaracter++;
							char caracter = strLine.charAt(nCaracter);

							System.out.println("nCaracter = " + nCaracter);
							System.out.println("caracter = " + caracter);

							caracter = trataComentariosConsomeEspaco(caracter);

							if (erroDetectado) {
								break;
							} else {
								pegaToken(caracter);
								if (erroDetectado) {
									break;
								}
							}
						}
					}
				}
			} catch (Exception e1) {
				System.err.println("Error: " + e1.getMessage());
				System.err.println(nCaracter);
				System.err.println(strLine);
			}

			if (erroDetectado) {
				System.out.println("Erro na linha: " + nCaracter);
				TabelaLexema();
			} else {
				TabelaLexema();
			}
		}

	}

	char trataComentariosConsomeEspaco(char caracter) {
		while (caracter == '{' || caracter == ' ' || caracter == '/') {
			if (caracter == '{') {
				while (caracter != '}') {
					nCaracter++;
					if (nCaracter >= strLine.length()) {
						erroDetectado = true;
						break;
					}
					caracter = strLine.charAt(nCaracter);
				}
				if (caracter == '}') {
					nCaracter++;
					if (nCaracter >= strLine.length()) {
						return caracter;
					}
					caracter = strLine.charAt(nCaracter);
				}
			}

			if (caracter == ' ') {
				nCaracter++;
				if (nCaracter >= strLine.length()) {
					return caracter;
				}
				caracter = strLine.charAt(nCaracter);
			}

			if (caracter == '/') {
				nCaracter++;
				if (nCaracter >= strLine.length()) {
					erroDetectado = true;
					break;
				}
				caracter = strLine.charAt(nCaracter);

				if (caracter == '/') {
					try {
						strLine = br.readLine();
						nCaracter = 0;
						return strLine.charAt(nCaracter);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (caracter == '*') {
					while (true) {
						nCaracter++;
						if (nCaracter >= strLine.length()) {
							erroDetectado = true;
							return ' ';
						}
						caracter = strLine.charAt(nCaracter);

						if (caracter == '*') {
							nCaracter++;
							if (nCaracter >= strLine.length()) {
								erroDetectado = true;
								break;
							}
							caracter = strLine.charAt(nCaracter);

							if (caracter == '/') {
								nCaracter++;
								if (nCaracter >= strLine.length()) {
									try {
										strLine = br.readLine();
										nCaracter = 0;
										return strLine.charAt(nCaracter);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								return strLine.charAt(nCaracter);
							}
						}
					}
				}
			}
		}
		return caracter;
	}

	private final void pegaToken(char caracter) {

		if (Character.isDigit(caracter)) {
			trataDigito(caracter);
		} else if (Character.isLetter(caracter)) {
			trataIdentificadorPalavraReservada(caracter);
		} else if (caracter == ':') {
			trataAtribuicao(caracter);
		} else if (caracter == '+' || caracter == '-' || caracter == '*') {
			trataOperadorAritmetico(caracter);
		} else if (caracter == '<' || caracter == '>' || caracter == '=' || caracter == '!') {
			trataOperadorRelacional(caracter);
		} else if (caracter == ';' || caracter == ',' || caracter == '(' || caracter == ')' || caracter == '.') {
			trataPontuacao(caracter);
		} else {
			erroDetectado = true;
			nCaracter = strLine.length();
		}
	}

	void trataDigito(char caracter) {
		String numero;
		char proximoCaracter;

		numero = Character.toString(caracter);

		if (nCaracter < strLine.length()) {
			proximoCaracter = strLine.charAt(nCaracter);
			while (Character.isDigit(proximoCaracter) && ((nCaracter + 1) < strLine.length())
					&& !Character.isWhitespace(proximoCaracter)) {
				nCaracter++;
				proximoCaracter = strLine.charAt(nCaracter);
				if (Character.isDigit(proximoCaracter)) {
					numero = numero + Character.toString(proximoCaracter);
				}
			}
			if (!(Character.isDigit(proximoCaracter))) {
				nCaracter--;
			}

		}
		adicionaToken(numero, "snumero");
	}

	void trataIdentificadorPalavraReservada(char caracter) {
		String palavra;
		char proximoCaracter;

		palavra = Character.toString(caracter);

		if (nCaracter < strLine.length()) {
			proximoCaracter = strLine.charAt(nCaracter);
			if (Character.isLetter(proximoCaracter)) {
				while ((Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
						|| proximoCaracter == '_') && (nCaracter + 1) < strLine.length()
						&& !Character.isWhitespace(proximoCaracter)) {
					nCaracter++;
					proximoCaracter = strLine.charAt(nCaracter);
					if ((Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
							|| proximoCaracter == '_')) {
						palavra = palavra + Character.toString(proximoCaracter);
					}
				}
			}

			if (!(Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
					|| proximoCaracter == '_')) {
				nCaracter--;
			}

		}

		analisador(palavra);

	}

	void trataAtribuicao(char caracter) {

		if ((nCaracter + 1) < strLine.length()) {
			nCaracter = nCaracter + 1;
			char simbolo = strLine.charAt(nCaracter);

			if (simbolo == '=') {
				adicionaToken(":=", "satribuicao");
			} else {
				nCaracter--;
				adicionaToken(":", "sdoispontos");
			}
		}
	}

	void trataOperadorAritmetico(char caracter) {
		if (caracter == '+') {
			adicionaToken("+", "smais");
		} else if (caracter == '-') {
			adicionaToken("-", "smenos");
		} else {
			adicionaToken("*", "smult");
		}
		// ver pq do divis�o n�o estar aqui
	}

	void trataOperadorRelacional(char caracter) {

		String op = Character.toString(caracter);
		char newCaracter;

		if (caracter == '<') {

			nCaracter++;
			if (nCaracter < strLine.length()) {
				newCaracter = strLine.charAt(nCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					adicionaToken("<=", "smenorig");
				} else {
					nCaracter--;
				}
			}
			adicionaToken("<", "smenor");

		} else if (caracter == '>') {

			nCaracter++;
			if (nCaracter < strLine.length()) {
				newCaracter = strLine.charAt(nCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					adicionaToken(">=", "smaiorig");
				} else {
					nCaracter--;
				}
			}
			adicionaToken(">", "smaior");

		} else if (caracter == '=') {

			adicionaToken("=", "sig");

		} else if (caracter == '!') {
			nCaracter++;
			if (nCaracter < strLine.length()) {
				newCaracter = strLine.charAt(nCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					adicionaToken("!=", "sdif");
				}
			}
			erroDetectado = true;
			nCaracter = strLine.length();
		}
		erroDetectado = true;
		nCaracter = strLine.length();
	}

	void trataPontuacao(char caracter) {
		if (caracter == ';') {
			adicionaToken(";", "sponto_virgula");
		} else if (caracter == ',') {
			adicionaToken(",", "svirgula");
		} else if (caracter == '(') {
			adicionaToken("(", "sabre_parenteses");
		} else if (caracter == ')') {
			adicionaToken(")", "sfecha_parenteses");
		} else {
			adicionaToken(".", "sponto");
		}
	}

	void analisador(String lexema) {

//		String Lexema = linha.get(0);

		switch (lexema) {
		case "programa":
//			if (Lexema.contains(";")) {
			MLexama.add(lexema);
			MSimbolo.add("Sprograma");
//	
//				
//				for (int i = 1; i <= linha)
//			
//				
//				MLexama.add(String.valueOf(linha.replace(";", "")));
//				MSimbolo.add("Sindentificador");
//				Analisador(";", null, 0);
//
//			} else {
//				MErro.add(String.valueOf(nlinha));
//			}

			break;
		case "inicio":
			MLexama.add(lexema);
			MSimbolo.add("Sinicio");
			break;
		case "fim":
			MLexama.add(lexema);
			;
			MSimbolo.add("Sfim");
			break;
		case "procedimento":
			MLexama.add(lexema);
			MSimbolo.add("Sprocedimento");
//			if (linha.contains(";")) {
//				MLexama.add(String.valueOf(linha.replace(";", "")));
//				MSimbolo.add("Sindentificador");
//				Analisador(";", null, 0);
//
//			} else {
//				MErro.add(String.valueOf(nlinha));
//			}
			break;
		case "funcao":
			MLexama.add(lexema);
			MSimbolo.add("Sfuncao");
			break;
		case "se":
			MLexama.add(lexema);
			MSimbolo.add("Sse");
//			System.out.println();
//			if (linha.contains(">")) {
//				String[] variavel = linha.split(">");
//				MLexama.add(variavel[0]);
//				MSimbolo.add("Sindentificador");
//				Analisador(">", null, 0);
//				MLexama.add(variavel[1]);
//				MSimbolo.add("Sindentificador");
//			} else if (linha.contains(">=")) {
//				String[] variavel = linha.split(">=");
//				MLexama.add(variavel[0]);
//				MSimbolo.add("Sindentificador");
//				Analisador(">=", null, 0);
//				MLexama.add(variavel[1]);
//				MSimbolo.add("Sindentificador");
//			} else if (linha.contains("<")) {
//				String[] variavel = linha.split("<");
//				MLexama.add(variavel[0]);
//				MSimbolo.add("Sindentificador");
//				Analisador("<", null, 0);
//				MLexama.add(variavel[1]);
//				MSimbolo.add("Sindentificador");
//			} else if (linha.contains("<=")) {
//				String[] variavel = linha.split("<=");
//				MLexama.add(variavel[0]);
//				MSimbolo.add("Sindentificador");
//				Analisador("<=", null, 0);
//				MLexama.add(variavel[1]);
//				MSimbolo.add("Sindentificador");
//			} else {
//				MErro.add(String.valueOf(nlinha));
//			}
			break;
		case "entao":
			MLexama.add(lexema);
			MSimbolo.add("Sentao");
//			if (linha.contains(":=")) {
//				String[] variavel = linha.split(":=");
//				MLexama.add(variavel[0]);
//				MSimbolo.add("Sindentificador");
//				Analisador(":=", null, 0);
//				if (variavel[1].replace(";", "").replace(" ", "").matches("^[0-9]*$")) {
//					MLexama.add(variavel[1].replace(";", ""));
//					MSimbolo.add("Snumero");
//					Analisador(";", null, 0);
//				} else {
//					MErro.add(String.valueOf(nlinha));
//				}
//
//			} else {
//				MErro.add(String.valueOf(nlinha));
//			}
			break;
		case "senao":
			MLexama.add(lexema);
			MSimbolo.add("Ssenao");
			break;
		case "enquanto":
			MLexama.add(lexema);
			MSimbolo.add("Senquanto");
			break;
		case "faca":
			MLexama.add(lexema);
			MSimbolo.add("Sfaca");
			break;
		case ":=":
			MLexama.add(lexema);
			MSimbolo.add("Satribui��o");
			break;
		case "escreva":
			MLexama.add(lexema);
			MSimbolo.add("Sescreva");
			break;
		case "leia":
			MLexama.add(lexema);
			MSimbolo.add("Sleia");
			break;
		case "var":
			MLexama.add(lexema);
			MSimbolo.add("Svar");
//			if (linha.contains(":") && linha.contains(";")) {
//
//				String[] variavel = linha.split(":");
//				String[] NomeVariavel = variavel[0].split(",");
//				for (String a : NomeVariavel) {
//					MLexama.add(a.replace(":", ""));
//					MSimbolo.add("Sindentificador");
//					Analisador(",", null, 0);
//				}
//				Analisador(":", null, 0);
//				Analisador(variavel[1].replace(" ", "").replace(";", ""), null, 0);
//				Analisador(";", null, 0);
//			} else {
//				MErro.add(String.valueOf(nlinha));
//			}

			break;
		case "inteiro":
			MLexama.add(lexema);
			MSimbolo.add("Sinteiro");
			break;
		case "booleano":
			MLexama.add(lexema);
			MSimbolo.add("Sbooleano");
			break;
		case "indentificador":
			MLexama.add(lexema);
			MSimbolo.add("Sindentificador");
			break;
		case "numero":
			MLexama.add(lexema);
			MSimbolo.add("Sn�mero");
			break;
		case ".":
			MLexama.add(lexema);
			MSimbolo.add("Sponto");
			break;
		case ";":
			MLexama.add(lexema);
			MSimbolo.add("Sponto_virgula");
			break;
		case ",":
			MLexama.add(lexema);
			MSimbolo.add("Svirgula");
			break;
		case "(":
			MLexama.add(lexema);
			MSimbolo.add("Sabre_parenteses");
			break;
		case ")":
			MLexama.add(lexema);
			MSimbolo.add("Sfecha_parenteses");
			break;
		case ">":
			MLexama.add(lexema);
			MSimbolo.add("Smaior");
			break;
		case ">=":
			MLexama.add(lexema);
			MSimbolo.add("Smaiorig");
			break;
		case "=":
			MLexama.add(lexema);
			MSimbolo.add("Sig");
			break;
		case "<":
			MLexama.add(lexema);
			MSimbolo.add("Smenor");
			break;
		case "<=":
			MLexama.add(lexema);
			MSimbolo.add("Smenorig");
			break;
		case "!=":
			MLexama.add(lexema);
			MSimbolo.add("Sgif");
			break;
		case "+":
			MLexama.add(lexema);
			MSimbolo.add("Smais");
			break;
		case "-":
			MLexama.add(lexema);
			MSimbolo.add("Smenos");
			break;
		case "*":
			MLexama.add(lexema);
			MSimbolo.add("Smult");
			break;
		case "div":
			MLexama.add(lexema);
			MSimbolo.add("Sdiv");
			break;
		case "e":
			MLexama.add(lexema);
			MSimbolo.add("Se");
			break;
		case "ou":
			MLexama.add(lexema);
			MSimbolo.add("Sou");
			break;
		case "nao":
			MLexama.add(lexema);
			MSimbolo.add("Snao");
			break;
		case ":":
			MLexama.add(lexema);
			MSimbolo.add("Sdoispontos");
			break;
		default:
			// System.err.println(Lexema);

			break;

		}

	}

//	boolean isInvalidCharacter(char character) {
//		return (character == '\n' || Character.isWhitespace(character) || Character.isSpaceChar(character));
//	}

	void adicionaToken(String lexema, String simbolo) {
		MLexama.add(lexema);
		MSimbolo.add(simbolo);
	}

	public void TabelaLexema() {
		// pnlPilha = new MeuJPanel();
		rowDataLexema = new Vector<Vector>();
		columnNamesLexema = new Vector<String>();
		// pnlPilha.remove(barraRolagemLexema);
		for (int i = 0; i < MSimbolo.size(); i++) {
			rowLinhaLexema = new Vector<String>(); // limpa o vector, nao sei se eh o mais correto, pode afetar a
													// memoria fisica
			rowLinhaLexema.addElement(String.valueOf(MLexama.get(i)));
			rowLinhaLexema.addElement(String.valueOf(MSimbolo.get(i)));
			rowDataLexema.addElement(rowLinhaLexema);
		}

		columnNamesLexema.addElement("Lexema");
		columnNamesLexema.addElement("Simbolo");
		tabelaLexema = new JTable(rowDataLexema, columnNamesLexema);
		barraRolagemLexema = new JScrollPane(tabelaLexema);
		tabelaLexema.setPreferredScrollableViewportSize(tabelaLexema.getPreferredSize());
		tabelaLexema.setFillsViewportHeight(false);
		pnlPilha.add(barraRolagemLexema);

		JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);
		scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		texBreakPoint.setText(MErro.toString()); // colocar codigo aquui
		pnlAmostraDados.add(scrollableTextBreakPoint);

	}
}
