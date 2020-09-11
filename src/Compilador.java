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
	boolean iscomentario = false;
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
				linha = strLine.split(" ");	
				
				for(int i = 0; i < linha.length; i++)
				{
					if(linha[i].contains("/*") || iscomentario)
					{
						while(linha[i].contains("*/"))
						{
							
							i++;
							if(i == linha.length)
							{
								iscomentario = true;
								break;
							}
						}
					}
					if(linha[i].contains("//"))
					{
						i =  linha.length;
					}
					else 
					{
						Analisador(linha[i],linha);
					}
				}
				
				
				
			}
		} catch (Exception e1) { // Catch exception if any
			System.err.println("Error: " + e1.getMessage());
		}
		TabelaLexema();
	}

}
	int Analisador(String Lexema, String[] linha)
	{
		MLexama.add(Lexema);
		switch (Lexema)
		{
		case "programa": 
			
			MSimbolo.add("Sprograma");
			if(linha.length!=0)
			{
				MLexama.add(linha[1]);
				MSimbolo.add("Sindentificador");
			}
			
			break;
		case "inicio": 

			MSimbolo.add("Sinicio");
			break;
		case "fim": 

			MSimbolo.add("Sfim");
			break;
		case "procedimento": 

			MSimbolo.add("Sprocedimento");
			break;
		case "funcao": 

			MSimbolo.add("Sfuncao");
			break;
		case "se": 

			MSimbolo.add("Sse");
			break;
		case "entao": 

			MSimbolo.add("Sentao");
			break;
		case "senao": 

			MSimbolo.add("Ssenao");
			break;
		case "enquanto": 

			MSimbolo.add("Senquanto");
			break;
		case "faca": 

			MSimbolo.add("Sfaca");
			break;
		case ":=": 

			MSimbolo.add("Satribuição");
			break;
		case "escreva": 

			MSimbolo.add("Sescreva");
			break;
		case "leia": 

			MSimbolo.add("Sleia");
			break;
		case "var": 

			MSimbolo.add("Svar");
			break;
		case "inteiro": 

			MSimbolo.add("Sinteiro");
			break;
		case "booleano": 

			MSimbolo.add("Sbooleano");
			break;
		case "indentificador": 

			MSimbolo.add("Sindentificador");
			break;
		case "numero": 

			MSimbolo.add("Snúmero");
			break;
		case ".": 

			MSimbolo.add("Sponto");
			break;
		case ";": 

			MSimbolo.add("Sponto_virgula");
			break;
		case ",": 

			MSimbolo.add("Svirgula");
			break;
		case "(": 

			MSimbolo.add("Sabre_parenteses");
			break;
		case ")": 

			MSimbolo.add("Sfecha_parenteses");
			break;
		case ">": 

			MSimbolo.add("Smaior");
			break;
		case ">=": 

			MSimbolo.add("Smaiorig");
			break;
		case "=": 

			MSimbolo.add("Sig");
			break;
		case "<": 

			MSimbolo.add("Smenor");
			break;
		case "<=": 

			MSimbolo.add("Smenorig");
			break;
		case "!=": 

			MSimbolo.add("Sgif");
			break;
		case "+": 

			MSimbolo.add("Smais");
			break;
		case "-": 

			MSimbolo.add("Smenos");
			break;
		case "*": 

			MSimbolo.add("Smult");
			break;
		case "div": 

			MSimbolo.add("Sdiv");
			break;
		case "e": 

			MSimbolo.add("Se");
			break;
		case "ou": 

			MSimbolo.add("Sou");
			break;
		case "nao": 

			MSimbolo.add("Snao");
			break;
		case ":": 

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

	}
}
