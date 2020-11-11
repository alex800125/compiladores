import Excecoes.excecaoSintatico;

import java.io.IOException;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Color;
import java.io.BufferedReader;

public class Sintatico extends MaquinaVirtual {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Lexico lexico = new Lexico();
	Token token = null;
	BufferedReader br2;
	protected Vector<Token> vetorTokens = new Vector<Token>();

	public void analisadorSintatico() throws excecaoSintatico, IOException {

		try {
			br2 = lexico.InicializadorArquivo();
			getToken();
			if (token.getSimbolo().equals(Constantes.programa)) {
				getToken();
				if (token.getSimbolo().equals(Constantes.identificador)) {
					getToken();
					if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
						analisaBloco();
						if (token.getSimbolo().equals(Constantes.ponto)) {
							// esse Token não usa a função getToken pq ele serve para verificar se o arquivo
							// fechou ao fim, depois do Sponto, não entrando na lista de Tokens
							token = lexico.AnalisadorLexico();
							if (token.getSimbolo().equals(Constantes.eof)) {
								System.out.println("Fim do programa, sucesso");
								montarTabelas();
								textErroSintatico.setBackground(Color.GREEN);
								MostarMensagem("Fim do programa, sucesso");
							} else {
								getToken();
								throw new excecaoSintatico(
										"Fim do programa, não pode haver mais items. Linha: " + token.getLinha());
							}
						} else {
							throw new excecaoSintatico(Constantes.ponto, token.getSimbolo(), token.getLinha());
						}
					} else {
						throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
					}
				} else {
					throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Constantes.programa, token.getSimbolo(), token.getLinha());
			}
		} catch (excecaoSintatico e) {
			System.out.println(e);
			montarTabelas();
			textErroSintatico.setBackground(Color.RED);
			MostarMensagem(String.valueOf(e));
			
		}

		System.out.println("Fim do sintatico");
	}

	private void analisaBloco() throws excecaoSintatico, IOException {
		getToken();

		analisaEtVariaveis();
		analisaSubrotinas();
		analisaComandos();

	}

	private void analisaEtVariaveis() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Constantes.var)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {
				while (token.getSimbolo().equals(Constantes.identificador)) {

					analisaVariaveis();
					if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
						getToken();
					} else {
						throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
					}
				}
			} else {
				throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
			}
		}

	}

	private void analisaVariaveis() throws excecaoSintatico, IOException {
		do {
			if (token.getSimbolo().equals(Constantes.identificador)) {

				getToken();
				if (token.getSimbolo().equals(Constantes.virgula) || token.getSimbolo().equals(Constantes.doispontos)) {
					if (token.getSimbolo().equals(Constantes.virgula)) {
						getToken();
						if (token.getSimbolo().equals(Constantes.doispontos)) {
							throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
						}
					}
				} else {
					throw new excecaoSintatico(Constantes.virgula, Constantes.doispontos, token.getSimbolo(),
							token.getLinha());
				}

			} else {
				throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
			}

		} while (!(token.getSimbolo().equals(Constantes.doispontos)));

		getToken();
		analisaTipo();
	}

	private void analisaTipo() throws excecaoSintatico, IOException {
		if (!(token.getSimbolo().equals(Constantes.inteiro)) && !(token.getSimbolo().equals(Constantes.booleano))) {
			throw new excecaoSintatico(Constantes.inteiro, Constantes.booleano, token.getSimbolo(), token.getLinha());
		}
		getToken();
	}

	private void analisaSubrotinas() throws excecaoSintatico, IOException {

		if ((token.getSimbolo().equals(Constantes.procedimento)) || (token.getSimbolo().equals(Constantes.funcao))) {
			// verificar se é necessario por enquanto
		}

		while ((token.getSimbolo().equals(Constantes.procedimento)) || (token.getSimbolo().equals(Constantes.funcao))) {

			if (token.getSimbolo().equals(Constantes.procedimento)) {
				analisaDeclaracaoProcedimento();
			} else {
				analisaDeclaracaoFuncao();
			}

			if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
				getToken();
			} else {
				throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
			}
		}

	}

	private void analisaComandos() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Constantes.inicio)) {
			getToken();
			analisaComandoSimples();

			while (!(token.getSimbolo().equals(Constantes.fim))) {
				if (token.getSimbolo().equals(Constantes.ponto_virgula)) {

					getToken();
					if (!(token.getSimbolo().equals(Constantes.fim))) {
						analisaComandoSimples();
					}
				} else {
					throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
				}
			}
			getToken();
		} else {
			throw new excecaoSintatico(Constantes.inicio, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaComandoSimples() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Constantes.identificador)) {
			analisaAtribChprocedimento();
		} else if (token.getSimbolo().equals(Constantes.se)) {
			analisaSe();
		} else if (token.getSimbolo().equals(Constantes.enquanto)) {
			analisaEnquanto();
		} else if (token.getSimbolo().equals(Constantes.leia)) {
			analisaLeia();
		} else if (token.getSimbolo().equals(Constantes.escreva)) {
			analisaEscreva();
		} else {
			analisaComandos();
		}
	}

	private void analisaAtribChprocedimento() throws excecaoSintatico, IOException {
		Token aux = token;
		getToken();

		if (token.getSimbolo().equals(Constantes.atribuicao)) {
			analisaAtribuicao(aux);
		} else {
//			chamadaProcedimento(aux);
		}
	}

	private void analisaAtribuicao(Token attributionToken) throws excecaoSintatico, IOException {
		getToken();
		analisaExpressao();
	}

	private void analisaLeia() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {
				getToken();
				if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
					getToken();
				} else {
					throw new excecaoSintatico(Constantes.fecha_parenteses, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Constantes.abre_parenteses, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaEscreva() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {

				getToken();

				if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
					getToken();
				} else {
					throw new excecaoSintatico(Constantes.fecha_parenteses, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Constantes.abre_parenteses, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaEnquanto() throws excecaoSintatico, IOException {
		getToken();
		analisaExpressao();

		if (token.getSimbolo().equals(Constantes.faca)) {
			getToken();
			analisaComandoSimples();

		} else {
			throw new excecaoSintatico(Constantes.faca, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaSe() throws excecaoSintatico, IOException {
		getToken();
		analisaExpressao();

		if (token.getSimbolo().equals(Constantes.entao)) {
			getToken();
			analisaComandoSimples();
			if (token.getSimbolo().equals(Constantes.senao)) {
				getToken();
				analisaComandoSimples();
			}
		} else {
			throw new excecaoSintatico(Constantes.entao, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaDeclaracaoProcedimento() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
				analisaBloco();
			} else {
				throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaDeclaracaoFuncao() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			getToken();

			if (token.getSimbolo().equals(Constantes.doispontos)) {
				getToken();

				if (token.getSimbolo().equals(Constantes.inteiro) || token.getSimbolo().equals(Constantes.booleano)) {
					getToken();

					if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
						analisaBloco();
					}
				} else {
					throw new excecaoSintatico(Constantes.inteiro, Constantes.booleano, token.getSimbolo(),
							token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Constantes.doispontos, token.getSimbolo(), token.getLinha());
			}

		} else {
			throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaExpressao() throws excecaoSintatico, IOException {
		analisaExpressaoSimples();
		if (token.getSimbolo().equals(Constantes.maior) || token.getSimbolo().equals(Constantes.maiorig)
				|| token.getSimbolo().equals(Constantes.ig) || token.getSimbolo().equals(Constantes.menor)
				|| token.getSimbolo().equals(Constantes.menorig) || token.getSimbolo().equals(Constantes.dif)) {
			getToken();
			analisaExpressaoSimples();
		}
	}

	private void analisaExpressaoSimples() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)) {
			getToken();
		}
		analisaTermo();
		while (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)
				|| token.getSimbolo().equals(Constantes.ou)) {
			getToken();
			analisaTermo();
		}
	}

	private void analisaTermo() throws excecaoSintatico, IOException {
		analisaFator();
		while (token.getSimbolo().equals(Constantes.mult) || token.getSimbolo().equals(Constantes.div)
				|| token.getSimbolo().equals(Constantes.e)) {
			getToken();
			analisaFator();
		}
	}

	private void analisaFator() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Constantes.identificador)) {

			getToken();

		} else if (token.getSimbolo().equals(Constantes.numero)) {
			getToken();
		} else if (token.getSimbolo().equals(Constantes.nao)) {
			getToken();
			analisaFator();
		} else if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			analisaExpressao();
			if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
				getToken();
			} else {
				throw new excecaoSintatico(Constantes.fecha_parenteses, token.getSimbolo(), token.getLinha());
			}
		} else if (token.getSimbolo().equals(Constantes.verdadeiro) || token.getSimbolo().equals(Constantes.falso)) {
			getToken();
		} else {
			throw new excecaoSintatico("Expressão incompleta na linha: " + token.getLinha());
		}
	}

	// ----------------------------------------------------------------------------
	// Aqui pra baixo é a parte da interface
	// ----------------------------------------------------------------------------

	private void getToken() throws IOException, excecaoSintatico {
		token = lexico.AnalisadorLexico();
		if (token.getSimbolo().equals(Constantes.erro)) {
			throw new excecaoSintatico("Erro na parte Lexica.");
		} else {
			vetorTokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
		}
	}

	private void montarTabelas() throws IOException {
		TabelaInstrucoes2();
		TabelaSintatico();
		
		
	}
	public void MostarMensagem(String mensagem) {

		JScrollPane scrollableTextErroSintatico = new JScrollPane(textErroSintatico);
		scrollableTextErroSintatico.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollableTextErroSintatico.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		textErroSintatico.setText(String.valueOf(mensagem)); // colocar codigo aqui
		pnlPartedeBaixo.add(scrollableTextErroSintatico);
	}

	public void TabelaSintatico() {

		rowDataSintatico = new Vector<Vector>();
		columnNamesSintatico = new Vector<String>();

		for (int i = 0; i < vetorTokens.size(); i++) {
			rowLinhaSintatico = new Vector<String>();

			rowLinhaSintatico.addElement(String.valueOf(vetorTokens.get(i).getLinha()));
			rowLinhaSintatico.addElement(String.valueOf(vetorTokens.get(i).getSimbolo()));
			rowLinhaSintatico.addElement(String.valueOf(vetorTokens.get(i).getLexema()));

			rowDataSintatico.addElement(rowLinhaSintatico);

		}

		columnNamesSintatico.addElement("Linha");
		columnNamesSintatico.addElement("Lexema");
		columnNamesSintatico.addElement("Simbolo");

		tabelaSintatico = new JTable(rowDataSintatico, columnNamesSintatico);
		barraRolagemSintatico = new JScrollPane(tabelaSintatico);
//		tabelaSintatico.setPreferredScrollableViewportSize(tabelaSintatico.getPreferredSize());
//		tabelaSintatico.setFillsViewportHeight(false);
		tabelaSintatico.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		pnlPilha.add(barraRolagemSintatico);

	}

	public void TabelaInstrucoes2() throws IOException {

		rowDataInstrucao2 = new Vector<Vector>();
		columnNamesInstrucao2 = new Vector<String>();

		String strLine2;
		
		int nlinha2 = 0;

		while ((strLine2 = br2.readLine()) != null) {
			nlinha2++;
			
			rowLinhaInstrucao2 = new Vector<String>();
			rowLinhaInstrucao2.addElement(String.valueOf(nlinha2));

			rowLinhaInstrucao2.addElement(String.valueOf(strLine2));
			
			rowDataInstrucao2.addElement(rowLinhaInstrucao2);
		}

		columnNamesInstrucao2.addElement("Linha");
		columnNamesInstrucao2.addElement("Codigo");


		tabelaInstrucao2 = new JTable(rowDataInstrucao2, columnNamesInstrucao2);
		barraRolagemInstrucao2 = new JScrollPane(tabelaInstrucao2);
//		tabelaInstrucao2.setPreferredScrollableViewportSize(tabelaInstrucao2.getPreferredSize());
//		tabelaInstrucao2.setFillsViewportHeight(false);
		tabelaInstrucao2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

		if (vetorTokens.size() != 0) {
			tabelaInstrucao2.addRowSelectionInterval(0, vetorTokens.get(vetorTokens.size() - 1).getLinha() - 1);
		}
		pnlTabela.add(barraRolagemInstrucao2);
	}
}
