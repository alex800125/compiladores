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
			if (token.getSimbolo().equals(Simbolos.programa)) {
				getToken();
				if (token.getSimbolo().equals(Simbolos.identificador)) {
					getToken();
					if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {
						analisaBloco();
						if (token.getSimbolo().equals(Simbolos.ponto)) {
							// esse Token não usa a função getToken pq ele serve para verificar se o arquivo
							// fechou ao fim, depois do Sponto, não entrando na lista de Tokens
							token = lexico.AnalisadorLexico();
							if (token.getSimbolo().equals(Simbolos.eof)) {
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
							throw new excecaoSintatico(Simbolos.ponto, token.getSimbolo(), token.getLinha());
						}
					} else {
						throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
					}
				} else {
					throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Simbolos.programa, token.getSimbolo(), token.getLinha());
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
		if (token.getSimbolo().equals(Simbolos.var)) {
			getToken();
			if (token.getSimbolo().equals(Simbolos.identificador)) {
				while (token.getSimbolo().equals(Simbolos.identificador)) {

					analisaVariaveis();
					if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {
						getToken();
					} else {
						throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
					}
				}
			} else {
				throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
			}
		}

	}

	private void analisaVariaveis() throws excecaoSintatico, IOException {
		do {
			if (token.getSimbolo().equals(Simbolos.identificador)) {

				getToken();
				if (token.getSimbolo().equals(Simbolos.virgula) || token.getSimbolo().equals(Simbolos.doispontos)) {
					if (token.getSimbolo().equals(Simbolos.virgula)) {
						getToken();
						if (token.getSimbolo().equals(Simbolos.doispontos)) {
							throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
						}
					}
				} else {
					throw new excecaoSintatico(Simbolos.virgula, Simbolos.doispontos, token.getSimbolo(),
							token.getLinha());
				}

			} else {
				throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
			}

		} while (!(token.getSimbolo().equals(Simbolos.doispontos)));

		getToken();
		analisaTipo();
	}

	private void analisaTipo() throws excecaoSintatico, IOException {
		if (!(token.getSimbolo().equals(Simbolos.inteiro)) && !(token.getSimbolo().equals(Simbolos.booleano))) {
			throw new excecaoSintatico(Simbolos.inteiro, Simbolos.booleano, token.getSimbolo(), token.getLinha());
		}
		getToken();
	}

	private void analisaSubrotinas() throws excecaoSintatico, IOException {

		if ((token.getSimbolo().equals(Simbolos.procedimento)) || (token.getSimbolo().equals(Simbolos.funcao))) {
			// verificar se é necessario por enquanto
		}

		while ((token.getSimbolo().equals(Simbolos.procedimento)) || (token.getSimbolo().equals(Simbolos.funcao))) {

			if (token.getSimbolo().equals(Simbolos.procedimento)) {
				analisaDeclaracaoProcedimento();
			} else {
				analisaDeclaracaoFuncao();
			}

			if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {
				getToken();
			} else {
				throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
			}
		}

	}

	private void analisaComandos() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Simbolos.inicio)) {
			getToken();
			analisaComandoSimples();

			while (!(token.getSimbolo().equals(Simbolos.fim))) {
				if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {

					getToken();
					if (!(token.getSimbolo().equals(Simbolos.fim))) {
						analisaComandoSimples();
					}
				} else {
					throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
				}
			}
			getToken();
		} else {
			throw new excecaoSintatico(Simbolos.inicio, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaComandoSimples() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Simbolos.identificador)) {
			analisaAtribChprocedimento();
		} else if (token.getSimbolo().equals(Simbolos.se)) {
			analisaSe();
		} else if (token.getSimbolo().equals(Simbolos.enquanto)) {
			analisaEnquanto();
		} else if (token.getSimbolo().equals(Simbolos.leia)) {
			analisaLeia();
		} else if (token.getSimbolo().equals(Simbolos.escreva)) {
			analisaEscreva();
		} else {
			analisaComandos();
		}
	}

	private void analisaAtribChprocedimento() throws excecaoSintatico, IOException {
		Token aux = token;
		getToken();

		if (token.getSimbolo().equals(Simbolos.atribuicao)) {
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
		if (token.getSimbolo().equals(Simbolos.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Simbolos.identificador)) {
				getToken();
				if (token.getSimbolo().equals(Simbolos.fecha_parenteses)) {
					getToken();
				} else {
					throw new excecaoSintatico(Simbolos.fecha_parenteses, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Simbolos.abre_parenteses, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaEscreva() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Simbolos.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Simbolos.identificador)) {

				getToken();

				if (token.getSimbolo().equals(Simbolos.fecha_parenteses)) {
					getToken();
				} else {
					throw new excecaoSintatico(Simbolos.fecha_parenteses, token.getSimbolo(), token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Simbolos.abre_parenteses, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaEnquanto() throws excecaoSintatico, IOException {
		getToken();
		analisaExpressao();

		if (token.getSimbolo().equals(Simbolos.faca)) {
			getToken();
			analisaComandoSimples();

		} else {
			throw new excecaoSintatico(Simbolos.faca, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaSe() throws excecaoSintatico, IOException {
		getToken();
		analisaExpressao();

		if (token.getSimbolo().equals(Simbolos.entao)) {
			getToken();
			analisaComandoSimples();
			if (token.getSimbolo().equals(Simbolos.senao)) {
				getToken();
				analisaComandoSimples();
			}
		} else {
			throw new excecaoSintatico(Simbolos.entao, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaDeclaracaoProcedimento() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Simbolos.identificador)) {
			getToken();
			if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {
				analisaBloco();
			} else {
				throw new excecaoSintatico(Simbolos.ponto_virgula, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaDeclaracaoFuncao() throws excecaoSintatico, IOException {
		getToken();
		if (token.getSimbolo().equals(Simbolos.identificador)) {
			getToken();

			if (token.getSimbolo().equals(Simbolos.doispontos)) {
				getToken();

				if (token.getSimbolo().equals(Simbolos.inteiro) || token.getSimbolo().equals(Simbolos.booleano)) {
					getToken();

					if (token.getSimbolo().equals(Simbolos.ponto_virgula)) {
						analisaBloco();
					}
				} else {
					throw new excecaoSintatico(Simbolos.inteiro, Simbolos.booleano, token.getSimbolo(),
							token.getLinha());
				}
			} else {
				throw new excecaoSintatico(Simbolos.doispontos, token.getSimbolo(), token.getLinha());
			}

		} else {
			throw new excecaoSintatico(Simbolos.identificador, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaExpressao() throws excecaoSintatico, IOException {
		analisaExpressaoSimples();
		if (token.getSimbolo().equals(Simbolos.maior) || token.getSimbolo().equals(Simbolos.maiorig)
				|| token.getSimbolo().equals(Simbolos.ig) || token.getSimbolo().equals(Simbolos.menor)
				|| token.getSimbolo().equals(Simbolos.menorig) || token.getSimbolo().equals(Simbolos.dif)) {
			getToken();
			analisaExpressaoSimples();
		}
	}

	private void analisaExpressaoSimples() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Simbolos.mais) || token.getSimbolo().equals(Simbolos.menos)) {
			getToken();
		}
		analisaTermo();
		while (token.getSimbolo().equals(Simbolos.mais) || token.getSimbolo().equals(Simbolos.menos)
				|| token.getSimbolo().equals(Simbolos.ou)) {
			getToken();
			analisaTermo();
		}
	}

	private void analisaTermo() throws excecaoSintatico, IOException {
		analisaFator();
		while (token.getSimbolo().equals(Simbolos.mult) || token.getSimbolo().equals(Simbolos.div)
				|| token.getSimbolo().equals(Simbolos.e)) {
			getToken();
			analisaFator();
		}
	}

	private void analisaFator() throws excecaoSintatico, IOException {
		if (token.getSimbolo().equals(Simbolos.identificador)) {

			getToken();

		} else if (token.getSimbolo().equals(Simbolos.numero)) {
			getToken();
		} else if (token.getSimbolo().equals(Simbolos.nao)) {
			getToken();
			analisaFator();
		} else if (token.getSimbolo().equals(Simbolos.abre_parenteses)) {
			getToken();
			analisaExpressao();
			if (token.getSimbolo().equals(Simbolos.fecha_parenteses)) {
				getToken();
			} else {
				throw new excecaoSintatico(Simbolos.fecha_parenteses, token.getSimbolo(), token.getLinha());
			}
		} else if (token.getSimbolo().equals(Simbolos.verdadeiro) || token.getSimbolo().equals(Simbolos.falso)) {
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
		if (token.getSimbolo().equals(Simbolos.erro)) {
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
