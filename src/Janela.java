import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;

import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Janela extends JFrame {
	protected static final long serialVersionUID = 1L;

	protected JButton btnAbrir = new JButton("Abrir"), // carregar arquivo
			btnExecutar = new JButton("Executar"), // Executa
			btnApagar = new JButton("Apagar"), // apagar escritas
			btnDeBug = new JButton("DeBug"), // Escrever debug
			btnContinuar = new JButton("Continuar"), // proxima instruaao
			btnSair = new JButton("Sair");

	// protected MeuJPanel pnlDesenho = new MeuJPanel ();

	protected JLabel statusBar1 = new JLabel("Mensagem:"), statusBar2 = new JLabel("Coordenada:");

	// protected boolean esperaPonto, esperaInicioReta, esperaFimReta;

	// protected Color corAtual = Color.BLACK;
	// protected Ponto p1;

	// protected Vector<Figura> figuras = new Vector<Figura>();
	// protected JPanel painelFundo;
	protected JTable tabelaInstrucoes;
	protected JScrollPane barraRolagemInstrucoes;
	protected JTable tabelaPilha;
	protected JScrollPane barraRolagemPilha;

	String[] colunasInstrucoes = { "Linha", "Instruaao", "Atributo #1", "Atributo #2", "Comentario" };
	String[] colunasPilha = { "Endereco", "Valor" };
	protected enum Liguagem {
    	LDC, LDV,
    	ADD, SUB, MULT, DIVI, INV,
    	AND, OR, NEG,
    	CME, CMA, CEQ, CDIF, CMEQ, CMAG,
    	START, HLT,
    	STR,
    	JMP, JMPF,
    	NULL,
    	RD,
    	PRN,
    	ALLOC,
    	DALLOC,
    	CALL,
    	RETURN;

    }

	protected MeuJPanel pnlTabela = new MeuJPanel();
	protected MeuJPanel pnlEntrada = new MeuJPanel();
	// protected MeuJPanel pnlSaida = new MeuJPanel ();
	// protected MeuJPanel pnlBreakPoint = new MeuJPanel ();
	protected MeuJPanel pnlPilha = new MeuJPanel();
	// painelFundo = new JPanel();

	public Janela() {
		super("Construaao Compiladores");

		try {
			Image btnAbrirImg = ImageIO.read(getClass().getResource("resources/abrir.jpg"));
			btnAbrir.setIcon(new ImageIcon(btnAbrirImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo abrir.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}
		try {
			Image btnExecutarImg = ImageIO.read(getClass().getResource("resources/cores.jpg"));
			btnExecutar.setIcon(new ImageIcon(btnExecutarImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo Executar.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}

		try {
			Image btnApagarImg = ImageIO.read(getClass().getResource("resources/apagar.jpg"));
			btnApagar.setIcon(new ImageIcon(btnApagarImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo Executar.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}

		try {
			Image btnDeBugImg = ImageIO.read(getClass().getResource("resources/circulo.jpg"));
			btnDeBug.setIcon(new ImageIcon(btnDeBugImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo DeBug.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}

		try {
			Image btnContinuarImg = ImageIO.read(getClass().getResource("resources/ponto.jpg"));
			btnContinuar.setIcon(new ImageIcon(btnContinuarImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo Continuar.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}

		try {
			Image btnSairImg = ImageIO.read(getClass().getResource("resources/sair.jpg"));
			btnSair.setIcon(new ImageIcon(btnSairImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo sair.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}

		btnAbrir.addActionListener(new Abrir());
		btnExecutar.addActionListener(new Executar());
		btnApagar.addActionListener(new Apagar());
		btnDeBug.addActionListener(new DeBug());
		btnContinuar.addActionListener(new Continuar());
		btnSair.addActionListener(new Sair());

		JPanel pnlBotoes = new JPanel();
		FlowLayout flwBotoes = new FlowLayout();
		pnlBotoes.setLayout(flwBotoes);

		pnlBotoes.add(btnAbrir);
		pnlBotoes.add(btnExecutar);
		pnlBotoes.add(btnApagar);
		pnlBotoes.add(btnDeBug);
		pnlBotoes.add(btnContinuar);
		pnlBotoes.add(btnSair);

		JPanel pnlStatus = new JPanel();
		GridLayout grdStatus = new GridLayout(1, 2);
		pnlStatus.setLayout(grdStatus);

		pnlStatus.add(statusBar1);
		pnlStatus.add(statusBar2);

		Container cntForm = this.getContentPane();
		cntForm.setLayout(new BorderLayout());
		cntForm.add(pnlBotoes, BorderLayout.NORTH);
		// cntForm.add (pnlDesenho, BorderLayout.CENTER);
		cntForm.add(pnlStatus, BorderLayout.SOUTH);

		GridLayout grdTabela = new GridLayout(0, 1); // tentar arrumar setsize
		pnlTabela.setLayout(grdTabela);
		pnlEntrada.setLayout(grdTabela);

		// pnlSaida.setLayout(grdTabela);
		// pnlBreakPoint.setLayout(grdTabela);
		pnlPilha.setLayout(grdTabela);

		cntForm.add(pnlTabela, BorderLayout.CENTER);
		cntForm.add(pnlEntrada, BorderLayout.WEST);
		cntForm.add(pnlPilha, BorderLayout.EAST);
		// cntForm.add (pnlSaida, BorderLayout.CENTER);
		// cntForm.add (pnlBreakPoint, BorderLayout.CENTER);

		// pnlTabela.setVisible(false);

		this.addWindowListener(new FechamentoDeJanela());

		this.setSize(700, 500);
		this.setVisible(true);
	}

	protected class MeuJPanel extends JPanel implements MouseListener, MouseMotionListener {
		public MeuJPanel() {
			super();

			this.addMouseListener(this);
			this.addMouseMotionListener(this);
		}

		public void mousePressed(MouseEvent e) {
			// interaaao com mouse pressed
		}

		public void mouseMoved(MouseEvent e) {
			statusBar2.setText("Coordenada: " + e.getX() + "," + e.getY());
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub
		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub
		}
	}

	protected class Abrir implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			int posicao = 0, linha = 0;
        	  
        	  String part1, part2, part3, part4, part5;
        	  part1 = part2 = part3 = part4 = part5 = null;
        	  Object [][] dadosInstrucoes = { 
						{"*l*","*i*","*a1*","*a2*","*c*"}
					}; //colocar codigo aquui
        	  JFileChooser fileChooser = new JFileChooser();
        	  int returnValue = fileChooser.showOpenDialog(null);
        	  if (returnValue == JFileChooser.APPROVE_OPTION)
        	  {
        		  
        		  File selectedFile = fileChooser.getSelectedFile();
        		  //System.out.println(selectedFile.getName());
        		  
        		  try{
        			  
        			  // Open the file that is the first 
        			  // command line parameter
        			  FileInputStream fstream = new FileInputStream(selectedFile);
        			  // Get the object of DataInputStream
        			  DataInputStream in = new DataInputStream(fstream);
        			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
        			  String strLine;
        			  //Read File Line By Line
        			  
					  while ((strLine = br.readLine()) != null)
					    {
        				  linha++;
        				  posicao = 0;
        				  // Print the content on the console
        				  // System.out.println (strLine);
        				  if(strLine.length() != posicao) //Ler uma linha vazia, e para seguir lyt da logica
	      				  	{
        					  part1 = String.valueOf(strLine.charAt(posicao));
            				  posicao++;
	      				  	}
        				  while(strLine.length() != posicao && strLine.charAt(posicao) != ' ') //um possivel uso do{}while(strLine.length() != posicao) pode ser mais util
							{ //part1
								part1+=strLine.charAt(posicao); //char by char
								posicao++;
							}
        				  
        				  if(strLine.length() != posicao)
        				  	{
        				  		posicao++;
    							part2 = String.valueOf(strLine.charAt(posicao)); //ultrapassando vallor strLine logica talvez errada ou somente ajusto de posicao
    							posicao++;
        				  	}
        				  while(strLine.length() != posicao && strLine.charAt(posicao) != ' ') //um possivel uso do{}while(strLine.length() != posicao) pode ser mais util
							{ 	//possivel conflito != ou <= posicao
								part2+=strLine.charAt(posicao); //char by char
								posicao++;
							}
        				  
        				  if(strLine.length() != posicao)
	      				  	{
	      				  		posicao++;
	  							part3 = String.valueOf(strLine.charAt(posicao)); //ultrapassando vallor strLine logica talvez errada ou somente ajusto de posicao
	  							posicao++;
	      				  	}
	      				  while(strLine.length() != posicao && strLine.charAt(posicao) != ' ') //um possivel uso do{}while(strLine.length() != posicao) pode ser mais util
							{ 	//possivel conflito != ou <= posicao
								part3+=strLine.charAt(posicao); //char by char
								posicao++;
							}
	      				  
	      				  if(strLine.length() != posicao)
	      				  	{
	      				  		posicao++;
	      				  		part4 = String.valueOf(strLine.charAt(posicao)); //ultrapassando vallor strLine logica talvez errada ou somente ajusto de posicao
	  							posicao++;
	      				  	}
	      				  while(strLine.length() != posicao && strLine.charAt(posicao) != ' ') //um possivel uso do{}while(strLine.length() != posicao) pode ser mais util
		      				{ 	//possivel conflito != ou <= posicao
		      					part4+=strLine.charAt(posicao); //char by char
								posicao++;
		      				}
	      				  
	      				  if(strLine.length() != posicao)
	      				  	{
	      				  		posicao++;
	      				  		part5 = String.valueOf(strLine.charAt(posicao)); //ultrapassando vallor strLine logica talvez errada ou somente ajusto de posicao
	  							posicao++;
	      				  	}
	      				  while(strLine.length() != posicao) //aqui teremos o comentario entao pegamos a sting toda dele sem contar os espa�os
		      				{ 	//possivel conflito != ou <= posicao
		      					part5+=strLine.charAt(posicao); //char by char
								posicao++;
		      				}
							
							//part1 seria Nome da Linha ou Instrução
							//part2 seria
							//part3 seria Atributo1
							//part4 seria Atributo2
							//part5 seria Comentarios
							System.out.print (part1+" ");
							System.out.print (part2+" ");
							System.out.print (part3+" ");
							System.out.print (part4+" ");
							System.out.print (part5+" ");
							System.out.print ("\n");
							//String [][] dadosInstrucoes1;
							String[] LINHA;
							switch (part1)

							{
							case "LDC" : 
								Object [][] dadosInstrucoesLDC = {{String.valueOf(linha),part1,part2,part3,part4+part5}};

								dadosInstrucoes =  dadosInstrucoesLDC; //teria que achar algo do tipo
																//dadosInstrucoes +=  dadosInstrucoes1;
								break;
							case "LDV" : 

									break;
							case "ADD" :
								break;
							case "SUB" :
								break;
							case "MULT" :
								break;
							case "DIVI" :
								break;
							case "INV" :
								break;
							case "AND" :
								break;
							case "OR" :
								break;
							case "NEG" :
								break;
							case "CME" :
								break;
							case "CMA" :
								break;
							case "CEQ" :
								break;
							case "CDIF" :
								break;
							case "CMEQ" :
								break;
							case "CMAG" :
								break;
							case "STR" :
								break;
							case "HLT" :
								break;
							case "JMP" :
								break;
							case "JMPF" :
								break;
							case "NULL" :
								break;
							case "RD" :
								break;
							case "PRN" :
								break;
							case "ALLOC" :
								break;
							case "DALLOC" :
								break;
							case "CALL" :
								break;
							case "RETURN" :
								break;

							
									

							default:
								String [][] dadosInstrucoesD = {{part1,part2,part3,part4,part5}};
								
								
								dadosInstrucoes =  dadosInstrucoesD; //teria que achar algo do tipo
																//dadosInstrucoes +=  dadosInstrucoes1;
								break;
							}
        			 	 }
        			  
        			  //Close the input stream
        			  in.close();
        			    }catch (Exception e1){//Catch exception if any
        			  System.err.println("Error: " + e1.getMessage());
					  }
					  
				tabelaInstrucoes = new JTable(dadosInstrucoes, colunasInstrucoes);
				barraRolagemInstrucoes = new JScrollPane(tabelaInstrucoes);
				tabelaInstrucoes.setPreferredScrollableViewportSize(tabelaInstrucoes.getPreferredSize());
				tabelaInstrucoes.setFillsViewportHeight(false);
				pnlTabela.add(barraRolagemInstrucoes);

				// TABELAPilha
				Object[][] dadosPilha = { { "*E*", "*V*" } }; // colocar codigo aquui

				tabelaPilha = new JTable(dadosPilha, colunasPilha);
				barraRolagemPilha = new JScrollPane(tabelaPilha);
				tabelaPilha.setPreferredScrollableViewportSize(tabelaPilha.getPreferredSize());
				tabelaPilha.setFillsViewportHeight(false);
				pnlPilha.add(barraRolagemPilha);

				// Entrada opcional
				JTextArea textEntrada = new JTextArea(10, 10);
				JScrollPane scrollableTextEntrada = new JScrollPane(textEntrada);
				scrollableTextEntrada.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextEntrada.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				textEntrada.setText("Entrada"); // colocar codigo aquui
				pnlEntrada.add(scrollableTextEntrada);
				// getContentPane().add(pnlEntrada);
				// saida

				JTextArea texSaida = new JTextArea(10, 10);
				JScrollPane scrollableTextSaida = new JScrollPane(texSaida);
				scrollableTextSaida.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextSaida.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				texSaida.setText("SAida"); // colocar codigo aquui
				pnlEntrada.add(scrollableTextSaida);

				JTextArea texBreakPoint = new JTextArea(10, 10);
				JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);
				scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				texBreakPoint.setText("BreakPoint"); // colocar codigo aquui
				pnlEntrada.add(scrollableTextBreakPoint);
				// getContentPane().add(pnlEntrada);

			}

			statusBar1.setText("Mensagem: Arquivo a ser aberto");
		}
	}

	protected class Executar implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			statusBar1.setText("Mensagem: Arquivo a ser Executado");
		}
	}

	protected class Apagar implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			statusBar1.setText("Mensagem: Limpeza Geral");
		}
	}

	protected class DeBug implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			statusBar1.setText("Mensagem: Execucao Passo a Passo");
		}
	}

	protected class Continuar implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			statusBar1.setText("Mensagem: Pressione Continuar");
		}
	}

	protected class Sair implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			System.exit(0);
			statusBar1.setText("Mensagem: Sair");
		}
	}

	protected class FechamentoDeJanela extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}
	}
}
