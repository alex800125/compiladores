import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTable;



public class Compilador extends MaquinaVirtual
{
	protected Vector<String> MLexama = new Vector<String>();
	protected Vector<String> MSimbolo = new Vector<String>();
	
	void AnalisadorEntrada() 
	{
	int nlinha;
	
	String[] linha;
	String[] argumento;
	String[] linhaComentario;
	nlinha = 0;

	JFileChooser fileChooser = new JFileChooser();
	int returnValue = fileChooser.showOpenDialog(null);
	if (returnValue == JFileChooser.APPROVE_OPTION) {

		File selectedFile = fileChooser.getSelectedFile();

		try {
			FileInputStream fstream = new FileInputStream(selectedFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				nlinha++;
							
				Analisador("programa");
			}
		} catch (Exception e1) { // Catch exception if any
			System.err.println("Error: " + e1.getMessage());
		}
		TabelaLexema();
	}

}
	int Analisador(String Lexema)
	{
		switch (Lexema)
		{
		case "programa": // Carregar constante (passado por parametro)
			System.out.println("Sprograma");
			MSimbolo.add("Sprograma");
			break;
		case "inicio": // Carregar constante (passado por parametro)
			System.out.println("Sinicio");
			MSimbolo.add("Sinicio");
			break;
		case "fim": // Carregar constante (passado por parametro)
			System.out.println("Sfim");
			MSimbolo.add("Sfim");
			break;
		case "procedimento": // Carregar constante (passado por parametro)
			System.out.println("Sprocedimento");
			MSimbolo.add("Sprocedimento");
			break;
		case "funcao": // Carregar constante (passado por parametro)
			System.out.println("Sfuncao");
			MSimbolo.add("Sfuncao");
			break;
		case "se": // Carregar constante (passado por parametro)
			System.out.println("Sse");
			MSimbolo.add("Sse");
			break;
		case "entao": // Carregar constante (passado por parametro)
			System.out.println("Sentao");
			MSimbolo.add("Sentao");
			break;
		case "senao": // Carregar constante (passado por parametro)
			System.out.println("Ssenao");
			MSimbolo.add("Ssenao");
			break;
		case "enquanto": // Carregar constante (passado por parametro)
			System.out.println("Senquanto");
			MSimbolo.add("Senquanto");
			break;
		case "faca": // Carregar constante (passado por parametro)
			System.out.println("Sfaca");
			MSimbolo.add("Sfaca");
			break;
		case ":=": // Carregar constante (passado por parametro)
			System.out.println("Satribuição");
			MSimbolo.add("Satribuição");
			break;
		case "escreva": // Carregar constante (passado por parametro)
			System.out.println("Sescreva");
			MSimbolo.add("Sescreva");
			break;
		case "leia": // Carregar constante (passado por parametro)
			System.out.println("Sleia");
			MSimbolo.add("Sleia");
			break;
		case "var": // Carregar constante (passado por parametro)
			System.out.println("Svar");
			MSimbolo.add("Svar");
			break;
		case "inteiro": // Carregar constante (passado por parametro)
			System.out.println("Sinteiro");
			MSimbolo.add("Sinteiro");
			break;
		case "booleano": // Carregar constante (passado por parametro)
			System.out.println("Sbooleano");
			MSimbolo.add("Sbooleano");
			break;
		case "indentificador": // Carregar constante (passado por parametro)
			System.out.println("Sindentificador");
			MSimbolo.add("Sindentificador");
			break;
		case "numero": // Carregar constante (passado por parametro)
			System.out.println("Snúmero");
			MSimbolo.add("Snúmero");
			break;
		case ".": // Carregar constante (passado por parametro)
			System.out.println("Sponto");
			MSimbolo.add("Sponto");
			break;
		case ";": // Carregar constante (passado por parametro)
			System.out.println("Sponto_virgula");
			MSimbolo.add("Sponto_virgula");
			break;
		case ",": // Carregar constante (passado por parametro)
			System.out.println("Svirgula");
			MSimbolo.add("Svirgula");
			break;
		case "(": // Carregar constante (passado por parametro)
			System.out.println("Sabre_parenteses");
			MSimbolo.add("Sabre_parenteses");
			break;
		case ")": // Carregar constante (passado por parametro)
			System.out.println("Sfecha_parenteses");
			MSimbolo.add("Sfecha_parenteses");
			break;
		case ">": // Carregar constante (passado por parametro)
			System.out.println("Smaior");
			MSimbolo.add("Smaior");
			break;
		case ">=": // Carregar constante (passado por parametro)
			System.out.println("Smaiorig");
			MSimbolo.add("Smaiorig");
			break;
		case "=": // Carregar constante (passado por parametro)
			System.out.println("Sig");
			MSimbolo.add("Sig");
			break;
		case "<": // Carregar constante (passado por parametro)
			System.out.println("Smenor");
			MSimbolo.add("Smenor");
			break;
		case "<=": // Carregar constante (passado por parametro)
			System.out.println("Smenorig");
			MSimbolo.add("Smenorig");
			break;
		case "!=": // Carregar constante (passado por parametro)
			System.out.println("Sgif");
			MSimbolo.add("Sgif");
			break;
		case "+": // Carregar constante (passado por parametro)
			System.out.println("Smais");
			MSimbolo.add("Smais");
			break;
		case "-": // Carregar constante (passado por parametro)
			System.out.println("Smenos");
			MSimbolo.add("Smenos");
			break;
		case "*": // Carregar constante (passado por parametro)
			System.out.println("Smult");
			MSimbolo.add("Smult");
			break;
		case "div": // Carregar constante (passado por parametro)
			System.out.println("Sdiv");
			MSimbolo.add("Sdiv");
			break;
		case "e": // Carregar constante (passado por parametro)
			System.out.println("Se");
			MSimbolo.add("Se");
			break;
		case "ou": // Carregar constante (passado por parametro)
			System.out.println("Sou");
			MSimbolo.add("Sou");
			break;
		case "nao": // Carregar constante (passado por parametro)
			System.out.println("Snao");
			MSimbolo.add("Snao");
			break;
		case ":": // Carregar constante (passado por parametro)
			System.out.println("Sdoispontos");
			MSimbolo.add("Sdoispontos");
			break;
			default:
				System.err.println(Lexema);
				break;
		
		}
		return 0;
	}
	public void TabelaLexema() {
		// pnlPilha = new MeuJPanel();
		rowDataLexema = new Vector<Vector>();
		columnNamesLexema = new Vector<String>();
		//pnlPilha.remove(barraRolagemLexema);
		for (int i = 0; i < MSimbolo.size(); i++) {
			rowLinhaLexema = new Vector<String>(); // limpa o vector, nao sei se eh o mais correto, pode afetar a
													// memoria fisica
			rowLinhaLexema.addElement(String.valueOf(i));
			rowLinhaLexema.addElement(String.valueOf(MSimbolo.get(i)));
			rowDataLexema.addElement(rowLinhaLexema);
		}
		System.out.println(MSimbolo);
		columnNamesLexema.addElement("Lexema");
		columnNamesLexema.addElement("Simbolo");
		tabelaLexema = new JTable(rowDataLexema, columnNamesLexema);
		barraRolagemLexema = new JScrollPane(tabelaLexema);
		tabelaLexema.setPreferredScrollableViewportSize(tabelaLexema.getPreferredSize());
		tabelaLexema.setFillsViewportHeight(false);
		pnlPilha.add(barraRolagemLexema);

	}
}
