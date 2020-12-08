import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.*;
import java.io.*;
import java.util.*;
import java.awt.GridLayout;
import java.util.concurrent.TimeUnit;

import Excecoes.excecaoSemantico;
import Excecoes.excecaoSintatico;

public class MaquinaVirtual extends JFrame {
	protected static final long serialVersionUID = 1L;

	protected JButton btnAbrirAssembly = new JButton("Abrir Assembly"), // carregar arquivo
			btnExecutarAssembly = new JButton("Executar Assembly"), // Executa
			btnApagar = new JButton("Limpar Tela"), // apagar escritas
			btnDeBug = new JButton("Debug"), // Escrever debug
			btnContinuar = new JButton("Continuar"), // proxima instrucao
			btnSair = new JButton("Sair"), // Sair
			btnAbrirCodigo = new JButton("Abrir Codigo"), btnExecutarCodigo = new JButton("Executar Codigo"),
			btnNovoSalvarExecutar = new JButton(" Salvar e Executar"),
			bntAbrirCodigoGerado = new JButton("Abrir Codigo Gerado"),
			bntAbreVariosTestesCompilador = new JButton("Abre varios testes de Compilador"),
			bntAbreVariosTestesAssembly = new JButton("Abre varios testes de Assembly");

	protected JLabel statusBar1 = new JLabel("Mensagem:"), statusBar2 = new JLabel("Coordenada:");
	// VARIVEIS USADAS PARA COMPLEMENTAR A INTERFACE DAS TABELAS
	protected JTable tabelaInstrucoes;
	protected JScrollPane barraRolagemInstrucoes;
	protected JTable tabelaPilha;
	protected JScrollPane barraRolagemPilha;
	protected JTable tabelaLexema;
	protected JScrollPane barraRolagemLexema;
	protected JTable tabelaSintatico;
	protected JScrollPane barraRolagemSintatico;
	protected JTable tabelaInstrucao2;
	protected JScrollPane barraRolagemInstrucao2;
	protected JTable tabelaInstrucaoLOCAL;
	protected JScrollPane barraRolagemInstrucaoLOCAL;
	protected JTable tabelaSintaticoLOCAL;
	protected JScrollPane barraRolagemSintaticoLOCAL;

	// Variaveis usadas na MAQUINA VIRTUAL
	protected String[] Linguagem = { "LDC", "LDV", "ADD", "SUB", "MULT", "DIVI", "INV", "AND", "OR", "NEG", "CME",
			"CMA", "CEQ", "CDIF", "CMEQ", "CMAQ", "START", "HLT", "STR", "JMP", "JMPF", "NULL", "RD", "PRN", "ALLOC",
			"DALLOC", "CALL", "RETURN" };

	// Uso Global
	public static String strLine;
	public static int returnValue;
	public static JFileChooser fileChooser;
	public static File selectedFile;
	public static BufferedReader br;
	public static BufferedReader br2;
	public static BufferedReader brLOCAL;
	public static FileInputStream fstream;
	public static DataInputStream in;
	public static FileInputStream fstream2;
	public static DataInputStream in2;
	public static FileInputStream fstreamLOCAL;
	public static DataInputStream inLOCAL;
	public static int nlinha;
	public static String strLine2;
	public static String strLineLOCAL;
	public static int nlinha2;
	public static int nlinhaLOCAL;
	public static Vector<Token> vetorTokens;
	public static String ErroDoTryCath;
	public static Color CorDoFundo;
	public static String NomeDoArquivoTXT;
	public static JScrollPane scrollableTextErroSintatico;
	protected String[] linha;
	protected String[] argumento;
	protected String[] linhaComentario;
	protected JFileChooser fileChooserParaAssembly;
	protected File[] files;
	
	protected int PosicaoFunção;

	protected int S;
	protected int Ji;
	// VECTORES USADAS PARA COMPLEMENTAR A INTERFACE DAS TABELAS
	protected Vector<String> rowLinhaInstrucao = new Vector<String>();
	protected Vector<Vector> rowDataInstrucao = new Vector<Vector>();
	protected Vector<String> columnNamesInstrucao = new Vector<String>();
	protected Vector<String> rowLinhaPilha = new Vector<String>();
	protected Vector<Vector> rowDataPilha = new Vector<Vector>();
	protected Vector<String> columnNamesPilha = new Vector<String>();
	protected Vector<String> rowLinhaLexema = new Vector<String>();
	protected Vector<Vector> rowDataLexema = new Vector<Vector>();
	protected Vector<String> columnNamesLexema = new Vector<String>();
	protected Vector<String> rowLinhaSintatico = new Vector<String>();
	protected Vector<Vector> rowDataSintatico = new Vector<Vector>();
	protected Vector<String> columnNamesSintatico = new Vector<String>();
	protected Vector<String> rowLinhaInstrucao2 = new Vector<String>();
	protected Vector<Vector> rowDataInstrucao2 = new Vector<Vector>();
	protected Vector<String> columnNamesInstrucao2 = new Vector<String>();
	protected Vector<String> rowLinhaInstrucaoLOCAL = new Vector<String>();
	protected Vector<Vector> rowDataInstrucaoLOCAL = new Vector<Vector>();
	protected Vector<String> columnNamesInstrucaoLOCAL = new Vector<String>();
	protected Vector<String> rowLinhaSintaticoLOCAL = new Vector<String>();
	protected Vector<Vector> rowDataSintaticoLOCAL = new Vector<Vector>();
	protected Vector<String> columnNamesSintaticoLOCAL = new Vector<String>();
	// VECTORES USADAS PARA COMPLEMENTAR A INTERFACE DAS TABELAS

	protected Vector<Integer> M = new Vector<Integer>(); // pilha
	protected Vector<Integer> ChamadasCall = new Vector<Integer>(); // pilha

	protected MeuJPanel pnlTabela = new MeuJPanel();
	protected MeuJPanel pnlAmostraDados = new MeuJPanel();
	protected MeuJPanel pnlPilha = new MeuJPanel();
	protected MeuJPanel pnlPartedeBaixo = new MeuJPanel();

	protected JTextArea textEntrada = new JTextArea(10, 10);
	protected JTextArea texSaida = new JTextArea(10, 10);
	protected JTextArea texBreakPoint = new JTextArea(10, 10);
	protected JTextArea textErroSintatico = new JTextArea(5, 5);
	
	protected JMenuBar MenuDeCima;
	protected JMenu Menu;
	protected JMenuItem AbreVariosTestesCompilador;
	protected JMenuItem AbreVariosTestesAssembly;

	public MaquinaVirtual() {
		super("Construcao Compiladores");

		try {
			Image btnAbrirImg = ImageIO.read(getClass().getResource("resources/abrir.jpg"));
			btnAbrirAssembly.setIcon(new ImageIcon(btnAbrirImg));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Arquivo abrir.jpg nao foi encontrado", "Arquivo de imagem ausente",
					JOptionPane.WARNING_MESSAGE);
		}
		try {
			Image btnExecutarImg = ImageIO.read(getClass().getResource("resources/cores.jpg"));
			btnExecutarAssembly.setIcon(new ImageIcon(btnExecutarImg));
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

		// Adicionando ações de click Botões superiores
		btnAbrirCodigo.addActionListener(new AbrirCodigo());
		btnExecutarCodigo.addActionListener(new ExecutarCodigo());
		btnAbrirAssembly.addActionListener(new AbrirAssembly());
		btnExecutarAssembly.addActionListener(new ExecutarAssembly());
		btnApagar.addActionListener(new Apagar());
		btnDeBug.addActionListener(new DeBug());
		btnContinuar.addActionListener(new Continuar());
		btnSair.addActionListener(new Sair());
		btnNovoSalvarExecutar.addActionListener(new NovoSalvarExecutar());
		bntAbrirCodigoGerado.addActionListener(new AbrirCodigoGerado());
		bntAbreVariosTestesCompilador.addActionListener(new AbreVariosTestesCompilador());
		bntAbreVariosTestesAssembly.addActionListener(new AbreVariosTestesAssembly());

		// Grid Superior, onde fica os botões
		JPanel pnlBotoes = new JPanel();
		FlowLayout flwBotoes = new FlowLayout();
		pnlBotoes.setLayout(flwBotoes);

		// Adicionando botões ao grid superior
		pnlBotoes.add(btnAbrirCodigo);
		pnlBotoes.add(btnExecutarCodigo);
		pnlBotoes.add(btnNovoSalvarExecutar);
		pnlBotoes.add(bntAbrirCodigoGerado);
		pnlBotoes.add(btnAbrirAssembly);
		pnlBotoes.add(btnExecutarAssembly);
		pnlBotoes.add(btnApagar);
		pnlBotoes.add(btnDeBug);
		pnlBotoes.add(btnContinuar);
		// pnlBotoes.add(btnAnalisador);
		pnlBotoes.add(btnSair);
		//pnlBotoes.add(bntAbreVariosTestesCompilador);
		//pnlBotoes.add(bntAbreVariosTestesAssembly);

		// Interface grafica
		JPanel pnlStatus = new JPanel();
		GridLayout grdStatus = new GridLayout(1, 2);
		pnlStatus.setLayout(grdStatus);
		pnlStatus.add(statusBar1);
		pnlStatus.add(statusBar2);
		// Interface grafica
		Container cntForm = this.getContentPane();
		cntForm.setLayout(new BorderLayout());
		cntForm.add(pnlBotoes, BorderLayout.NORTH);
		cntForm.add(pnlStatus, BorderLayout.SOUTH);
		// Interface grafica
		GridLayout grdTabela = new GridLayout(0, 1); // tentar arrumar setsize
		pnlTabela.setLayout(grdTabela);
		pnlAmostraDados.setLayout(grdTabela);
		pnlPilha.setLayout(grdTabela);
		cntForm.add(pnlTabela, BorderLayout.CENTER);
		cntForm.add(pnlAmostraDados, BorderLayout.WEST);
		cntForm.add(pnlPilha, BorderLayout.EAST);

		pnlPartedeBaixo.setLayout(grdStatus);
		cntForm.add(pnlPartedeBaixo, BorderLayout.SOUTH);

		this.addWindowListener(new FechamentoDeJanela());

		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setVisible(true);
		
		MenuDeCima = new JMenuBar();
		Menu  = new JMenu("Menu Adicional");
		AbreVariosTestesCompilador = new JMenuItem("Abre Varios Testes Compilador");
		AbreVariosTestesAssembly = new JMenuItem("Abre Varios Testes Assembly");
		Menu.add(AbreVariosTestesCompilador);
		Menu.add(AbreVariosTestesAssembly);
		MenuDeCima.add(Menu);
		
		setJMenuBar(MenuDeCima);
		AbreVariosTestesCompilador.addActionListener(new AbreVariosTestesCompilador());
		AbreVariosTestesAssembly.addActionListener(new AbreVariosTestesAssembly());

		btnAbrirCodigo.setEnabled(true);
		btnExecutarCodigo.setEnabled(false);
		btnNovoSalvarExecutar.setEnabled(false);
		bntAbrirCodigoGerado.setEnabled(false);
		btnAbrirAssembly.setEnabled(true);
		btnExecutarAssembly.setEnabled(false);
		btnApagar.setEnabled(true);
		btnDeBug.setEnabled(false);
		btnContinuar.setEnabled(false);
		btnSair.setEnabled(true);

	}

	protected class MeuJPanel extends JPanel implements MouseListener, MouseMotionListener {
		private static final long serialVersionUID = 1L;

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

	protected class AbreVariosTestesCompilador implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			AbrirCodigo AbrirCodigoChamada = new AbrirCodigo();
			ExecutarCodigo ExecutarCodigoChamada = new ExecutarCodigo();
			Apagar NovoApagar = new Apagar();
			NovoApagar.ApagaTudo();
			fileChooser = new JFileChooser();
			fileChooser.setMultiSelectionEnabled(true);
			fileChooser.showOpenDialog(null);
			files = fileChooser.getSelectedFiles();
			// AbrirCodigoChamada.InicializadorArquivo(files[0]);
			for (int i = 0; i < files.length; i++) {
				AbrirCodigoChamada.InicializadorArquivo(files[i]);
				NomeDoArquivoTXT = files[i].toString();
				try {
					AbrirCodigoChamada.TabelaInstrucoes2();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Sintatico SINOVO = new Sintatico();
				try {
					// dispose(); // close window
					// setVisible(false); // hide window
					SINOVO.analisadorSintatico();

				} catch (excecaoSintatico | IOException | excecaoSemantico e1) {
					System.out.println("Erro = " + e1);
				}
				ExecutarCodigoChamada.TabelaSintatico();
				if (CorDoFundo == Color.GREEN) {
					tabelaSintaticoLOCAL.setGridColor(Color.GREEN);
					tabelaInstrucaoLOCAL.setGridColor(Color.GREEN);
				} else {
					tabelaSintaticoLOCAL.setGridColor(Color.RED);
					tabelaInstrucaoLOCAL.setGridColor(Color.RED);
				}

			}
			pnlPartedeBaixo.removeAll();
		}
	}

	protected class AbreVariosTestesAssembly implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Apagar NovoApagar = new Apagar();
			NovoApagar.ApagaTudo();
			AbrirAssembly AbrirAssemblyChamada = new AbrirAssembly();
			ExecutarAssembly ExecutarAssemblyChamada = new ExecutarAssembly();
			for (int i = 0; i < files.length; i++) {
				fileChooserParaAssembly = new JFileChooser();
				NomeDoArquivoTXT = files[i].toString();
				fileChooserParaAssembly
				.setSelectedFile(new File(NomeDoArquivoTXT.replace(".txt", "") + " " + "CodigoGerado.txt"));
				AbrirAssemblyChamada.AbriCodigoAssemly(fileChooserParaAssembly.getSelectedFile());
				ExecutarAssemblyChamada.ExecutaOExecuta();
				

			}
		}
	}

	protected class AbrirCodigo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(true);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(false);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(false);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);

			Apagar NovoApagar = new Apagar();
			NovoApagar.ApagaTudo();
			fileChooser = new JFileChooser();
			returnValue = fileChooser.showOpenDialog(null);
			InicializadorArquivo(fileChooser.getSelectedFile());
			try {
				TabelaInstrucoes2();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			statusBar1.setText("Mensagem: Novo Abrir");
		}

		public void InicializadorArquivo(File selectedFile) {

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				NomeDoArquivoTXT = fileChooser.getSelectedFile().toString();

				// selectedFile = fileChooser.getSelectedFile();

				try {
					fstream = new FileInputStream(selectedFile);
					in = new DataInputStream(fstream);
					br = new BufferedReader(new InputStreamReader(in));
					fstream2 = new FileInputStream(selectedFile);
					in2 = new DataInputStream(fstream2);
					br2 = new BufferedReader(new InputStreamReader(in2));
					fstreamLOCAL = new FileInputStream(selectedFile);
					inLOCAL = new DataInputStream(fstreamLOCAL);
					brLOCAL = new BufferedReader(new InputStreamReader(inLOCAL));
					nlinha++;// optativo
					strLine = br.readLine();

				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

		}

		public void TabelaInstrucoes2() throws IOException {

			rowDataInstrucaoLOCAL = new Vector<Vector>();
			columnNamesInstrucaoLOCAL = new Vector<String>();
			rowDataInstrucao = new Vector<Vector>();
			columnNamesInstrucao = new Vector<String>();

			nlinhaLOCAL = 0;

			while ((strLineLOCAL = brLOCAL.readLine()) != null) {
				nlinhaLOCAL++;

				rowLinhaInstrucaoLOCAL = new Vector<String>();
				rowLinhaInstrucaoLOCAL.addElement(String.valueOf(nlinhaLOCAL));

				rowLinhaInstrucaoLOCAL.addElement(String.valueOf(strLineLOCAL));

				rowDataInstrucaoLOCAL.addElement(rowLinhaInstrucaoLOCAL);

				rowLinhaInstrucao = new Vector<String>();
				rowLinhaInstrucao.addElement(String.valueOf(nlinhaLOCAL));

				rowLinhaInstrucao.addElement(String.valueOf(strLineLOCAL));

				rowDataInstrucao.addElement(rowLinhaInstrucao);

			}

			columnNamesInstrucaoLOCAL.addElement("Linha");
			columnNamesInstrucaoLOCAL.addElement("Codigo");
			columnNamesInstrucao.addElement("Linha");
			columnNamesInstrucao.addElement("Codigo");

			tabelaInstrucaoLOCAL = new JTable(rowDataInstrucaoLOCAL, columnNamesInstrucaoLOCAL);
			barraRolagemInstrucaoLOCAL = new JScrollPane(tabelaInstrucaoLOCAL);
			tabelaInstrucaoLOCAL.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			tabelaInstrucoes = new JTable(rowDataInstrucao, columnNamesInstrucao);
			barraRolagemInstrucoes = new JScrollPane(tabelaInstrucoes);
			tabelaInstrucoes.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			pnlTabela.add(barraRolagemInstrucoes);
		}

	}

	protected class ExecutarCodigo implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(true);
			btnNovoSalvarExecutar.setEnabled(true);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(false);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(false);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);

			Sintatico SINOVO = new Sintatico();
			try {
				// dispose(); // close window
				// setVisible(false); // hide window
				SINOVO.analisadorSintatico();

			} catch (excecaoSintatico | IOException | excecaoSemantico e1) {
				System.out.println("Erro = " + e1);
			}
			TabelaSintatico();

		}

		public void TabelaSintatico() {

			rowDataSintaticoLOCAL = new Vector<Vector>();
			columnNamesSintaticoLOCAL = new Vector<String>();

			for (int i = 0; i < vetorTokens.size(); i++) {
				rowLinhaSintaticoLOCAL = new Vector<String>();

				rowLinhaSintaticoLOCAL.addElement(String.valueOf(vetorTokens.get(i).getLinha()));
				rowLinhaSintaticoLOCAL.addElement(String.valueOf(vetorTokens.get(i).getSimbolo()));
				rowLinhaSintaticoLOCAL.addElement(String.valueOf(vetorTokens.get(i).getLexema()));

				rowDataSintaticoLOCAL.addElement(rowLinhaSintaticoLOCAL);

			}

			columnNamesSintaticoLOCAL.addElement("Linha");
			columnNamesSintaticoLOCAL.addElement("Lexema");
			columnNamesSintaticoLOCAL.addElement("Simbolo");

			tabelaSintaticoLOCAL = new JTable(rowDataSintaticoLOCAL, columnNamesSintaticoLOCAL);
			barraRolagemSintaticoLOCAL = new JScrollPane(tabelaSintaticoLOCAL);
			tabelaSintaticoLOCAL.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			pnlPilha.add(barraRolagemSintaticoLOCAL);

			MostarMensagem(ErroDoTryCath);
			if (vetorTokens.size() != 0) {
				tabelaInstrucaoLOCAL.addRowSelectionInterval(0, vetorTokens.get(vetorTokens.size() - 1).getLinha() - 1);
			}
			textErroSintatico.setBackground(CorDoFundo);
			if (CorDoFundo == Color.GREEN) {
				btnAbrirCodigo.setEnabled(true);
				btnExecutarCodigo.setEnabled(true);
				btnNovoSalvarExecutar.setEnabled(true);
				bntAbrirCodigoGerado.setEnabled(true);
				btnAbrirAssembly.setEnabled(true);
				btnExecutarAssembly.setEnabled(false);
				btnApagar.setEnabled(true);
				btnDeBug.setEnabled(false);
				btnContinuar.setEnabled(false);
				btnSair.setEnabled(true);
			} else {
				btnAbrirCodigo.setEnabled(true);
				btnExecutarCodigo.setEnabled(true);
				btnNovoSalvarExecutar.setEnabled(true);
				bntAbrirCodigoGerado.setEnabled(false);
				btnAbrirAssembly.setEnabled(true);
				btnExecutarAssembly.setEnabled(false);
				btnApagar.setEnabled(true);
				btnDeBug.setEnabled(false);
				btnContinuar.setEnabled(false);
				btnSair.setEnabled(true);
			}

			pnlTabela.add(barraRolagemInstrucaoLOCAL);
			pnlTabela.remove(barraRolagemInstrucoes);
		}

		public void MostarMensagem(String mensagem) {
			textErroSintatico.setText(String.valueOf(mensagem)); // colocar codigo aqui
			scrollableTextErroSintatico = new JScrollPane(textErroSintatico);
			scrollableTextErroSintatico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			scrollableTextErroSintatico.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

			pnlPartedeBaixo.add(scrollableTextErroSintatico);
		}
	}

	protected class NovoSalvarExecutar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Apagar ApagarNovo = new Apagar();
			AbrirCodigo AbrirCodigoChamada = new AbrirCodigo();
			ExecutarCodigo ExecutarCodigoChamada = new ExecutarCodigo();
			if (barraRolagemInstrucaoLOCAL != null) {
				String LinhaDaTabela = (String) tabelaInstrucaoLOCAL.getModel().getValueAt(0, 1) + "\r\n";
				for (int LinhaAtual = 1; LinhaAtual < tabelaInstrucaoLOCAL.getRowCount(); LinhaAtual++) {
					LinhaDaTabela = LinhaDaTabela
							.concat((String) tabelaInstrucaoLOCAL.getModel().getValueAt(LinhaAtual, 1) + "\r\n");
				}
				createFile(LinhaDaTabela);

				ApagarNovo.ApagaTudo();
				AbrirCodigoChamada.InicializadorArquivo(fileChooser.getSelectedFile());
				try {
					AbrirCodigoChamada.TabelaInstrucoes2();
				} catch (IOException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}

				Sintatico SintaticoChamada = new Sintatico();
				try {

					SintaticoChamada.analisadorSintatico();

				} catch (excecaoSintatico | IOException | excecaoSemantico e1) {
					System.out.println("Erro = " + e1);
				}
				ExecutarCodigoChamada.TabelaSintatico();
			}

		}

		public void createFile(String code) {
			try {
				File directory = new File(NomeDoArquivoTXT);
				directory.createNewFile();

				FileWriter file = new FileWriter(directory);
				file.write(code);
				file.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	protected class AbrirCodigoGerado implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(false);
			btnExecutarAssembly.setEnabled(true);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(true);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);
			AbrirAssembly AbrirAssemblyChamada = new AbrirAssembly();
			Apagar ApagarNovo = new Apagar();
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				fileChooserParaAssembly = new JFileChooser();
				fileChooserParaAssembly
						.setSelectedFile(new File(NomeDoArquivoTXT.replace(".txt", "") + " " + "CodigoGerado.txt"));

				if (returnValue == fileChooserParaAssembly.APPROVE_OPTION) {
					ApagarNovo.ApagaTudo();
					AbrirAssemblyChamada.AbriCodigoAssemly(fileChooserParaAssembly.getSelectedFile());
				}

			}
		}

	}

	protected class AbrirAssembly implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(true);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(true);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);

			nlinha = 0;
			fileChooser = new JFileChooser();
			int returnValue = fileChooser.showOpenDialog(null);
			if (returnValue == JFileChooser.APPROVE_OPTION) {
				AbriCodigoAssemly(fileChooser.getSelectedFile());
			}

			statusBar1.setText("Mensagem: Arquivo a ser aberto");
		}

		void AbriCodigoAssemly(File selectedFile) {
			PosicaoFunção = 0;
			rowDataInstrucao.removeAllElements();
			nlinha = 0;
			Apagar ApagarNovo = new Apagar();
			ApagarNovo.ApagaTudo();
			try {
				fstream = new FileInputStream(selectedFile);
				in = new DataInputStream(fstream);
				br = new BufferedReader(new InputStreamReader(in));
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
						// se é o primeiro item, verifica se é uma linha ou uma instrucao
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
					// System.out.println(rowDataInstrucao);
					// System.out.println("Linha = " + strLine + " | posicao = " + posicao);
				}

				in.close();
			} catch (Exception e1) { // Catch exception if any
				System.err.println("Error: " + e1.getMessage());
			}

			InterfaceAbrir();

		}

		void InterfaceAbrir() {
			// Adiciona os campos da tabela
			columnNamesInstrucao.addElement("Linha");
			columnNamesInstrucao.addElement("Instrucao");
			columnNamesInstrucao.addElement("Atributo #1");
			columnNamesInstrucao.addElement("Atributo #2");
			columnNamesInstrucao.addElement("Comentario");
			columnNamesPilha.addElement("Posicao");
			columnNamesPilha.addElement("Valor");

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

	protected class ExecutarAssembly implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(true);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(true);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);

			
			 ExecutaOExecuta();
			
			statusBar1.setText("Mensagem: Arquivo a ser Executado");
		}
		void ExecutaOExecuta()
		{
			ChamadasCall = new Vector<Integer>();
			ExecucaoCompilador EC = new ExecucaoCompilador();
			int totalLinhasCodigo = tabelaInstrucoes.getRowCount();
			int TopoPilha = 0;

			for (int LinhaAtual = 0; LinhaAtual < totalLinhasCodigo; LinhaAtual++) {
				String instrucao = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 1);
				String atributo_1 = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 2);
				String atributo_2 = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 3);

				System.out.println("instrucao: " + instrucao + " | atributo_1: " + atributo_1 + " | atributo_2: "
						+ atributo_2 + " --------- TopoPilha: " + TopoPilha);

				// Finaliza o programa
				if (instrucao.equals("HLT")) {
					btnAbrirCodigo.setEnabled(true);
					btnExecutarCodigo.setEnabled(false);
					btnNovoSalvarExecutar.setEnabled(false);
					bntAbrirCodigoGerado.setEnabled(false);
					btnAbrirAssembly.setEnabled(true);
					btnExecutarAssembly.setEnabled(true);
					btnApagar.setEnabled(true);
					btnDeBug.setEnabled(true);
					btnContinuar.setEnabled(false);
					btnSair.setEnabled(true);
					System.out.println("HTL");
					break;

				} else if (instrucao.equals("JMP") || instrucao.equals("JMPF") || (instrucao.equals("CALL"))) {

					for (int t = 0; t < totalLinhasCodigo; t++) {
						String linha = (String) tabelaInstrucoes.getModel().getValueAt(t, 0);

						if (atributo_1.equals(linha)) {
							if ((instrucao.equals("CALL"))) {
								ChamadasCall.add(LinhaAtual);
							}

							if (instrucao.equals("JMPF")) {
								if (M.get(TopoPilha) == 0) {
									LinhaAtual = t;
								}
								M.remove(TopoPilha);
								TopoPilha = TopoPilha - 1;
							} else {
								LinhaAtual = t;
							}
						}
					}

				} else if (instrucao.equals("RETURN")) {

					LinhaAtual = ChamadasCall.get((ChamadasCall.size() - 1));
					ChamadasCall.remove((ChamadasCall.size() - 1));
					// i = valorReturn;
				} else {
					TopoPilha = EC.InstrucaoLinha(instrucao, atributo_1, atributo_2, TopoPilha, 0);
				}
				System.out.println(M);
			}

			EC.AtualizarPilha(TopoPilha);
		}
	}

	protected class Apagar implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			// InterfaceAbrir();
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(false);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(false);
			btnContinuar.setEnabled(false);
			btnSair.setEnabled(true);
			ApagaTudo();

			statusBar1.setText("Mensagem: Limpeza Geral");
		}

		void ApagaTudo() {
			InterfaceAbrir();
			tabelaInstrucoes.removeAll();
			barraRolagemInstrucoes.removeAll();
			tabelaPilha.removeAll();
			barraRolagemPilha.removeAll();
			rowLinhaInstrucao.removeAllElements();
			rowDataInstrucao.removeAllElements();
			columnNamesInstrucao.removeAllElements();
			rowLinhaPilha.removeAllElements();
			rowDataPilha.removeAllElements();
			columnNamesPilha.removeAllElements();

			textEntrada.removeAll();
			texSaida.removeAll();
			texBreakPoint.removeAll();

			pnlTabela.removeAll();
			pnlAmostraDados.removeAll();
			pnlPilha.removeAll();

			M.removeAllElements();
			ChamadasCall.removeAllElements();
			nlinha = 0;
			S = 0;
			Ji = 0;

			tabelaInstrucoes.removeAll();
			barraRolagemInstrucoes.removeAll();
			tabelaPilha.removeAll();
			barraRolagemPilha.removeAll();
			// tabelaLexema.removeAll();
			// barraRolagemLexema.removeAll();
			// tabelaSintatico.removeAll();
			// barraRolagemSintatico.removeAll();
			// tabelaInstrucao2.removeAll();
			// barraRolagemInstrucao2.removeAll();
			tabelaInstrucaoLOCAL.removeAll();
			barraRolagemInstrucaoLOCAL.removeAll();
			tabelaSintaticoLOCAL.removeAll();
			barraRolagemSintaticoLOCAL.removeAll();

			rowLinhaInstrucao.removeAllElements();
			rowDataInstrucao.removeAllElements();
			columnNamesInstrucao.removeAllElements();
			rowLinhaPilha.removeAllElements();
			rowDataPilha.removeAllElements();
			columnNamesPilha.removeAllElements();
			rowLinhaLexema.removeAllElements();
			rowDataLexema.removeAllElements();
			columnNamesLexema.removeAllElements();
			rowLinhaSintatico.removeAllElements();
			rowDataSintatico.removeAllElements();
			columnNamesSintatico.removeAllElements();
			rowLinhaInstrucao2.removeAllElements();
			rowDataInstrucao2.removeAllElements();
			columnNamesInstrucao2.removeAllElements();
			rowLinhaInstrucaoLOCAL.removeAllElements();
			rowDataInstrucaoLOCAL.removeAllElements();
			columnNamesInstrucaoLOCAL.removeAllElements();
			rowLinhaSintaticoLOCAL.removeAllElements();
			rowDataSintaticoLOCAL.removeAllElements();
			columnNamesSintaticoLOCAL.removeAllElements();

			pnlTabela.removeAll();
			pnlAmostraDados.removeAll();
			pnlPilha.removeAll();
			pnlPartedeBaixo.removeAll();

			textEntrada.removeAll();
			texSaida.removeAll();
			texBreakPoint.removeAll();
			textErroSintatico.removeAll();

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
			columnNamesInstrucaoLOCAL.addElement("Linha");
			columnNamesInstrucaoLOCAL.addElement("Codigo");
			columnNamesSintaticoLOCAL.addElement("Linha");
			columnNamesSintaticoLOCAL.addElement("Lexema");
			columnNamesSintaticoLOCAL.addElement("Simbolo");

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

			tabelaInstrucaoLOCAL = new JTable(rowDataInstrucaoLOCAL, columnNamesInstrucaoLOCAL);
			barraRolagemInstrucaoLOCAL = new JScrollPane(tabelaInstrucaoLOCAL);
//			tabelaInstrucao2.setPreferredScrollableViewportSize(tabelaInstrucao2.getPreferredSize());
//			tabelaInstrucao2.setFillsViewportHeight(false);
			tabelaInstrucaoLOCAL.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

			tabelaSintaticoLOCAL = new JTable(rowDataSintaticoLOCAL, columnNamesSintaticoLOCAL);
			barraRolagemSintaticoLOCAL = new JScrollPane(tabelaSintaticoLOCAL);
//			tabelaSintaticoLOCAL.setPreferredScrollableViewportSize(tabelaSintatico.getPreferredSize());
//			tabelaSintaticoLOCAL.setFillsViewportHeight(false);
			tabelaSintaticoLOCAL.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
			pnlPilha.add(barraRolagemSintaticoLOCAL);

		}
	}

	protected class DeBug implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(false);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(true);
			btnContinuar.setEnabled(true);
			btnSair.setEnabled(true);
			ChamadasCall = new Vector<Integer>();
		}

	}

	protected class Continuar implements ActionListener {
		int TopoPilha;
		int LinhaAtual = -1;

		public void actionPerformed(ActionEvent e) {
			btnAbrirCodigo.setEnabled(true);
			btnExecutarCodigo.setEnabled(false);
			btnNovoSalvarExecutar.setEnabled(false);
			bntAbrirCodigoGerado.setEnabled(false);
			btnAbrirAssembly.setEnabled(true);
			btnExecutarAssembly.setEnabled(false);
			btnApagar.setEnabled(true);
			btnDeBug.setEnabled(true);
			btnContinuar.setEnabled(true);
			btnSair.setEnabled(true);
			if (LinhaAtual != -999) {
				int totalLinhasCodigo = tabelaInstrucoes.getRowCount();
				ExecucaoCompilador EC = new ExecucaoCompilador();
				tabelaInstrucoes.setRowSelectionInterval((LinhaAtual) + 1, (LinhaAtual) + 1);
				LinhaAtual = CompiladorLinhaLinha(LinhaAtual + 1, totalLinhasCodigo, EC);

			}

			if (LinhaAtual != -999) {
				statusBar1.setText("Mensagem: Fim das Linhas");

			}

		}

		public int CompiladorLinhaLinha(int LinhaAtual, int totalLinhasCodigo, ExecucaoCompilador EC) {

			String instrucao = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 1);
			String atributo_1 = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 2);
			String atributo_2 = (String) tabelaInstrucoes.getModel().getValueAt(LinhaAtual, 3);

			System.out.println("instrucao: " + instrucao + " | atributo_1: " + atributo_1 + " | atributo_2: "
					+ atributo_2 + " --------- TopoPilha: " + TopoPilha);

			// Finaliza o programa
			if (instrucao.equals("HLT")) {
				btnAbrirCodigo.setEnabled(true);
				btnExecutarCodigo.setEnabled(false);
				btnNovoSalvarExecutar.setEnabled(false);
				bntAbrirCodigoGerado.setEnabled(false);
				btnAbrirAssembly.setEnabled(true);
				btnExecutarAssembly.setEnabled(true);
				btnApagar.setEnabled(true);
				btnDeBug.setEnabled(true);
				btnContinuar.setEnabled(false);
				btnSair.setEnabled(true);
				System.out.println("HTL");
				// Ver o que deve ser feito quando isso acontece (acho que finaliza o programa)
				// Futuramente remover esses comandos desses IFs do
				// ExecucaoCompilador.InstrucaoLinha
				return -999;
				// break;

			} else if (instrucao.equals("JMP") || instrucao.equals("JMPF") || (instrucao.equals("CALL"))) {

				for (int t = 0; t < totalLinhasCodigo; t++) {
					String linha = (String) tabelaInstrucoes.getModel().getValueAt(t, 0);

					if (atributo_1.equals(linha)) {
						if ((instrucao.equals("CALL"))) {
							ChamadasCall.add(LinhaAtual);
						}

						if (instrucao.equals("JMPF")) {
							if (M.get(TopoPilha) == 0) {
								LinhaAtual = t;
							}
							M.remove(TopoPilha);
							TopoPilha = TopoPilha - 1;
						} else {
							LinhaAtual = t;
						}
					}
				}

			} else if (instrucao.equals("RETURN")) {

				LinhaAtual = ChamadasCall.get((ChamadasCall.size() - 1));
				ChamadasCall.remove((ChamadasCall.size() - 1));
				// i = valorReturn;
			} else {
				TopoPilha = EC.InstrucaoLinha(instrucao, atributo_1, atributo_2, TopoPilha, 0);
			}
			System.out.println(M);

			EC.AtualizarPilha(TopoPilha);
			statusBar1.setText("Mensagem: Arquivo a ser Executado");
			return LinhaAtual;

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
			int valor;

			statusBar1.setText("Executando " + Instrucao);

			switch (Instrucao) {
			case "LDC": // Carregar constante (passado por parametro)
				TopoPilha = TopoPilha + 1;
				M.add(TopoPilha, Integer.parseInt(PrimeiroAtributo));
				break;

			case "LDV": // Carregar valor (passado a posicao no vetor que vai conter esse valor)
				TopoPilha = TopoPilha + 1;
				valor = (int) M.get(Integer.parseInt(PrimeiroAtributo));
				M.add(TopoPilha, valor);
				break;

			case "ADD":
				valor = (int) M.get(TopoPilha - 1) + (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), valor);
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "SUB":
				valor = (int) M.get(TopoPilha - 1) - (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), valor);
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "MULT":
				valor = (int) M.get(TopoPilha - 1) * (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), valor);
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "DIVI":
				valor = (int) M.get(TopoPilha - 1) / (int) M.get(TopoPilha);
				M.set((TopoPilha - 1), valor);
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "INV":
				valor = (int) M.get(TopoPilha) * -1;
				M.set(TopoPilha, valor);
				break;

			case "AND":
				if (M.get(TopoPilha - 1).equals(1) && M.get(TopoPilha).equals(1)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "OR":
				if (M.get(TopoPilha - 1).equals(1) || M.get(TopoPilha).equals(1)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "NEG":
				valor = 1 - M.get(TopoPilha);
				M.set(TopoPilha, valor);
				break;

			case "CME":
				if (M.get(TopoPilha - 1) < M.get(TopoPilha)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "CMA":
				if (M.get(TopoPilha - 1) > M.get(TopoPilha)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "CEQ":
				if (M.get(TopoPilha - 1).equals(M.get(TopoPilha))) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "CDIF":
				if (!M.get(TopoPilha - 1).equals(M.get(TopoPilha))) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "CMEQ":
				if (M.get(TopoPilha - 1) <= M.get(TopoPilha)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "CMAQ":
				if (M.get(TopoPilha - 1) >= M.get(TopoPilha)) {
					M.set((TopoPilha - 1), 1);
				} else {
					M.set((TopoPilha - 1), 0);
				}
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "START":
				TopoPilha = -1;
				break;

			case "STR":
				valor = (int) M.get(TopoPilha);
				M.set(Integer.parseInt(PrimeiroAtributo), valor);
				M.remove(TopoPilha);

				TopoPilha = TopoPilha - 1;
				break;

			case "NULL":
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
				M.remove(TopoPilha);
				TopoPilha = TopoPilha - 1;
				break;

			case "ALLOC":

				for (int k = 0; k <= Integer.parseInt(SegundoAtributo) - 1; k++) {
					TopoPilha = TopoPilha + 1;
					M.add(TopoPilha, -999); // atualmente -999 é referente a lixo;
				}
				break;

			case "DALLOC":
				for (int k = Integer.parseInt(SegundoAtributo) - 1; k >= 0; k--) {

					valor = (int) M.get(TopoPilha);
					M.set((Integer.parseInt(PrimeiroAtributo) + k), valor);
					M.remove(TopoPilha);
					TopoPilha = TopoPilha - 1;
				}
				break;

			default:
				System.out.println("Intrucao nao reconhecida = " + Instrucao);
				break;
			}

			return TopoPilha;
		}

		public void AtualizarPilha(int TopoPilha) {
			// pnlPilha = new MeuJPanel();
			rowDataPilha = new Vector<Vector>();
			columnNamesPilha = new Vector<String>();
			pnlPilha.remove(barraRolagemPilha);
			for (int i = 0; i < M.size(); i++) {
				// limpa o vector, nao sei se eh o mais correto, pode afetar a
				// memoria fisica
				rowLinhaPilha = new Vector<String>();
				rowLinhaPilha.addElement(String.valueOf(i));
				rowLinhaPilha.addElement(String.valueOf(M.get(i)));
				rowDataPilha.addElement(rowLinhaPilha);
			}
			System.out.println(M);
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
