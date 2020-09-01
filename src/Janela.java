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

	protected JLabel statusBar1 = new JLabel("Mensagem:"), statusBar2 = new JLabel("Coordenada:");

	protected JTable tabelaInstrucoes;
	protected JScrollPane barraRolagemInstrucoes;
	protected JTable tabelaPilha;
	protected JScrollPane barraRolagemPilha;

	protected String[] Linguagem = { "LDC", "LDV", "ADD", "SUB", "MULT", "DIVI", "INV", "AND", "OR", "NEG", "CME",
			"CMA", "CEQ", "CDIF", "CMEQ", "CMAG", "START", "HLT", "STR", "JMP", "JMPF", "NULL", "RD", "PRN", "ALLOC",
			"DALLOC", "CALL", "RETURN" };

	protected int nlinha;
	protected int S;
	protected int Ji;

	protected Vector<String> rowLinhaInstrucao = new Vector<String>();
	protected Vector<Vector> rowDataInstrucao = new Vector<Vector>();
	protected Vector<String> columnNamesInstrucao = new Vector<String>();
	protected Vector<String> rowLinhaPilha = new Vector<String>();
	protected Vector<Vector> rowDataPilha = new Vector<Vector>();
	protected Vector<String> columnNamesPilha = new Vector<String>();

	protected Vector<Integer> M = new Vector<Integer>(); // pilha
	protected Vector<Integer> ChamadasCall = new Vector<Integer>(); // pilha

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
						int posicao = 0;
						int posicaoLc = 0;
						boolean isInstruction = false;
						String comentario = "";
						rowLinhaInstrucao = new Vector<String>(); // representa cada linha da tabela

						linha = strLine.split(" "); // separa a linha pra cada espaco que existe

						for (String l : linha) {
							// se � o primeiro item, verifica se � uma linha ou uma instrucao
							if (posicao == 0) {
								for (int i = 0; i < Linguagem.length; i++) {
									if (Linguagem[i].equals(l)) {
										isInstruction = true;
									}
								}
								// se for uma instrucao, deixa o espaco na tabela da linha em branco
								if (isInstruction) {
									rowLinhaInstrucao.add("");
									rowLinhaInstrucao.add(l);
									posicao++;
								} else {
									rowLinhaInstrucao.add(l);
								}
							} else {
								if (l.contains(",")) {
									argumento = l.split(",");
									for (String a : argumento) {
										rowLinhaInstrucao.add(a);
									}
									posicao++;
								} else if (l.contains(";")) {
									comentario = "";
									if (posicao == 1) {
										rowLinhaInstrucao.add("");
										rowLinhaInstrucao.add("");
										rowLinhaInstrucao.add("");

										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;

											posicaoLc++;
										}
										rowLinhaInstrucao.add(comentario);
									}
									if (posicao == 2) {
										rowLinhaInstrucao.add("");
										rowLinhaInstrucao.add("");

										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;

											posicaoLc++;
										}
										rowLinhaInstrucao.add(comentario);
									}
									if (posicao == 3) {
										rowLinhaInstrucao.add("");

										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;

											posicaoLc++;
										}
										rowLinhaInstrucao.add(comentario);
									}
									if (posicao == 4) {
										linhaComentario = strLine.split(";");
										for (String lc : linhaComentario) {
											if (posicaoLc != 0)
												comentario = comentario + lc;

											posicaoLc++;
										}
										rowLinhaInstrucao.add(comentario);
									}
								} else {
									rowLinhaInstrucao.add(l);
								}

							}
							
							posicao++;
						}
						
						rowDataInstrucao.addElement(rowLinhaInstrucao); // adicionar ao Data da tabela a linha com os
																		// itens
						//System.out.println(rowDataInstrucao);
						// System.out.println("Linha = " + strLine + " | posicao = " + posicao);
					}

					in.close();
				} catch (Exception e1) { // Catch exception if any
					System.err.println("Error: " + e1.getMessage());
				}

				InterfaceAbrir();
			}

			statusBar1.setText("Mensagem: Arquivo a ser aberto");
		}

		void InterfaceAbrir() {
			// Adiciona os campos da tabela
			columnNamesInstrucao.addElement("Linha");
			columnNamesInstrucao.addElement("Instrucao");
			columnNamesInstrucao.addElement("Atributo #1");
			columnNamesInstrucao.addElement("Atributo #2");
			columnNamesInstrucao.addElement("Comentario");
			columnNamesPilha.addElement("Endereco");
			columnNamesPilha.addElement("Valor1");

			// Cria a tabela e insere as colunas e os Dados previamente preenchidos
			tabelaInstrucoes = new JTable(rowDataInstrucao, columnNamesInstrucao);
			barraRolagemInstrucoes = new JScrollPane(tabelaInstrucoes);
			tabelaInstrucoes.setPreferredScrollableViewportSize(tabelaInstrucoes.getPreferredSize());
			tabelaInstrucoes.setFillsViewportHeight(false);
			pnlTabela.add(barraRolagemInstrucoes);

			// Tabela referente a pilha
			rowDataPilha.add(rowLinhaPilha);
			tabelaPilha = new JTable(rowDataPilha, columnNamesPilha);
			barraRolagemPilha = new JScrollPane(tabelaPilha);
			tabelaPilha.setPreferredScrollableViewportSize(tabelaPilha.getPreferredSize());
			tabelaPilha.setFillsViewportHeight(false);
			pnlPilha.add(barraRolagemPilha);

			// Entrada opcional

			JScrollPane scrollableTextEntrada = new JScrollPane(textEntrada);
			scrollableTextEntrada.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollableTextEntrada.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			textEntrada.setText("Entrada"); // colocar codigo aqui
			pnlAmostraDados.add(scrollableTextEntrada);

			// saida

			JScrollPane scrollableTextSaida = new JScrollPane(texSaida);
			scrollableTextSaida.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollableTextSaida.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			texSaida.setText("Saida"); // colocar codigo aqui
			pnlAmostraDados.add(scrollableTextSaida);

			// break
			JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);
			scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			texBreakPoint.setText("BreakPoint"); // colocar codigo aquui
			pnlAmostraDados.add(scrollableTextBreakPoint);
		}
	}

	protected class Executar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			ChamadasCall = new Vector<Integer>();
			ExecucaoCompilador EC = new ExecucaoCompilador();
			
			int totalLinhasCodigo = tabelaInstrucoes.getRowCount();
			int TopoPilha = 0;
			int valorEntrada = 0;
			int valorReturn = 0;
			boolean isNumber = false;
			//System.out.println("total de linhas: " + totalLinhasCodigo);

			for (int i = 0; i < totalLinhasCodigo; i++) {
				String instrucao = (String) tabelaInstrucoes.getModel().getValueAt(i, 1);
				String atributo_1 = (String) tabelaInstrucoes.getModel().getValueAt(i, 2);
				String atributo_2 = (String) tabelaInstrucoes.getModel().getValueAt(i, 3);

				// Finaliza o programa
				if (instrucao.equals("HLT"))
				{
					System.out.println("HTL");
					// Ver o que deve ser feito quando isso acontece (acho que finaliza o programa)
					// Futuramente remover esses comandos desses IFs do
					// ExecucaoCompilador.InstrucaoLinha
					break;

				} else if (instrucao.equals("JMP") || instrucao.equals("JMPF") || (instrucao.equals("CALL")))
				{

					for (int t = 0; t < totalLinhasCodigo; t++) {
						String linha = (String) tabelaInstrucoes.getModel().getValueAt(t, 0);
						
						if (atributo_1.equals(linha))
						{
							if((instrucao.equals("CALL")))
							{
								ChamadasCall.add(i);
								
							}
							i = t; //loo�p
							
						}
					}

				} else if(instrucao.equals("RETURN"))
				{
					ChamadasCall.size();
					i = ChamadasCall.get((ChamadasCall.size()-1));
					ChamadasCall.remove((ChamadasCall.size()-1));
					
				}else{
					System.out.println("instrucao = " + instrucao + " | atributo_1 = " + atributo_1 + " | atributo_2 = "
							+ atributo_2 + " | TopoPilha = " + TopoPilha);
					TopoPilha = EC.InstrucaoLinha(instrucao, atributo_1, atributo_2, TopoPilha, 0);

					// Precisamos trabalhar primeiro nessa parte, sobre o que fazer com o topo da
					// Pilha
				}
				
				//System.out.println(M);
				EC.AtualizarPilha(TopoPilha);
			}

			
			
			// n�o sabia o que era essas coisas aqui em baixo, deixei comentado

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
//			try {
//				for (int i = 0; i < nlinha; i++) {
//					Thread.sleep(1000);// seria um pause na interface n�o deu certo
//					tabelaInstrucoes.setRowSelectionInterval(i, i);
//				}
//			} catch (InterruptedException ie) {
//				Thread.currentThread().interrupt();
//			}
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
		boolean isNumber = false;

		public int InstrucaoLinha(String Instrucao, String PrimeiroAtributo, String SegundoAtributo, int TopoPilha,
				int linhaJump) {
			int valor, k = 0;

			statusBar1.setText("Executando " + Instrucao);

			switch (Instrucao) {
			case "LDC": // Carregar constante
				TopoPilha = TopoPilha + 1;
				M.add(TopoPilha, (int) k);
				break;

			case "LDV": // Carregar valor
				TopoPilha = TopoPilha + 1;
				valor = (int) M.get(Integer.parseInt(PrimeiroAtributo));
				M.set(TopoPilha, M.get(Integer.parseInt(PrimeiroAtributo)));
				break;

			case "ADD":
				valor = (int) M.get(TopoPilha - 1) + (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), (int) valor);
				TopoPilha = TopoPilha - 1;
				break;

			case "SUB":
				valor = (int) M.get(TopoPilha - 1) - (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), (int) valor);
				TopoPilha = TopoPilha - 1;
				break;

			case "MULT":
				valor = (int) M.get(TopoPilha - 1) * (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), (int) valor);
				TopoPilha = TopoPilha - 1;
				break;

			case "DIVI":
				valor = (int) M.get(TopoPilha - 1) / (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), (int) valor);
				TopoPilha = TopoPilha - 1;
				break;

			case "INV":
				valor = (int) M.get(TopoPilha) * -1;
				M.set(TopoPilha, (int) valor);
				break;

			case "AND":
				if (M.get(TopoPilha - 1).equals(1) && M.get(TopoPilha).equals(1)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "OR":
				if (M.get(TopoPilha - 1).equals(1) || M.get(TopoPilha).equals(1)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "NEG":
				valor = 1 - (int) M.get(TopoPilha);
				M.set(TopoPilha, (int) valor);
				break;

			case "CME":
				if (M.get(TopoPilha - 1) < M.get(TopoPilha)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "CMA":
				if (M.get(TopoPilha - 1) > M.get(TopoPilha)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "CEQ":
				if (M.get(TopoPilha - 1).equals(M.get(TopoPilha))) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "CDIF":
				if (!M.get(TopoPilha - 1).equals(M.get(TopoPilha))) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "CMEQ":
				if (M.get(TopoPilha - 1) <= M.get(TopoPilha)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "CMAG":
				if (M.get(TopoPilha - 1) >= M.get(TopoPilha)) {
					M.set((TopoPilha - 1), (int) 1);
				} else {
					M.set((TopoPilha - 1), (int) 0);
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "START":
				TopoPilha = -1;
				break;

			// TODO talvez tenha q retirar daqui
			case "HLT":
				break;

			case "STR":
				valor = (int) M.get(TopoPilha);
				M.set(Integer.parseInt(PrimeiroAtributo), valor);
				
				TopoPilha = TopoPilha - 1;
				break;

			// TODO talvez tenha q retirar daqui
			case "JMP":
				Ji = linhaJump; // talvez nao seja so isso
				// aparentemente tem algo a mais pra fazer aqui
				break;

			// TODO talvez tenha q retirar daqui
			case "JMPF":
				if (M.get(TopoPilha).equals(0)) {
					Ji = linhaJump;
				} else {
					Ji = Ji + 1;
				}
				TopoPilha = TopoPilha - 1;
				break;

			case "NULL":
				// nada????
				break;

			case "RD":
				TopoPilha = TopoPilha + 1;
				int Entrada = 0;
				do {
					try {
						String x = javax.swing.JOptionPane.showInputDialog("Digite Somente Numeros");
						Entrada = Integer.parseInt(x);
						isNumber = true;
					} catch (NumberFormatException e1) {
						System.out.println(e1);
					}
				} while (!isNumber);
				textEntrada.setText(Entrada + "\n" + textEntrada.getText());
				M.add(TopoPilha, Entrada);
				break;

			case "PRN":
				texSaida.setText((M.get(TopoPilha)) + "\n" + texSaida.getText());
				// interface
				TopoPilha = TopoPilha - 1;
				break;

			case "ALLOC":

				for (k = 0; k <= Integer.parseInt(SegundoAtributo) - 1; k++) {
					TopoPilha = TopoPilha + 1;
					M.add(TopoPilha, -999); //atualmente -999 � referente a lixo;
				}
				break;

			case "DALLOC":
				for (k = Integer.parseInt(SegundoAtributo) - 1; k >= 0; k--) {

					valor = (int) M.get(TopoPilha);
					M.set((Integer.parseInt(PrimeiroAtributo) + k), valor);
					// aqui pode-se remover o conteudo da pilha OBRIGATORIO??
					TopoPilha = TopoPilha - 1;
				}
				break;

			case "CALL":
				TopoPilha = TopoPilha + 1;
				valor = Ji + 1;
				M.set(TopoPilha, valor);
				Ji = linhaJump;
				break;

			case "RETURN":
				Ji = M.get(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			default:
				System.out.println("Intrucao nao reconhecida = " + Instrucao);
				break;
			}

			//System.out.println("TopoPilha = " + TopoPilha);
			return TopoPilha;
		}
		public void AtualizarPilha(int TopoPilha) 
		{
			pnlPilha = new MeuJPanel();
			rowDataPilha = new Vector<Vector>();
			columnNamesPilha = new Vector<String>();
			for(int i = 0; i < M.size();i++)
			{				
				rowLinhaPilha = new Vector<String>(); //limpa o vector, nao sei se eh o mais correto, pode afetar a memoria fisica
				rowLinhaPilha.addElement(String.valueOf(i));
				rowLinhaPilha.addElement(String.valueOf(M.get(i)));
				rowDataPilha.addElement(rowLinhaPilha);
			}
			System.out.println(rowDataPilha);
			columnNamesPilha.addElement("Endereco");
			columnNamesPilha.addElement("Valor1");
			tabelaPilha = new JTable(rowDataPilha, columnNamesPilha);
			barraRolagemPilha = new JScrollPane(tabelaPilha);
			tabelaPilha.setPreferredScrollableViewportSize(tabelaPilha.getPreferredSize());
			tabelaPilha.setFillsViewportHeight(false);
			pnlPilha.add(barraRolagemPilha);
			
		}
	}
	
}
