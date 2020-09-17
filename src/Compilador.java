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
import javax.swing.JTextArea;

public class Compilador extends MaquinaVirtual {
	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	protected Vector<String> MErro = new Vector<String>();

	BufferedReader br;
	String strLine = null;
	boolean erroDetectado = false;
	boolean fimDaLinha = false;
	int countCaracter = -1;

	void AnalisadorEntrada() {
		int nlinha = 0;
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
					
					nlinha ++;
					countCaracter = -1;
					while (strLine.length() > countCaracter && !strLine.equals("")) {
						countCaracter++;

						System.out
								.println("countCaracter = " + countCaracter + "strLine.length() = " + strLine.length());
						if (countCaracter < strLine.length()) {
							char caracter = strLine.charAt(countCaracter);

							System.out.println("nCaracter = " + countCaracter);
							System.out.println("caracter = " + caracter);

							caracter = trataComentariosConsomeEspaco(caracter);

							if (!fimDaLinha) {
								if (erroDetectado) {
									MErro.add("Linha: " + String.valueOf(nlinha) + "Posição: " + String.valueOf(countCaracter) + "\n");
									break;
								} else {
									pegaToken(caracter);
									if (erroDetectado) {
										MErro.add("Linha: " + String.valueOf(nlinha) + "Posição: " + String.valueOf(countCaracter) + "\n");
										break;
									}
								}
							} else {
								fimDaLinha = false;
							}
						}

					}
					
				}
			} catch (Exception e1) {
				System.err.println("Error: " + e1.getMessage());
				System.err.println(countCaracter);
				System.err.println(strLine.charAt(countCaracter));
				MErro.add("Linha: " + String.valueOf(nlinha) + "Posição: " + String.valueOf(countCaracter) + "\n");
				}

			if (erroDetectado) {
				MErro.add("Linha: " + String.valueOf(nlinha) + "Posição: " + String.valueOf(countCaracter)+ "\n" );
				System.out.println("Erro na linha: " + countCaracter);
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
					countCaracter++;
					if (countCaracter >= strLine.length()) {
						erroDetectado = true;
						break;
					}
					caracter = strLine.charAt(countCaracter);
				}
				if (caracter == '}') {
					countCaracter++;
					if (countCaracter >= strLine.length()) {

						fimDaLinha = true;
						return ' ';

					} else {
						return caracter;
					}
				}
			}

			if (caracter == ' ') {
				countCaracter++;
				if (countCaracter >= strLine.length()) {
					return caracter;
				}
				caracter = strLine.charAt(countCaracter);
			}

			if (caracter == '/') {
				countCaracter++;
				if (countCaracter >= strLine.length()) {
					erroDetectado = true;
					break;
				}
				caracter = strLine.charAt(countCaracter);

				if (caracter == '/') {
					try {
						strLine = br.readLine();
						countCaracter = 0;
						return strLine.charAt(countCaracter);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (caracter == '*') {
					while (true) {
						countCaracter++;
						if (countCaracter >= strLine.length()) {
							erroDetectado = true;
							return ' ';
						}
						caracter = strLine.charAt(countCaracter);

						if (caracter == '*') {
							countCaracter++;
							if (countCaracter >= strLine.length()) {
								erroDetectado = true;
								break;
							}
							caracter = strLine.charAt(countCaracter);

							if (caracter == '/') {
								countCaracter++;
								if (countCaracter >= strLine.length()) {
									try {
										strLine = br.readLine();
										countCaracter = 0;
										return strLine.charAt(countCaracter);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								return strLine.charAt(countCaracter);
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
		} else if (trataComentariosConsomeEspaco(caracter) == ' '){
			// apenas ignora
		}  else if (caracter == ' ') {
			// apenas ignora
		}else {
			erroDetectado = true;
			countCaracter = strLine.length();
		}
	}

	void trataDigito(char caracter) {
		String numero;
		char proximoCaracter;

		numero = Character.toString(caracter);

		if (countCaracter < strLine.length()) {
			proximoCaracter = strLine.charAt(countCaracter);
			while (Character.isDigit(proximoCaracter) && ((countCaracter + 1) < strLine.length())
					&& !Character.isWhitespace(proximoCaracter)) {
				countCaracter++;
				proximoCaracter = strLine.charAt(countCaracter);
				if (Character.isDigit(proximoCaracter)) {
					numero = numero + Character.toString(proximoCaracter);
				}
			}
			if (!(Character.isDigit(proximoCaracter))) {
				countCaracter--;
			}

		}
		analisador(numero);
	}

	void trataIdentificadorPalavraReservada(char caracter) {
		String palavra;
		char proximoCaracter;

		palavra = Character.toString(caracter);

		if (countCaracter < strLine.length()) {
			proximoCaracter = strLine.charAt(countCaracter);
			if (Character.isLetter(proximoCaracter)) {
				while ((Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
						|| proximoCaracter == '_') && countCaracter < strLine.length() - 1
						&& !Character.isWhitespace(proximoCaracter)) {
					countCaracter++;
					proximoCaracter = strLine.charAt(countCaracter);
					if ((Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
							|| proximoCaracter == '_')) {
						palavra = palavra + Character.toString(proximoCaracter);
					}
				}
			}

			if (!(Character.isLetter(proximoCaracter) || Character.isDigit(proximoCaracter)
					|| proximoCaracter == '_')) {
				countCaracter--;
			}

		}
		System.out.println("palavra = " + palavra);
		analisador(palavra);

	}

	void trataAtribuicao(char caracter) {

		if ((countCaracter + 1) < strLine.length()) {
			countCaracter = countCaracter + 1;
			char simbolo = strLine.charAt(countCaracter);

			if (simbolo == '=') {
				analisador(":=");
			} else {
				countCaracter--;
				analisador(":");
			}
		}
	}

	void trataOperadorAritmetico(char caracter) {
		if (caracter == '+') {
			analisador("+");
		} else if (caracter == '-') {
			analisador("-");
		} else {
			analisador("*");
		}
		// ver pq do divisão não estar aqui
	}

	void trataOperadorRelacional(char caracter) {

		String op = Character.toString(caracter);
		char newCaracter;

		if (caracter == '<') {

			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					analisador("<=");
				} else {
					countCaracter--;
					analisador("<"); //add 2 vezes
				}
			}
			

		} else if (caracter == '>') {

			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					analisador(">=");
				} else {
					countCaracter--;
					analisador(">");
				}
			}
			

		} else if (caracter == '=') {

			analisador("=");//possivel erro???

		} else if (caracter == '!') {
			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					analisador("!=");
				}
			} else {
				erroDetectado = true;
				countCaracter = strLine.length();
			}
		} else {
			erroDetectado = true;
			countCaracter = strLine.length();
		}
	}

	void trataPontuacao(char caracter) {
		if (caracter == ';') {
			analisador(";");
		} else if (caracter == ',') {
			analisador(",");
		} else if (caracter == '(') {
			analisador("(");
		} else if (caracter == ')') {
			analisador(")");
		} else {
			analisador(".");
		}
	}

	void analisador(String lexema) {
		switch (lexema) {
		case "programa":
			MLexama.add(lexema);
			MSimbolo.add("Sprograma");
			break;
		case "inicio":
			MLexama.add(lexema);
			MSimbolo.add("Sinicio");
			break;
		case "fim":
			MLexama.add(lexema);
			MSimbolo.add("Sfim");
			break;
		case "procedimento":
			MLexama.add(lexema);
			MSimbolo.add("Sprocedimento");
			break;
		case "funcao":
			MLexama.add(lexema);
			MSimbolo.add("Sfuncao");
			break;
		case "se":
			MLexama.add(lexema);
			MSimbolo.add("Sse");
			break;
		case "entao":
			MLexama.add(lexema);
			MSimbolo.add("Sentao");
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
			MSimbolo.add("Snúmero");
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
			MLexama.add(lexema);
			MSimbolo.add("Sidentificador");
			break;

		}

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
		pnlTabela.add(barraRolagemLexema);
		texBreakPoint = new JTextArea(10, 15);
		
		JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);
		scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		texBreakPoint.setText(MErro.toString()); // colocar codigo aquui
		pnlAmostraDados.add(scrollableTextBreakPoint);

	}
}
