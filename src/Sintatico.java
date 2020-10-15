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
	Token token = null;
	protected Vector<Token> tokens = new Vector<Token>();

	public void analisadorSintatico() {
		Lexico lexico = new Lexico();
		lexico.InicializadorArquivo();

		do {
			try {
				token = lexico.AnalisadorEntrada();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if (token.getLexema().equals("ERRO")) {
				System.out.println("Token ERRO");
				break;
			} else {
				tokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
				System.out.println("Token lexema = " + token.getLexema() + " | simbolo = " + token.getSimbolo());
			}
		} while (token.getSimbolo() != "Sponto");
		// lexico.TabelaLexema();
		TabelaSintatico();
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

		/*
		 * texBreakPoint = new JTextArea(10, 15); JScrollPane scrollableTextBreakPoint =
		 * new JScrollPane(texBreakPoint);
		 * scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.
		 * HORIZONTAL_SCROLLBAR_ALWAYS);
		 * scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.
		 * VERTICAL_SCROLLBAR_ALWAYS); texBreakPoint.setText("aaa"); // colocar codigo
		 * aquui pnlAmostraDados.add(scrollableTextBreakPoint);
		 */
	}

}
