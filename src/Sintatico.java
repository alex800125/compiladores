import Excecoes.excecaoSintatico;

import java.io.IOException;
import java.util.Vector;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

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

public class Sintatico extends MaquinaVirtual {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Lexico lexico = new Lexico();
	Token token = null;
	BufferedReader br2;
	protected Vector<Token> tokens = new Vector<Token>();

	public void analisadorSintatico() throws IOException {

		lexico.InicializadorArquivo();

		do {
			try {
				token = lexico.AnalisadorEntrada();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (token.getLexema().equals("ERRO")) {
				////System.out.println(lexico.getMensagemErro());
				break;
			} else {
				tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
				////System.out.println("Token lexema = " + token.getLexema() + " | simbolo = " + token.getSimbolo());
			}
		} while (token.getSimbolo() != "Sponto");
		// lexico.TabelaLexema();
		TabelaSintatico();
		TabelaInstrucoes2();
	}

	public void analisadorSintatico1() throws excecaoSintatico, IOException {

		br2 = lexico.InicializadorArquivo();
		token = lexico.AnalisadorEntrada();
		tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
		if (token.getSimbolo().equals(Simbolos.programa)) {
			token = lexico.AnalisadorEntrada();
			tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
			if (token.getSimbolo().equals(Simbolos.identificador)) {
				token = lexico.AnalisadorEntrada();
				tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
				if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {

					analisaBloco();
					token = lexico.AnalisadorEntrada();
					tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
					if (token.getSimbolo().equals(Simbolos.ponto)) {
						token = lexico.AnalisadorEntrada();
						tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));

						if (token.getSimbolo().equals(Simbolos.eof)) {

							////System.out.println("Fim do programa, sucesso");

						} else {
							TabelaSintatico(); //NAO EH o lugar Oficial, so coloquei aqui pra funcionar
							TabelaInstrucoes2();
							throw new excecaoSintatico(
									"Fim do programa, não pode haver mais items. Linha: " + token.getLinha());
						}
					} else {
						TabelaSintatico(); //NAO EH o lugar Oficial, so coloquei aqui pra funcionar
						TabelaInstrucoes2();
						throw new excecaoSintatico(Simbolos.ponto, token.getSimbolo(), token.getLinha());
					}
				} else {
					TabelaSintatico(); //NAO EH o lugar Oficial, so coloquei aqui pra funcionar
					TabelaInstrucoes2();
					throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
				}
			} else {
				TabelaSintatico(); //NAO EH o lugar Oficial, so coloquei aqui pra funcionar
				TabelaInstrucoes2();
				throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
			}
		} else {
			TabelaSintatico(); //NAO EH o lugar Oficial, so coloquei aqui pra funcionar
			TabelaInstrucoes2();
			throw new excecaoSintatico(Simbolos.programa, token.getSimbolo(), token.getLinha());
		}
		TabelaSintatico();
		TabelaInstrucoes2();
	}

	private void analisaBloco() throws excecaoSintatico {
//		token = la.lexical();
//
//		analisaEtVariaveis();
//		analisaSubrotinas();
//		analisaComandos();

	}

	public void TabelaSintatico() {
		// pnlPilha = new MeuJPanel();
		rowDataSintatico = new Vector<Vector>();
		columnNamesSintatico = new Vector<String>();
		// pnlPilha.remove(barraRolagemLexema);
		for (int i = 0; i < tokens.size(); i++) {
			rowLinhaSintatico = new Vector<String>(); // limpa o vector, nao sei se eh o mais correto, pode afetar a
														// memoria fisica
			rowLinhaSintatico.addElement(String.valueOf(tokens.get(i).getLinha()));
			rowLinhaSintatico.addElement(String.valueOf(tokens.get(i).getSimbolo()));
			rowLinhaSintatico.addElement(String.valueOf(tokens.get(i).getLexema()));

			rowDataSintatico.addElement(rowLinhaSintatico);

		}

		columnNamesSintatico.addElement("Linha");
		columnNamesSintatico.addElement("Lexema");
		columnNamesSintatico.addElement("Simbolo");

		tabelaSintatico = new JTable(rowDataSintatico, columnNamesSintatico);
		barraRolagemSintatico = new JScrollPane(tabelaSintatico);
		tabelaSintatico.setPreferredScrollableViewportSize(tabelaSintatico.getPreferredSize());
		tabelaSintatico.setFillsViewportHeight(false);
		pnlTabela.add(barraRolagemSintatico);

	}
	public void TabelaInstrucoes2() throws IOException
	{
		// pnlPilha = new MeuJPanel();
		rowDataInstrucao2 = new Vector<Vector>();
		columnNamesInstrucao2 = new Vector<String>();
		// pnlPilha.remove(barraRolagemLexema);
		String strLine2;
		String[] linha2;
		int nlinha2=0;

		////System.out.println(strLine2);
		while ((strLine2 = br2.readLine()) != null)
		{
			nlinha2++;
			linha2 = strLine2.split(" ");
			rowLinhaInstrucao2 = new Vector<String>();
			rowLinhaInstrucao2.addElement(String.valueOf(nlinha2));
			for (String l : linha2)
			{
				rowLinhaInstrucao2.addElement(String.valueOf(l));
			}
			rowDataInstrucao2.addElement(rowLinhaInstrucao2);
			//System.out.println(rowDataInstrucao2);
		}

		columnNamesInstrucao2.addElement("Linha");
		columnNamesInstrucao2.addElement("Arg1");
		columnNamesInstrucao2.addElement("Arg2");
		columnNamesInstrucao2.addElement("Arg3");
		columnNamesInstrucao2.addElement("Arg4");
		columnNamesInstrucao2.addElement("Arg5");
		columnNamesInstrucao2.addElement("Arg6");
		columnNamesInstrucao2.addElement("Arg7");
		columnNamesInstrucao2.addElement("Arg8");
		columnNamesInstrucao2.addElement("Arg9");
		columnNamesInstrucao2.addElement("Arg10");
		columnNamesInstrucao2.addElement("Arg11");
		columnNamesInstrucao2.addElement("Arg12");
		columnNamesInstrucao2.addElement("Arg13");


		tabelaInstrucao2 = new JTable(rowDataInstrucao2, columnNamesInstrucao2);
		barraRolagemInstrucao2 = new JScrollPane(tabelaInstrucao2);
		tabelaInstrucao2.setPreferredScrollableViewportSize(tabelaInstrucao2.getPreferredSize());
		tabelaInstrucao2.setFillsViewportHeight(false);
		pnlAmostraDados.add(barraRolagemInstrucao2);

	}

}
