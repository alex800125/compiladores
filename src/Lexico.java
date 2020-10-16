import Excecoes.excecaoLexico;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JFileChooser;

public class Lexico {

	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	protected Vector<String> MErro = new Vector<String>();

	BufferedReader br;
	BufferedReader br2;
	String strLine = null;
	String mensagemErro = "";
	boolean erroDetectado = false;
	boolean fimDoArquivo = false;
	boolean IniciodoComentario = false;
	int countCaracter = -1;
	int nlinha = 0;

	public BufferedReader InicializadorArquivo() {

		// System.out.println("InicializadorArquivo");
		JFileChooser fileChooser = new JFileChooser();

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {

			File selectedFile = fileChooser.getSelectedFile();

			try {
				FileInputStream fstream = new FileInputStream(selectedFile);
				DataInputStream in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));

				FileInputStream fstream2 = new FileInputStream(selectedFile);
				DataInputStream in2 = new DataInputStream(fstream2);
				br2 = new BufferedReader(new InputStreamReader(in2));
				nlinha++;
				strLine = br.readLine();
				return br2;
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		}
		return null;
	}

	public Token AnalisadorLexico() throws IOException {

		System.out.println("AnalisadorEntrada nlinha = " + nlinha);
		Token token = null;
		char caracter = ' ';

		// while para retirar espaços vazios que possam existir no começo
		while (countCaracter >= strLine.length()) {
			if ((strLine = br.readLine()) != null) {
				countCaracter = 0;
				nlinha++;
				if (strLine.length() > 0) {
					caracter = strLine.charAt(countCaracter);
				}
			} else {
				return new Token(Simbolos.eof, "EOF", nlinha);
			}
		}

		try {
			caracter = trataComentariosConsomeEspaco(caracter);

			if (!erroDetectado && !fimDoArquivo) {
				token = pegaToken(caracter);
				if (erroDetectado) {
					System.out.println("ERRO nlinha = " + nlinha);
					MErro.add("Erro na Linha: " + nlinha);
				} else {
					return token;
				}
			} else if (fimDoArquivo) {
				return new Token(Simbolos.eof, "EOF", nlinha);
			}
		} catch (excecaoLexico e) {
			System.out.println(e);
		}

		return new Token(Simbolos.erro, "ERRO", nlinha);
	}

	private final char trataComentariosConsomeEspaco(char caracter) throws excecaoLexico, IOException {
		int linha = nlinha;
		while (caracter == '{' || caracter == ' ' || caracter == '/') {

			if (Character.isWhitespace(caracter)) {
				countCaracter++;
				while (countCaracter >= strLine.length()) {
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
					} else {
						fimDoArquivo = true;
						return ' ';
					}
				}
				caracter = strLine.charAt(countCaracter);
			}

			if (caracter == '{') {
				while (caracter != '}') {

					countCaracter++;
					if (countCaracter >= strLine.length()) {
						if ((strLine = br.readLine()) != null) {
							countCaracter = 0;
							nlinha++;
						} else {
							throw new excecaoLexico("Comentario não finalizado na linha " + linha);
						}
					}

					if (countCaracter < strLine.length()) {
						caracter = strLine.charAt(countCaracter);
					}
				}

				if (caracter == '}') {
					countCaracter++;
					while (countCaracter >= strLine.length()) {

						if ((strLine = br.readLine()) != null) {
							countCaracter = 0;
							nlinha++;
							if (strLine.length() > 0) {
								while (Character.isWhitespace(caracter = strLine.charAt(countCaracter))) {
									countCaracter++;
								}
								return trataComentariosConsomeEspaco(caracter);
							}
						} else {
							fimDoArquivo = true;
							return ' ';
						}
					}
					caracter = strLine.charAt(countCaracter);
				} else {
					throw new excecaoLexico("Comentario não finalizado na linha " + linha);
				}
			}

			if (caracter == '/') {
				countCaracter++;
				if (countCaracter >= strLine.length()) {
					erroDetectado = true;
					throw new excecaoLexico("Caracter não esperado na linha = " + linha);
				}
				caracter = strLine.charAt(countCaracter);

				if (caracter == '/') {

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

				} else if (caracter == '*') {
					countCaracter++;
					while (caracter != '/') {
						if (countCaracter >= strLine.length()) {
							if ((strLine = br.readLine()) != null) {
								countCaracter = 0;
								nlinha++;
							} else {
								throw new excecaoLexico("Comentario não finalizado na linha " + linha);
							}
						}

						if (countCaracter < strLine.length()) {
							caracter = strLine.charAt(countCaracter);
						}

						while (caracter != '*') {

							countCaracter++;
							if (countCaracter >= strLine.length()) {
								if ((strLine = br.readLine()) != null) {
									countCaracter = 0;
									nlinha++;
								} else {
									throw new excecaoLexico("Comentario não finalizado na linha " + linha);
								}
							}

							if (countCaracter < strLine.length()) {
								caracter = strLine.charAt(countCaracter);
							}
						}

						countCaracter++;
						if (countCaracter < strLine.length()) {
							caracter = strLine.charAt(countCaracter);
						}

					}
					countCaracter++;
					if (countCaracter >= strLine.length()) {
						if ((strLine = br.readLine()) != null) {
							countCaracter = 0;
							nlinha++;
						} else {
							throw new excecaoLexico("Comentario não finalizado na linha " + linha);
						}
					}

					if (countCaracter < strLine.length()) {
						caracter = strLine.charAt(countCaracter);
					}
				}
			}
		}
		return caracter;
	}

	private final Token pegaToken(char caracter) throws excecaoLexico {

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
			System.out.println("caracter = " + caracter);
			erroDetectado = true;
			countCaracter = strLine.length();
			throw new excecaoLexico("Caracter não tratado = '" + caracter + "' | linha = " + nlinha);
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
		MSimbolo.add(Simbolos.numero);

		return new Token(Simbolos.numero, numero, nlinha);
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

	private Token trataOperadorRelacional(char caracter) throws excecaoLexico {

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
					throw new excecaoLexico("Caracter não esperado na linha = " + nlinha);
				}
			} else {
				erroDetectado = true;
				countCaracter = strLine.length();
				throw new excecaoLexico("Caracter não esperado na linha = " + nlinha);
			}
		} else {
			erroDetectado = true;
			countCaracter = strLine.length();
			throw new excecaoLexico("Caracter não esperado na linha = " + nlinha);
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
			MSimbolo.add(Simbolos.programa);
			token = new Token(Simbolos.programa, lexema, nlinha);
			break;
		case "inicio":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.inicio);
			token = new Token(Simbolos.inicio, lexema, nlinha);
			break;
		case "fim":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.fim);
			token = new Token(Simbolos.fim, lexema, nlinha);
			break;
		case "procedimento":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.procedimento);
			token = new Token(Simbolos.procedimento, lexema, nlinha);
			break;
		case "funcao":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.funcao);
			token = new Token(Simbolos.funcao, lexema, nlinha);
			break;
		case "se":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.se);
			token = new Token(Simbolos.se, lexema, nlinha);
			break;
		case "entao":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.entao);
			token = new Token(Simbolos.entao, lexema, nlinha);
			break;
		case "senao":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.senao);
			token = new Token(Simbolos.senao, lexema, nlinha);
			break;
		case "enquanto":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.enquanto);
			token = new Token(Simbolos.enquanto, lexema, nlinha);
			break;
		case "faca":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.faca);
			token = new Token(Simbolos.faca, lexema, nlinha);
			break;
		case "escreva":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.escreva);
			token = new Token(Simbolos.escreva, lexema, nlinha);
			break;
		case "leia":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.leia);
			token = new Token(Simbolos.leia, lexema, nlinha);
			break;
		case "var":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.var);
			token = new Token(Simbolos.var, lexema, nlinha);
			break;
		case "inteiro":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.inteiro);
			token = new Token(Simbolos.inteiro, lexema, nlinha);
			break;
		case "booleano":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.booleano);
			token = new Token(Simbolos.booleano, lexema, nlinha);
			break;
		case "numero":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.numero);
			token = new Token(Simbolos.numero, lexema, nlinha);
			break;
		case ".":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.ponto);
			token = new Token(Simbolos.ponto, lexema, nlinha);
			break;
		case ";":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.ponto_virgula);
			token = new Token(Simbolos.ponto_virgula, lexema, nlinha);
			break;
		case ",":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.virgula);
			token = new Token(Simbolos.virgula, lexema, nlinha);
			break;
		case "(":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.abre_parenteses);
			token = new Token(Simbolos.abre_parenteses, lexema, nlinha);
			break;
		case ")":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.fecha_parenteses);
			token = new Token(Simbolos.fecha_parenteses, lexema, nlinha);
			break;
		case ">":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.maior);
			token = new Token(Simbolos.maior, lexema, nlinha);
			break;
		case ">=":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.maiorig);
			token = new Token(Simbolos.maiorig, lexema, nlinha);
			break;
		case "=":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.ig);
			token = new Token(Simbolos.ig, lexema, nlinha);
			break;
		case "<":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.menor);
			token = new Token(Simbolos.menor, lexema, nlinha);
			break;
		case "<=":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.menorig);
			token = new Token(Simbolos.menorig, lexema, nlinha);
			break;
		case "!=":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.dif);
			token = new Token(Simbolos.dif, lexema, nlinha);
			break;
		case "+":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.mais);
			token = new Token(Simbolos.mais, lexema, nlinha);
			break;
		case "-":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.menos);
			token = new Token(Simbolos.menos, lexema, nlinha);
			break;
		case "*":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.mult);
			token = new Token(Simbolos.mult, lexema, nlinha);
			break;
		case "div":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.div);
			token = new Token(Simbolos.div, lexema, nlinha);
			break;
		case "e":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.e);
			token = new Token(Simbolos.e, lexema, nlinha);
			break;
		case "ou":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.ou);
			token = new Token(Simbolos.ou, lexema, nlinha);
			break;
		case "nao":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.nao);
			token = new Token(Simbolos.nao, lexema, nlinha);
			break;
		case ":":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.doispontos);
			token = new Token(Simbolos.doispontos, lexema, nlinha);
			break;
		case ":=":
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.atribuicao);
			token = new Token(Simbolos.atribuicao, lexema, nlinha);
			break;
		default:
			MLexama.add(lexema);
			MSimbolo.add(Simbolos.identificador);
			token = new Token(Simbolos.identificador, lexema, nlinha);
			break;

		}

		return token;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

}
