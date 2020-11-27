import Excecoes.excecaoLexico;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JFileChooser;

public class LexicoNOVO {

	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	protected Vector<String> MErro = new Vector<String>();
	//Importando variveis
	protected BufferedReader br = MaquinaVirtual.br;

	protected String strLine =  MaquinaVirtual.strLine;
	
	String mensagemErro = "";
	boolean erroDetectado = false;
	boolean fimDoArquivo = false;
	boolean IniciodoComentario = false;
	int countCaracter = -1;
	int nlinha = 0;

	public void InicializadorArquivo() {
		
		nlinha++;
		
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
				return new Token(Constantes.eof, "EOF", nlinha);
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
				return new Token(Constantes.eof, "EOF", nlinha);
			}
		} catch (excecaoLexico e) {
			System.out.println(e);
		}

		return new Token(Constantes.erro, "ERRO", nlinha);
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
		MSimbolo.add(Constantes.numero);

		return new Token(Constantes.numero, numero, nlinha);
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
			MSimbolo.add(Constantes.programa);
			token = new Token(Constantes.programa, lexema, nlinha);
			break;
		case "inicio":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.inicio);
			token = new Token(Constantes.inicio, lexema, nlinha);
			break;
		case "fim":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.fim);
			token = new Token(Constantes.fim, lexema, nlinha);
			break;
		case "procedimento":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.procedimento);
			token = new Token(Constantes.procedimento, lexema, nlinha);
			break;
		case "funcao":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.funcao);
			token = new Token(Constantes.funcao, lexema, nlinha);
			break;
		case "se":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.se);
			token = new Token(Constantes.se, lexema, nlinha);
			break;
		case "entao":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.entao);
			token = new Token(Constantes.entao, lexema, nlinha);
			break;
		case "senao":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.senao);
			token = new Token(Constantes.senao, lexema, nlinha);
			break;
		case "enquanto":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.enquanto);
			token = new Token(Constantes.enquanto, lexema, nlinha);
			break;
		case "faca":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.faca);
			token = new Token(Constantes.faca, lexema, nlinha);
			break;
		case "escreva":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.escreva);
			token = new Token(Constantes.escreva, lexema, nlinha);
			break;
		case "leia":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.leia);
			token = new Token(Constantes.leia, lexema, nlinha);
			break;
		case "var":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.var);
			token = new Token(Constantes.var, lexema, nlinha);
			break;
		case "inteiro":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.inteiro);
			token = new Token(Constantes.inteiro, lexema, nlinha);
			break;
		case "booleano":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.booleano);
			token = new Token(Constantes.booleano, lexema, nlinha);
			break;
		case "numero":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.numero);
			token = new Token(Constantes.numero, lexema, nlinha);
			break;
		case ".":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.ponto);
			token = new Token(Constantes.ponto, lexema, nlinha);
			break;
		case ";":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.ponto_virgula);
			token = new Token(Constantes.ponto_virgula, lexema, nlinha);
			break;
		case ",":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.virgula);
			token = new Token(Constantes.virgula, lexema, nlinha);
			break;
		case "(":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.abre_parenteses);
			token = new Token(Constantes.abre_parenteses, lexema, nlinha);
			break;
		case ")":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.fecha_parenteses);
			token = new Token(Constantes.fecha_parenteses, lexema, nlinha);
			break;
		case ">":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.maior);
			token = new Token(Constantes.maior, lexema, nlinha);
			break;
		case ">=":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.maiorig);
			token = new Token(Constantes.maiorig, lexema, nlinha);
			break;
		case "=":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.ig);
			token = new Token(Constantes.ig, lexema, nlinha);
			break;
		case "<":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.menor);
			token = new Token(Constantes.menor, lexema, nlinha);
			break;
		case "<=":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.menorig);
			token = new Token(Constantes.menorig, lexema, nlinha);
			break;
		case "!=":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.dif);
			token = new Token(Constantes.dif, lexema, nlinha);
			break;
		case "+":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.mais);
			token = new Token(Constantes.mais, lexema, nlinha);
			break;
		case "-":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.menos);
			token = new Token(Constantes.menos, lexema, nlinha);
			break;
		case "*":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.mult);
			token = new Token(Constantes.mult, lexema, nlinha);
			break;
		case "div":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.div);
			token = new Token(Constantes.div, lexema, nlinha);
			break;
		case "e":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.e);
			token = new Token(Constantes.e, lexema, nlinha);
			break;
		case "ou":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.ou);
			token = new Token(Constantes.ou, lexema, nlinha);
			break;
		case "nao":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.nao);
			token = new Token(Constantes.nao, lexema, nlinha);
			break;
		case ":":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.doispontos);
			token = new Token(Constantes.doispontos, lexema, nlinha);
			break;
		case ":=":
			MLexama.add(lexema);
			MSimbolo.add(Constantes.atribuicao);
			token = new Token(Constantes.atribuicao, lexema, nlinha);
			break;
		default:
			MLexama.add(lexema);
			MSimbolo.add(Constantes.identificador);
			token = new Token(Constantes.identificador, lexema, nlinha);
			break;

		}

		return token;
	}

	public String getMensagemErro() {
		return mensagemErro;
	}

}
