import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

public class Lexico extends MaquinaVirtual {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	protected Vector<String> MErro = new Vector<String>();

	BufferedReader br;
	String strLine = null;
	boolean erroDetectado = false;
	boolean fimDaLinha = false;
	boolean IniciodoComentario = false;
	int countCaracter = 0;
	int nlinha = 0;

	public void InicializadorArquivo() {
		System.out.println("InicializadorArquivo");
		JFileChooser fileChooser = new JFileChooser();

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {

			File selectedFile = fileChooser.getSelectedFile();

			try {
				FileInputStream fstream = new FileInputStream(selectedFile);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
				nlinha++;
				strLine = br.readLine();

			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	public Token AnalisadorEntrada() throws IOException {
		//System.out.println("AnalisadorEntrada nlinha = " + nlinha);
		Token token = null;
		char caracter = ' ';

//		System.out.println("countCaracter = " + countCaracter + " | strLine.length() = " + strLine.length());

		while (countCaracter >= strLine.length()) {
			try {
				if ((strLine = br.readLine()) != null) {
					countCaracter = 0;
					nlinha++;
					if (strLine.length() > 0) {
						caracter = strLine.charAt(countCaracter);
					}
				} else {
					return new Token("ERRO", "ERRO", nlinha);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		caracter = trataComentariosConsomeEspaco(caracter);
//		System.out.println("caracter = " + caracter);

		if (!erroDetectado || !fimDaLinha) {
			token = pegaToken(caracter);
			if (erroDetectado) {
				//System.out.println("ERRO nlinha = " + nlinha);
				MErro.add("Erro na Linha: " + nlinha);
			} else {
				return token;
			}
		}

		return new Token("ERRO", "ERRO", nlinha);
	}

	private final char trataComentariosConsomeEspaco(char caracter) {
		while (caracter == '{' || caracter == ' ' || caracter == '/') {
			if (Character.isWhitespace(caracter)) {
				countCaracter++;
				while (countCaracter >= strLine.length()) {
					try {
						if ((strLine = br.readLine()) != null) {
							countCaracter = 0;
							nlinha++;
							if (strLine.length() > 0) {
								while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
									countCaracter++;
									if (countCaracter >= strLine.length())
										break;
								}
								return trataComentariosConsomeEspaco(caracter);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				caracter = strLine.charAt(countCaracter);
			}

			if (caracter == '{') {
				while (caracter != '}') {

					countCaracter++;
					while (countCaracter >= strLine.length()) {
						try {
							if ((strLine = br.readLine()) != null) {
								countCaracter = 0;
								nlinha++;
								if (strLine.length() > 0) {
									while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
										countCaracter++;
									}
									return trataComentariosConsomeEspaco(caracter);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					caracter = strLine.charAt(countCaracter);

				}
				if (caracter == '}') {
					countCaracter++;
					while (countCaracter >= strLine.length()) {
						try {
							if ((strLine = br.readLine()) != null) {
								countCaracter = 0;
								nlinha++;
								if (strLine.length() > 0) {
									while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
										countCaracter++;
									}
									return trataComentariosConsomeEspaco(caracter);
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

					caracter = strLine.charAt(countCaracter);
				}
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
						if ((strLine = br.readLine()) != null) {

							countCaracter = 0;
							nlinha++;
							if (strLine.length() > 0) {
								while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
									countCaracter++;
								}
								return trataComentariosConsomeEspaco(caracter);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else if (caracter == '*') {
					while (true) {
						countCaracter++;
						while (countCaracter >= strLine.length()) {
							try {
								//System.out.println("while");
								if ((strLine = br.readLine()) != null) {
									countCaracter = 0;
									nlinha++;
									//System.out.println("dentro while nlinha = " + nlinha);
									if (strLine.length() > 0) {
										while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
											countCaracter++;
										}
										return trataComentariosConsomeEspaco(caracter);
									}
								}

							} catch (IOException e) {
								e.printStackTrace();
							}
						}

						caracter = strLine.charAt(countCaracter);

						if (caracter == '*') {
							countCaracter++;
							while (countCaracter >= strLine.length()) {
								try {
									if ((strLine = br.readLine()) != null) {
										countCaracter = 0;
										nlinha++;
										if (strLine.length() > 0) {
											while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
												countCaracter++;
											}
											return trataComentariosConsomeEspaco(caracter);
										}
									}
								} catch (IOException e) {
									e.printStackTrace();
								}
							}

							caracter = strLine.charAt(countCaracter);

							if (caracter == '/') {
								countCaracter++;
								while (countCaracter >= strLine.length()) {
									try {
										if ((strLine = br.readLine()) != null) {
											countCaracter = 0;
											nlinha++;
											if (strLine.length() > 0) {
												while (Character
														.isWhitespace(caracter = strLine.charAt(countCaracter))) {
													countCaracter++;
												}
												return trataComentariosConsomeEspaco(caracter);
											}
										}
									} catch (IOException e) {
										e.printStackTrace();
									}
								}

								caracter = strLine.charAt(countCaracter);
							}
						}
					}
				}
			}
		}
		return caracter;

	}

	private final Token pegaToken(char caracter) {

		Token token = null;
		if (Character.isDigit(caracter)) {
			token = trataDigito(caracter);
		} else if (Character.isLetter(caracter)) {
			token = trataIdentificadorPalavraReservada(caracter);
		} else if (caracter == ':') {
			token = trataAtribuicao(caracter);
		} else if (caracter == '+' || caracter == '-' || caracter == '*') {
			token = trataOperadorAritmetico(caracter);
		} else if (caracter == '<' || caracter == '>' || caracter == '=' || caracter == '!') {
			token = trataOperadorRelacional(caracter);
		} else if (caracter == ';' || caracter == ',' || caracter == '(' || caracter == ')' || caracter == '.') {
			token = trataPontuacao(caracter);
		} else {

			// TODO token erro, seria bom criar

			//System.out.println("caracter = " + caracter);
			erroDetectado = true;
			countCaracter = strLine.length();
		}

		return token;
	}

	private Token trataDigito(char caracter) {
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

		MLexama.add(numero);
		MSimbolo.add("Snumero");

		return new Token("Snumero", numero, nlinha);
	}

	private Token trataIdentificadorPalavraReservada(char caracter) {
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

		return analisador(palavra);

	}

	private Token trataAtribuicao(char caracter) {

		Token token = null;
		if ((countCaracter + 1) < strLine.length()) {
			countCaracter = countCaracter + 1;
			char simbolo = strLine.charAt(countCaracter);

			if (simbolo == '=') {
				token = analisador(":=");
			} else {
				countCaracter--;
				token = analisador(":");
			}
		}
		return token;
	}

	private Token trataOperadorAritmetico(char caracter) {

		Token token = null;
		if (caracter == '+') {
			token = analisador("+");
		} else if (caracter == '-') {
			token = analisador("-");
		} else {
			token = analisador("*");
		}

		return token;
	}

	private Token trataOperadorRelacional(char caracter) {

		Token token = null;
		String op = Character.toString(caracter);
		char newCaracter;

		if (caracter == '<') {

			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					token = analisador("<=");
				} else {
					countCaracter--;
					token = analisador("<");
				}
			}

		} else if (caracter == '>') {

			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					token = analisador(">=");
				} else {
					countCaracter--;
					token = analisador(">");
				}
			}

		} else if (caracter == '=') {

			token = analisador("=");

		} else if (caracter == '!') {
			countCaracter++;
			if (countCaracter < strLine.length()) {
				newCaracter = strLine.charAt(countCaracter);
				if (newCaracter == '=') {
					op = op + newCaracter;
					token = analisador("!=");
				} else {
					erroDetectado = true;
					countCaracter = strLine.length();
				}
			} else {
				erroDetectado = true;
				countCaracter = strLine.length();
			}
		} else {
			erroDetectado = true;
			countCaracter = strLine.length();
		}

		return token;
	}

	private Token trataPontuacao(char caracter) {

		Token token = null;

		if (caracter == ';') {
			token = analisador(";");
		} else if (caracter == ',') {
			token = analisador(",");
		} else if (caracter == '(') {
			token = analisador("(");
		} else if (caracter == ')') {
			token = analisador(")");
		} else {
			token = analisador(".");
		}

		return token;
	}

	private Token analisador(String lexema) {
		Token token = null;
		switch (lexema) {
		case "programa":
			MLexama.add(lexema);
			MSimbolo.add("Sprograma");
			token = new Token("Sprograma", lexema, nlinha);
			break;
		case "inicio":
			MLexama.add(lexema);
			MSimbolo.add("Sinicio");
			token = new Token("Sinicio", lexema, nlinha);
			break;
		case "fim":
			MLexama.add(lexema);
			MSimbolo.add("Sfim");
			token = new Token("Sfim", lexema, nlinha);
			break;
		case "procedimento":
			MLexama.add(lexema);
			MSimbolo.add("Sprocedimento");
			token = new Token("Sprocedimento", lexema, nlinha);
			break;
		case "funcao":
			MLexama.add(lexema);
			MSimbolo.add("Sfuncao");
			token = new Token("Sfuncao", lexema, nlinha);
			break;
		case "se":
			MLexama.add(lexema);
			MSimbolo.add("Sse");
			token = new Token("Sse", lexema, nlinha);
			break;
		case "entao":
			MLexama.add(lexema);
			MSimbolo.add("Sentao");
			token = new Token("Sentao", lexema, nlinha);
			break;
		case "senao":
			MLexama.add(lexema);
			MSimbolo.add("Ssenao");
			token = new Token("Ssenao", lexema, nlinha);
			break;
		case "enquanto":
			MLexama.add(lexema);
			MSimbolo.add("Senquanto");
			token = new Token("Senquanto", lexema, nlinha);
			break;
		case "faca":
			MLexama.add(lexema);
			MSimbolo.add("Sfaca");
			token = new Token("Sfaca", lexema, nlinha);
			break;
		case "escreva":
			MLexama.add(lexema);
			MSimbolo.add("Sescreva");
			token = new Token("Sescreva", lexema, nlinha);
			break;
		case "leia":
			MLexama.add(lexema);
			MSimbolo.add("Sleia");
			token = new Token("Sleia", lexema, nlinha);
			break;
		case "var":
			MLexama.add(lexema);
			MSimbolo.add("Svar");
			token = new Token("Svar", lexema, nlinha);
			break;
		case "inteiro":
			MLexama.add(lexema);
			MSimbolo.add("Sinteiro");
			token = new Token("Sinteiro", lexema, nlinha);
			break;
		case "booleano":
			MLexama.add(lexema);
			MSimbolo.add("Sbooleano");
			token = new Token("Sbooleano", lexema, nlinha);
			break;
		case "numero":
			MLexama.add(lexema);
			MSimbolo.add("Snumero");
			token = new Token("Snumero", lexema, nlinha);
			break;
		case ".":
			MLexama.add(lexema);
			MSimbolo.add("Sponto");
			token = new Token("Sponto", lexema, nlinha);
			break;
		case ";":
			MLexama.add(lexema);
			MSimbolo.add("Sponto_virgula");
			token = new Token("Sponto_virgula", lexema, nlinha);
			break;
		case ",":
			MLexama.add(lexema);
			MSimbolo.add("Svirgula");
			token = new Token("Svirgula", lexema, nlinha);
			break;
		case "(":
			MLexama.add(lexema);
			MSimbolo.add("Sabre_parenteses");
			token = new Token("Sabre_parenteses", lexema, nlinha);
			break;
		case ")":
			MLexama.add(lexema);
			MSimbolo.add("Sfecha_parenteses");
			token = new Token("Sfecha_parenteses", lexema, nlinha);
			break;
		case ">":
			MLexama.add(lexema);
			MSimbolo.add("Smaior");
			token = new Token("Smaior", lexema, nlinha);
			break;
		case ">=":
			MLexama.add(lexema);
			MSimbolo.add("Smaiorig");
			token = new Token("Smaiorig", lexema, nlinha);
			break;
		case "=":
			MLexama.add(lexema);
			MSimbolo.add("Sig");
			token = new Token("Sig", lexema, nlinha);
			break;
		case "<":
			MLexama.add(lexema);
			MSimbolo.add("Smenor");
			token = new Token("Smenor", lexema, nlinha);
			break;
		case "<=":
			MLexama.add(lexema);
			MSimbolo.add("Smenorig");
			token = new Token("Smenorig", lexema, nlinha);
			break;
		case "!=":
			MLexama.add(lexema);
			MSimbolo.add("Sdif");
			token = new Token("Sdif", lexema, nlinha);
			break;
		case "+":
			MLexama.add(lexema);
			MSimbolo.add("Smais");
			token = new Token("Smais", lexema, nlinha);
			break;
		case "-":
			MLexama.add(lexema);
			MSimbolo.add("Smenos");
			token = new Token("Smenos", lexema, nlinha);
			break;
		case "*":
			MLexama.add(lexema);
			MSimbolo.add("Smult");
			token = new Token("Smult", lexema, nlinha);
			break;
		case "div":
			MLexama.add(lexema);
			MSimbolo.add("Sdiv");
			token = new Token("Sdiv", lexema, nlinha);
			break;
		case "e":
			MLexama.add(lexema);
			MSimbolo.add("Se");
			token = new Token("Se", lexema, nlinha);
			break;
		case "ou":
			MLexama.add(lexema);
			MSimbolo.add("Sou");
			token = new Token("Sou", lexema, nlinha);
			break;
		case "nao":
			MLexama.add(lexema);
			MSimbolo.add("Snao");
			token = new Token("Snao", lexema, nlinha);
			break;
		case ":":
			MLexama.add(lexema);
			MSimbolo.add("Sdoispontos");
			token = new Token("Sdoispontos", lexema, nlinha);
			break;
		case ":=":
			MLexama.add(lexema);
			MSimbolo.add("Satribuicao");
			token = new Token("Satribuicao", lexema, nlinha);
			break;
		default:
			MLexama.add(lexema);
			MSimbolo.add("Sidentificador");
			token = new Token("Sidentificador", lexema, nlinha);
			break;

		}

		return token;
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
