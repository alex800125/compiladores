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
 
public class Janela extends JFrame 
{
    protected static final long serialVersionUID = 1L;

    protected JButton btnAbrir   = new JButton ("Abrir"),//carregar arquivo
                      btnExecutar  = new JButton ("Executar"),//Executa
                      btnApagar  = new JButton ("Apagar"),//apagar escritas
                      btnDeBug  = new JButton ("DeBug"),//Escrever debug
                      btnContinuar  = new JButton ("Continuar"),//proxima instru��o
                      btnSair    = new JButton ("Sair");

    //protected MeuJPanel pnlDesenho = new MeuJPanel ();
    
    protected JLabel statusBar1 = new JLabel ("Mensagem:"),
                     statusBar2 = new JLabel ("Coordenada:");

    //protected boolean esperaPonto, esperaInicioReta, esperaFimReta;

    //protected Color corAtual = Color.BLACK;
    //protected Ponto p1;
    
    //protected Vector<Figura> figuras = new Vector<Figura>();
    //protected JPanel painelFundo;
    protected JTable tabelaInstrucoes;
    protected JScrollPane barraRolagemInstrucoes;
    protected JTable tabelaPilha;
    protected JScrollPane barraRolagemPilha;

    String [] colunasInstrucoes = {"Linha", "Instru��o", "Atributo #1", "Atributo #2", "Comentario"};
    String [] colunasPilha = {"Endere�o", "Valor"};


    protected MeuJPanel pnlTabela = new MeuJPanel ();
    protected MeuJPanel pnlEntrada = new MeuJPanel ();
    //protected MeuJPanel pnlSaida = new MeuJPanel ();
    //protected MeuJPanel pnlBreakPoint = new MeuJPanel ();
    protected MeuJPanel pnlPilha = new MeuJPanel ();
    //painelFundo = new JPanel();

    
    public Janela ()
    {
        super("Constru��o Compiladores");

       
        try
        {
            Image btnAbrirImg = ImageIO.read(getClass().getResource("resources/ronaldinho2.jpg"));
            btnAbrir.setIcon(new ImageIcon(btnAbrirImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo abrir.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }
        try
        {
            Image btnExecutarImg = ImageIO.read(getClass().getResource("resources/marta2.jpg"));
            btnExecutar.setIcon(new ImageIcon(btnExecutarImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo Executar.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }

       
        try
        {
            Image btnApagarImg = ImageIO.read(getClass().getResource("resources/romario2.jpg"));
            btnApagar.setIcon(new ImageIcon(btnApagarImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo Executar.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }

       
        try
        {
            Image btnDeBugImg = ImageIO.read(getClass().getResource("resources/rivaldo2.jpg"));
            btnDeBug.setIcon(new ImageIcon(btnDeBugImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo DeBug.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }

       
        try
        {
            Image btnContinuarImg = ImageIO.read(getClass().getResource("resources/formiga2.jpg"));
            btnContinuar.setIcon(new ImageIcon(btnContinuarImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo Continuar.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }

        try
        {
            Image btnSairImg = ImageIO.read(getClass().getResource("resources/ronaldo2.jpg"));
            btnSair.setIcon(new ImageIcon(btnSairImg));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog (null,
                                           "Arquivo sair.jpg n�o foi encontrado",
                                           "Arquivo de imagem ausente",
                                           JOptionPane.WARNING_MESSAGE);
        }


        
        btnAbrir.addActionListener (new Abrir());
        btnExecutar.addActionListener (new Executar ());
        btnApagar.addActionListener (new Apagar());
        btnDeBug.addActionListener (new DeBug ());
        btnContinuar.addActionListener (new Continuar());
        btnSair.addActionListener (new Sair());

        JPanel     pnlBotoes = new JPanel();
        FlowLayout flwBotoes = new FlowLayout(); 
        pnlBotoes.setLayout (flwBotoes);

        pnlBotoes.add (btnAbrir);
        pnlBotoes.add (btnExecutar);
        pnlBotoes.add (btnApagar);
        pnlBotoes.add (btnDeBug);
        pnlBotoes.add (btnContinuar);
        pnlBotoes.add (btnSair);

        JPanel     pnlStatus = new JPanel();
        GridLayout grdStatus = new GridLayout(1,2);
        pnlStatus.setLayout(grdStatus);

        pnlStatus.add(statusBar1);
        pnlStatus.add(statusBar2);

        Container cntForm = this.getContentPane();
        cntForm.setLayout (new BorderLayout());
        cntForm.add (pnlBotoes,  BorderLayout.NORTH);
        //cntForm.add (pnlDesenho, BorderLayout.CENTER);
        cntForm.add (pnlStatus,  BorderLayout.SOUTH);
        
        GridLayout grdTabela = new GridLayout(0,1); //tentar arrumar setsize
        pnlTabela.setLayout(grdTabela);
        pnlEntrada.setLayout(grdTabela);
        
        //pnlSaida.setLayout(grdTabela);
        //pnlBreakPoint.setLayout(grdTabela);
        pnlPilha.setLayout(grdTabela);

        cntForm.add (pnlTabela, BorderLayout.CENTER);
        cntForm.add (pnlEntrada, BorderLayout.WEST);
        cntForm.add (pnlPilha, BorderLayout.EAST);
        //cntForm.add (pnlSaida, BorderLayout.CENTER);
       // cntForm.add (pnlBreakPoint, BorderLayout.CENTER);
       
        //pnlTabela.setVisible(false);

        
        this.addWindowListener (new FechamentoDeJanela());

        this.setSize (700,500);
        this.setVisible (true);
    }

    protected class MeuJPanel extends    JPanel 
                              implements MouseListener,
                                         MouseMotionListener
    {
	public MeuJPanel()
        {
            super();

            this.addMouseListener       (this);
            this.addMouseMotionListener (this);
        }

      
        
        public void mousePressed (MouseEvent e)
        {
           //intera��o com mouse pressed
        }
        
        public void mouseReleased (MouseEvent e)
        {}
        
        public void mouseClicked (MouseEvent e)
        {}
        
        public void mouseEntered (MouseEvent e)
        {}

        public void mouseExited (MouseEvent e)
        {}
        
        public void mouseDragged(MouseEvent e)
        {}

        public void mouseMoved(MouseEvent e)
        {
            statusBar2.setText("Coordenada: "+e.getX()+","+e.getY());
        }
    }



    protected class Abrir implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
        	  int posicao = 0;
        	  Object[][] dadosInstrucoes = {{"START"}};
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
        			  while ((strLine = br.readLine()) != null)   {
        			  // Print the content on the console
        			  System.out.println (strLine);
        			  String[] words = strLine.split(" ");
        			  
        			  /*
        			  switch (words)
        			  {
    		    	  case 'START': //nome da linha
    		    		  break;
    		    	  case 'S': //Start
    		    		  break;
	    		  			
	    		  			
		    		  default:
		    			  break;
    		    	  }
    		    	  */
        			  
        			  Object [][] dadosInstrucoes1 = { 
      						{strLine}
      					}; //colocar codigo aquui
        					 //colocar codigo aquui
        			  dadosInstrucoes =  dadosInstrucoes1;
        			  }
        			  
        			  //Close the input stream
        			  in.close();
        			    }catch (Exception e1){//Catch exception if any
        			  System.err.println("Error: " + e1.getMessage());
        			  }
        		  
        		  //TABELAInstrucoes
        		    /*
        		     * Object [][] dadosInstrucoes = { 
    						{"*l*","*i*","*a1*","*a2*","*c*"}
    					}; //colocar codigo aquui
    					*/
        		    
        		    tabelaInstrucoes = new JTable(dadosInstrucoes, colunasInstrucoes);
        	        barraRolagemInstrucoes = new JScrollPane(tabelaInstrucoes);
        	        tabelaInstrucoes.setPreferredScrollableViewportSize(tabelaInstrucoes.getPreferredSize());
        	        tabelaInstrucoes.setFillsViewportHeight(false);
        	        pnlTabela.add(barraRolagemInstrucoes);
        	        
        	      //TABELAPilha
        		    Object [][] dadosPilha = { 
    						{"*E*","*V*"}
    					}; //colocar codigo aquui
        		    
        		    tabelaPilha = new JTable(dadosPilha, colunasPilha);
        	        barraRolagemPilha = new JScrollPane(tabelaPilha);
        	        tabelaPilha.setPreferredScrollableViewportSize(tabelaPilha.getPreferredSize());
        	        tabelaPilha.setFillsViewportHeight(false);
        	        pnlPilha.add(barraRolagemPilha);
        	        
        	        
        	        
        	        //Entrada opcional
            	  	JTextArea textEntrada = new JTextArea(10, 10);  
    		        JScrollPane scrollableTextEntrada = new JScrollPane(textEntrada);   
    		        scrollableTextEntrada.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    		        scrollableTextEntrada.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
    		        textEntrada.setText("Entrada");   //colocar codigo aquui
    		        pnlEntrada.add(scrollableTextEntrada);
    		        //getContentPane().add(pnlEntrada);
    		        //saida
    		        
    		        JTextArea texSaida = new JTextArea(10, 10);  
    		        JScrollPane scrollableTextSaida = new JScrollPane(texSaida);   
    		        scrollableTextSaida.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    		        scrollableTextSaida.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
    		        texSaida.setText("SAida");   //colocar codigo aquui
    		        pnlEntrada.add(scrollableTextSaida);
    		        
    		        JTextArea texBreakPoint = new JTextArea(10, 10);  
    		        JScrollPane scrollableTextBreakPoint = new JScrollPane(texBreakPoint);   
    		        scrollableTextBreakPoint.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);  
    		        scrollableTextBreakPoint.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);  
    		        texBreakPoint.setText("BreakPoint");   //colocar codigo aquui
    		        pnlEntrada.add(scrollableTextBreakPoint);
    		        //getContentPane().add(pnlEntrada);
    		       
        		  
        	  }
        	  
              statusBar1.setText("Mensagem: Arquivo a ser aberto");
          }
    }
    protected class Executar implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
             
              statusBar1.setText("Mensagem: Arquivo a ser Executado");
          }
    }
    protected class Apagar implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
              
              statusBar1.setText("Mensagem: Limpeza Geral");
          }
    }
    protected class DeBug implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
        	  	
		        //getContentPane().add(pnlEntrada);
              
              statusBar1.setText("Mensagem: Execu��o Passo a Passo");
          }
    }
    protected class Continuar implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
              
              statusBar1.setText("Mensagem: Pressione Continuar");
          }
    }
    protected class Sair implements ActionListener
    {
          public void actionPerformed (ActionEvent e)    
          {
        	  System.exit(0);
              statusBar1.setText("Mensagem: ADeus");
          }
    }

    protected class FechamentoDeJanela extends WindowAdapter
    {
        public void windowClosing (WindowEvent e)
        {
            System.exit(0);
        }
    }
}
