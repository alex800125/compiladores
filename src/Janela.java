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

	String[] colunasPilha = { "Endereco", "Valor" };

	String[] Linguagem = {
		"LDC", "LDV", "ADD", "SUB", "MULT", "DIVI", "INV", "AND", "OR", "NEG", "CME", 
		"CMA", "CEQ", "CDIF", "CMEQ", "CMAG", "START", "HLT", "STR", "JMP", "JMPF", "NULL", 
		"RD", "PRN", "ALLOC", "DALLOC", "CALL", "RETURN"
	};

	Vector<String> rowLinha = new Vector<String>();
	Vector<Vector> rowData = new Vector<Vector>();
	Vector<String> columnNames = new Vector<String>();

	protected MeuJPanel pnlTabela = new MeuJPanel();
	protected MeuJPanel pnlEntrada = new MeuJPanel();
	protected MeuJPanel pnlPilha = new MeuJPanel();

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
		pnlEntrada.setLayout(grdTabela);

		pnlPilha.setLayout(grdTabela);

		cntForm.add(pnlTabela, BorderLayout.CENTER);
		cntForm.add(pnlEntrada, BorderLayout.WEST);
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
						boolean isInstruction = false;
						rowLinha = new Vector<String>(); // representa cada linha da tabela
						
						linha = strLine.split(" "); // separa a linha pra cada espaco que existe

						for (String l : linha) {
							// se é o primeiro item, verifica se é uma linha ou uma instrucao
							if (posicao == 0) {
								for(int i = 0; i < Linguagem.length; i++){
									if (Linguagem[i].equals(l)) {
										isInstruction = true;
									}
								}
								// se for uma instrucao, deixa o espaco na tabela da linha em branco
								if (isInstruction) {
									rowLinha.add("");
									rowLinha.add(l);
								} else {
									rowLinha.add(l);
								}
							} else {
								rowLinha.add(l);
							}
							posicao++;
						}
						
						rowData.addElement(rowLinha); // adicionar ao Data da tabela a linha com os itens

						System.out.println("Linha = " + strLine);
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
