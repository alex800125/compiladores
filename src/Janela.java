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

	protected JLabel statusBar1 = new JLabel("Mensagem:"),statusBar2 = new JLabel ("Coordenada:");
	
	protected JTable tabelaInstrucoes;
	protected JScrollPane barraRolagemInstrucoes;
	protected JTable tabelaPilha;
	protected JScrollPane barraRolagemPilha;

	protected String[] colunasPilha = { "Endereco", "Valor" };

	protected String[] Linguagem = { "LDC", "LDV", "ADD", "SUB", "MULT", "DIVI", "INV", "AND", "OR", "NEG", "CME", "CMA", "CEQ",
			"CDIF", "CMEQ", "CMAG", "START", "HLT", "STR", "JMP", "JMPF", "NULL", "RD", "PRN", "ALLOC", "DALLOC",
			"CALL", "RETURN" };
	
	protected int S;
	protected int Ji;

	protected Vector<String> rowLinha = new Vector<String>();
	protected Vector<Vector> rowData = new Vector<Vector>();
	protected Vector<String> columnNames = new Vector<String>();
	protected Vector<Integer> M = new Vector<Integer>(); //pilha ou  logcia pdf
	
	protected MeuJPanel pnlTabela = new MeuJPanel();
	protected MeuJPanel pnlAmostraDados = new MeuJPanel();
	protected MeuJPanel pnlPilha = new MeuJPanel();

	
	protected JTextArea textEntrada = new JTextArea(10, 10);
	protected JTextArea texSaida = new JTextArea(10, 10);
	protected JTextArea texBreakPoint = new JTextArea(10, 10);

	public Janela() {
		super("Construcao Compiladores");

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
		cntForm.add(pnlStatus, BorderLayout.SOUTH);
		
		
		
		GridLayout grdTabela = new GridLayout(0, 1); // tentar arrumar setsize
		pnlTabela.setLayout(grdTabela);
		pnlAmostraDados.setLayout(grdTabela);
		pnlPilha.setLayout(grdTabela);

		cntForm.add(pnlTabela, BorderLayout.CENTER);
		cntForm.add(pnlAmostraDados, BorderLayout.WEST);
		cntForm.add(pnlPilha, BorderLayout.EAST);
		
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
			 statusBar2.setText("Coordenada: "+e.getX()+","+e.getY());
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
			String[] linha;
			String[] argumento;
			String[] linhaComentario;

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
						int posicao = 0;
						int posicaoLc = 0;
						boolean isInstruction = false;
						String comentario = "";
						rowLinha = new Vector<String>(); // representa cada linha da tabela

						linha = strLine.split(" "); // separa a linha pra cada espaco que existe

						for (String l : linha) {
							// se é o primeiro item, verifica se é uma linha ou uma instrucao
							if (posicao == 0) {
								for (int i = 0; i < Linguagem.length; i++) {
									if (Linguagem[i].equals(l)) {
										isInstruction = true;
									}
								}
								// se for uma instrucao, deixa o espaco na tabela da linha em branco
								if (isInstruction) {
									rowLinha.add("");
									rowLinha.add(l);
									posicao++;
								} else {
									rowLinha.add(l);
								}
							} else {
								if (l.contains(",")) {
									argumento = l.split(",");
									for (String a : argumento) {
										rowLinha.add(a);
									}
									posicao++;
								} else if (l.contains(";")) {
									comentario = "";
									if (posicao == 1) {
										rowLinha.add("");
										rowLinha.add("");
										rowLinha.add("");

										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;
											
											posicaoLc++;
										}
										rowLinha.add(comentario);
									}
									if (posicao == 2) {
										rowLinha.add("");
										rowLinha.add("");
										
										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;
											
											posicaoLc++;
										}
										rowLinha.add(comentario);
									}
									if (posicao == 3) {
										rowLinha.add("");

										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;
											
											posicaoLc++;
										}
										rowLinha.add(comentario);
									}
									if (posicao == 4) {
										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;
											
											posicaoLc++;
										}
										rowLinha.add(comentario);
									}
								} else {
									rowLinha.add(l);
								}

							}
							posicao++;
						}

						rowData.addElement(rowLinha); // adicionar ao Data da tabela a linha com os itens

						System.out.println("Linha = " + strLine + " | posicao = " + posicao);
					}

					in.close();
				} catch (Exception e1) { // Catch exception if any
					System.err.println("Error: " + e1.getMessage());
				}

				// Adiciona os campos da tabela
				columnNames.addElement("Linha");
				columnNames.addElement("Instrucao");
				columnNames.addElement("Atributo #1");
				columnNames.addElement("Atributo #2");
				columnNames.addElement("Comentario");

				// Cria a tabela e insere as colunas e os Dados previamente preenchidos
				tabelaInstrucoes = new JTable(rowData, columnNames);
				barraRolagemInstrucoes = new JScrollPane(tabelaInstrucoes);
				tabelaInstrucoes.setPreferredScrollableViewportSize(tabelaInstrucoes.getPreferredSize());
				tabelaInstrucoes.setFillsViewportHeight(false);
				pnlTabela.add(barraRolagemInstrucoes);

				// Tabela referente a pilha
				Object[][] dadosPilha = { { "*E*", "*V*" } }; // colocar resultados das operações aqui

				tabelaPilha = new JTable(dadosPilha, colunasPilha);
				barraRolagemPilha = new JScrollPane(tabelaPilha);
				tabelaPilha.setPreferredScrollableViewportSize(tabelaPilha.getPreferredSize());
				tabelaPilha.setFillsViewportHeight(false);
				pnlPilha.add(barraRolagemPilha);
				
				// Entrada opcional
				
				JScrollPane scrollableTextEntrada = new JScrollPane(textEntrada);
				scrollableTextEntrada.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextEntrada.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				textEntrada.setText("Entrada"); // colocar codigo aquui
				pnlAmostraDados.add(scrollableTextEntrada);
				// getContentPane().add(pnlEntrada);
				// saida

				
				JScrollPane scrollableTextSaida = new JScrollPane(texSaida);
				scrollableTextSaida.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextSaida.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				texSaida.setText("SAida"); // colocar codigo aquui
				pnlAmostraDados.add(scrollableTextSaida);

				
				JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);
				scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
				scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				texBreakPoint.setText("BreakPoint"); // colocar codigo aquui
				pnlAmostraDados.add(scrollableTextBreakPoint);
				// getContentPane().add(pnlEntrada);

			

			}

			statusBar1.setText("Mensagem: Arquivo a ser aberto");
		}
	}

	protected class Executar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			
			ExecucaoCompilador EC = new ExecucaoCompilador();
			//EC.InstrucaoLinha("LDC", S, 0, -999);
			//EC.InstrucaoLinha("LDV", S, -999, 0);
			/*
			S = S+1;
			M.add(S,0);
			S = S+1;
			M.add(S, M.get(S-1));
			*/
			System.out.println(M);
			statusBar1.setText("Mensagem: Arquivo a ser Executado");
		}
	}

	protected class Apagar implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			statusBar1.setText("Mensagem: Limpeza Geral");
		}
	}

	protected class DeBug implements ActionListener {
		public void actionPerformed(ActionEvent e) 
		{		
		
		}
	}

	protected class Continuar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			S = 0;
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
	protected class ExecucaoCompilador {
		boolean isnumber=false;
		public void InstrucaoLinha(String Instrucao, int S, int k, int n, int m, int t) 
		{
			int valor;
			switch (Instrucao)
			{
			case "LDC": //Carregar constante
				S = S + 1;
				M.add(S, (int)k);
				break;
			case "LDV": //Carregar valor
				S = S + 1;
				valor = (int)M.get(n);
				M.add( S, M.get(n) );
				break;
			case "ADD":
				valor = (int)M.get(S-1) + (int)M.get(S) ; 
				M.add( (S-1), (int)valor);
				S = S - 1;
				break;
			case "SUB":
				valor = (int)M.get(S-1) - (int)M.get(S) ; 
				M.add( (S-1), (int)valor);
				S = S - 1;
				break;
			case "MULT":
				valor = (int)M.get(S-1) * (int)M.get(S) ; 
				M.add( (S-1), (int)valor);
				S = S - 1;
				break;
			case "DIVI":
				valor = (int) M.get(S-1) / (int) M.get(S) ; 
				M.add( (S-1), (int)valor);
				S = S - 1;
				break;
			case "INV":
				valor = (int) M.get(S) * -1; 
				M.add(S, (int)valor);
				break;
			case "AND":
				if(M.get(S-1).equals(1) && M.get(S).equals(1) )
				{
					M.add( (S-1), (int)1);
				}
				else 
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "OR":
				if(M.get(S-1).equals(1) || M.get(S).equals(1) )
				{
					M.add( (S-1), (int)1);
				}
				else 
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "NEG":
				valor =  1 - (int)M.get(S); 
				M.add(S, (int)valor);
				break;
			case "CME":
				if(M.get(S-1) < M.get(S) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "CMA":
				if(M.get(S-1) > M.get(S) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "CEQ":
				if(M.get(S-1).equals(M.get(S)) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "CDIF":
				if(! M.get(S-1).equals(M.get(S)) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "CMEQ":
				if(M.get(S-1) <= M.get(S) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "CMAG":
				if(M.get(S-1) >= M.get(S) )
				{
					M.add( (S-1), (int)1);
				}
				else
				{
					M.add( (S-1), (int)0);
				}
				S = S - 1;
				break;
			case "START":
				S = - 1;
				break;
			case "HLT":				
				break;
			case "STR":
				valor = (int)M.get(S);
				M.add(n,valor);
				
				S = S - 1;
				break;
			case "JMP":
				Ji = t; //talvez nao seja so isso
				//aparentemente tem algo a mais pra fazer aqui
				break;
			case "JMPF":
				if( M.get(S).equals(0))
				{
					Ji = t;
				}
				else 
				{
					Ji = Ji + 1;
				}
				S = S - 1;
				break;
			case "NULL":
				//nada????
				break;
			case "RD":
				S = S + 1;//desemvolver interface
				int Entrada = 0;
				do {
						try
						{
							String x = javax.swing.JOptionPane.showInputDialog("Digite Somente Numeros");
							Entrada = Integer.parseInt(x); 
							isnumber = true;
						}
						catch(NumberFormatException  e1)
						{  
					     System.out.println(e1);    
					    }
				}while(!isnumber);
				textEntrada.setText(Entrada + "\n"+ textEntrada.getText());
				M.add(S, Entrada);
				break;
			case "PRN":
				texSaida.setText((M.get(S)) + "\n"+ texSaida.getText());
				//interface
				S = S - 1;
				break;
			case "ALLOC":
				
				for(k = 0; k < n-1; k++)
				{
					S = S + 1;
					valor = (int)M.get(m+k);  //pode dar problema no tamanho de M[m+k], pegar posiçao que nao exite
					M.add(S, valor);
				}

				break;
			case "DALLOC":
				for(k = n-1; k  > 0; k--)
				{
					
					valor = (int)M.get(S);  
					M.add((m+k), valor);
//aqui pode-se remover o conteudo da pilha OBRIGATORIO??
					S = S - 1;
				}
				break;
			case "CALL":
				S = S + 1;
				valor = Ji + 1;
				M.add(S, valor);
				Ji = t;
				break;
			case "RETURN":
				Ji = M.get(S);
				S = S - 1;
				break;
			default:
				//seria um ComentARIO?;
				break;
			}
			
			statusBar1.setText("Executando "+ Instrucao);
		}
	}
}
