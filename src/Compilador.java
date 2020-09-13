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
	protected Vector<String> MErro = new Vector<String>();
	
	void AnalisadorEntrada() 
	{
	int nlinha;
	boolean iscomentario = false;
	String[] linha;
	String argumento = null;
	String[] linhaComentario;
	nlinha = 0;
	String strLine = null;

	JFileChooser fileChooser = new JFileChooser();

	int returnValue = fileChooser.showOpenDialog(null);
	if (returnValue == JFileChooser.APPROVE_OPTION) {

		File selectedFile = fileChooser.getSelectedFile();

		try {
			FileInputStream fstream = new FileInputStream(selectedFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			

			while ((strLine = br.readLine()) != null) {
				nlinha++;
				argumento = null;
				
				if(!strLine.equals("")) // linha vazia
				{
					
					linha = strLine.split(" ");	
					for(int i = 0; i < linha.length -1; i++)
					{
						
						if(linha[i].contains("/*") || linha[i].contains("{"))
						{
							
							while(i <= linha.length && (!linha[i].contains("*/") && !linha[i].contains("}") )  ) //nota: tem que verificar sempre se i chega ao final da linha
							{
								//System.out.print(linha[i] + " "); //comentarios iginorados porem depoois dele 
								i++;
							}
						}
						else if(linha[i].contains("//"))
						{	
							
							i =  linha.length-1;
							//System.out.println(linha[i]);
						}
						else
						{
							int loop = i+1;
							do
							{
								if(argumento == null)
								{
									argumento = linha[loop];
								}
								else 
								{
									argumento += " " + linha[loop];
								}
								
								
								loop++;
							}while(loop<=linha.length-1);
							
							Analisador(linha[i],argumento, nlinha);
						}
						
					}
					if(linha.length == 1)
					{
						Analisador(linha[0],null, nlinha);
					}
					
					
					
				}
				
				
				
				
			}
		} catch (Exception e1) { // Catch exception if any
			System.err.println("Error: " + e1.getMessage());
			System.err.println(nlinha);
			System.err.println(strLine);
		}
		TabelaLexema();
	}

}
	boolean AnalisadorComentario(String[] linha)
	{
		
		for(int i = 1; i < linha.length; i++)
		{
		
			
			if(linha[i].contains("/*") || linha[i].contains("{"))
			{
				
				while(i < linha.length && (!linha[i].contains("*/") && !linha[i].contains("}") )  ) //nota: tem que verificar sempre se i chega ao final da linha
				{
					//System.out.print(linha[i] + " "); //comentarios iginorados porem depoois dele 
					i++;
				}
			}
			else if(linha[i].contains("//"))
			{	
				
				i =  linha.length-1;
				return true;
			}
			else 
			{
				//System.out.print(linha[i]);
				return false;
			}
		}
		return true;
	}
	void Analisador(String Lexema, String linha, int nlinha)
	{
		
		switch (Lexema)
		{
		case "programa": 
			MLexama.add(Lexema);
			MSimbolo.add("Sprograma");
			if(linha.contains(";"))
			{
				MLexama.add(String.valueOf(linha.replace(";" , "")));
				MSimbolo.add("Sindentificador");
				Analisador(";",null,0);

			}
			else
			{
				MErro.add(String.valueOf(nlinha));
			}
			
			break;
		case "inicio": 
			MLexama.add(Lexema);
			MSimbolo.add("Sinicio");
			break;
		case "fim": 
			MLexama.add(Lexema);;
			MSimbolo.add("Sfim");
			break;
		case "procedimento": 
			MLexama.add(Lexema);
			MSimbolo.add("Sprocedimento");
			if(linha.contains(";"))
			{
				MLexama.add(String.valueOf(linha.replace(";" , "")));
				MSimbolo.add("Sindentificador");
				Analisador(";",null,0);

			}
			else
			{
				MErro.add(String.valueOf(nlinha));
			}
			break;
		case "funcao": 
			MLexama.add(Lexema);
			MSimbolo.add("Sfuncao");
			break;
		case "se": 
			MLexama.add(Lexema);
			MSimbolo.add("Sse");
			System.out.println();
			if(linha.contains(">"))
			{
				String[] variavel = linha.split(">");
				MLexama.add(variavel[0]);
				MSimbolo.add("Sindentificador");
				Analisador(">",null,0);
				MLexama.add(variavel[1]);
				MSimbolo.add("Sindentificador");
			}
			else if(linha.contains(">="))
			{
				String[] variavel = linha.split(">=");
				MLexama.add(variavel[0]);
				MSimbolo.add("Sindentificador");
				Analisador(">=",null,0);
				MLexama.add(variavel[1]);
				MSimbolo.add("Sindentificador");
			}
			else if(linha.contains("<"))
			{
				String[] variavel = linha.split("<");
				MLexama.add(variavel[0]);
				MSimbolo.add("Sindentificador");
				Analisador("<",null,0);
				MLexama.add(variavel[1]);
				MSimbolo.add("Sindentificador");
			}
			else if(linha.contains("<="))
			{
				String[] variavel = linha.split("<=");
				MLexama.add(variavel[0]);
				MSimbolo.add("Sindentificador");
				Analisador("<=",null,0);
				MLexama.add(variavel[1]);
				MSimbolo.add("Sindentificador");
			}
			else
			{
				MErro.add(String.valueOf(nlinha));
			}
			break;
		case "entao": 
			MLexama.add(Lexema);
			MSimbolo.add("Sentao");
			if(linha.contains(":="))
			{
				String[] variavel = linha.split(":=");
				MLexama.add(variavel[0]);
				MSimbolo.add("Sindentificador");
				Analisador(":=",null,0);
				if(variavel[1].replace(";" , "").replace(" " , "").matches("^[0-9]*$"))
				{
					MLexama.add(variavel[1].replace(";" , ""));
					MSimbolo.add("Snumero");
					Analisador(";",null,0);
				}
				else
				{
					MErro.add(String.valueOf(nlinha));
				}
				
				
			}
			else
			{
				MErro.add(String.valueOf(nlinha));
			}
			break;
		case "senao": 
			MLexama.add(Lexema);
			MSimbolo.add("Ssenao");
			break;
		case "enquanto": 
			MLexama.add(Lexema);
			MSimbolo.add("Senquanto");
			break;
		case "faca": 
			MLexama.add(Lexema);
			MSimbolo.add("Sfaca");
			break;
		case ":=": 
			MLexama.add(Lexema);
			MSimbolo.add("Satribuição");
			break;
		case "escreva": 
			MLexama.add(Lexema);
			MSimbolo.add("Sescreva");
			break;
		case "leia": 
			MLexama.add(Lexema);
			MSimbolo.add("Sleia");
			break;
		case "var": 
			MLexama.add(Lexema);
			MSimbolo.add("Svar");
			if(linha.contains(":") && linha.contains(";") )
			{
				
				String[] variavel = linha.split(":");
				String[] NomeVariavel = variavel[0].split(",");
				for(String a : NomeVariavel)
				{
					MLexama.add(a.replace(":", ""));
					MSimbolo.add("Sindentificador");
					Analisador(",",null,0);
				}
				Analisador(":",null,0);
				Analisador(variavel[1].replace(" ", "").replace(";", ""),null,0);
				Analisador(";",null,0);
			}
			else
			{
				MErro.add(String.valueOf(nlinha));
			}
			
			break;
		case "inteiro": 
			MLexama.add(Lexema);
			MSimbolo.add("Sinteiro");
			break;
		case "booleano": 
			MLexama.add(Lexema);
			MSimbolo.add("Sbooleano");
			break;
		case "indentificador": 
			MLexama.add(Lexema);
			MSimbolo.add("Sindentificador");
			break;
		case "numero": 
			MLexama.add(Lexema);
			MSimbolo.add("Snúmero");
			break;
		case ".": 
			MLexama.add(Lexema);
			MSimbolo.add("Sponto");
			break;
		case ";": 
			MLexama.add(Lexema);
			MSimbolo.add("Sponto_virgula");
			break;
		case ",": 
			MLexama.add(Lexema);
			MSimbolo.add("Svirgula");
			break;
		case "(": 
			MLexama.add(Lexema);
			MSimbolo.add("Sabre_parenteses");
			break;
		case ")": 
			MLexama.add(Lexema);
			MSimbolo.add("Sfecha_parenteses");
			break;
		case ">": 
			MLexama.add(Lexema);
			MSimbolo.add("Smaior");
			break;
		case ">=": 
			MLexama.add(Lexema);
			MSimbolo.add("Smaiorig");
			break;
		case "=": 
			MLexama.add(Lexema);
			MSimbolo.add("Sig");
			break;
		case "<": 
			MLexama.add(Lexema);
			MSimbolo.add("Smenor");
			break;
		case "<=": 
			MLexama.add(Lexema);
			MSimbolo.add("Smenorig");
			break;
		case "!=": 
			MLexama.add(Lexema);
			MSimbolo.add("Sgif");
			break;
		case "+": 
			MLexama.add(Lexema);
			MSimbolo.add("Smais");
			break;
		case "-": 
			MLexama.add(Lexema);
			MSimbolo.add("Smenos");
			break;
		case "*": 
			MLexama.add(Lexema);
			MSimbolo.add("Smult");
			break;
		case "div": 
			MLexama.add(Lexema);
			MSimbolo.add("Sdiv");
			break;
		case "e": 
			MLexama.add(Lexema);
			MSimbolo.add("Se");
			break;
		case "ou": 
			MLexama.add(Lexema);
			MSimbolo.add("Sou");
			break;
		case "nao": 
			MLexama.add(Lexema);
			MSimbolo.add("Snao");
			break;
		case ":": 
			MLexama.add(Lexema);
			MSimbolo.add("Sdoispontos");
			break;
			default:
				//System.err.println(Lexema);
				
				break;
		
		}
		
	}
	public void TabelaLexema() {
		// pnlPilha = new MeuJPanel();
		rowDataLexema = new Vector<Vector>();
		columnNamesLexema = new Vector<String>();
		//pnlPilha.remove(barraRolagemLexema);
		for (int i = 0; i < MSimbolo.size() ; i++) {
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
