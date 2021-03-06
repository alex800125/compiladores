import Excecoes.excecaoSemantico;
import Excecoes.excecaoSintatico;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Color;
import java.io.BufferedReader;

public class Sintatico /* extends MaquinaVirtia */ {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// Importando variaveis
	protected BufferedReader br2 = MaquinaVirtual.br2;

	Lexico lexico = new Lexico();
	Semantico semantico = new Semantico();
	GeradorCodigo geradorCodigo = new GeradorCodigo();
	Token token = null;

	protected Vector<Token> vetorTokens = new Vector<Token>();

	// S�o as posi��es na mem�ria que est� alocado a vari�vel
	private int posicao = 0;

	// S�o as nomina��es para onde ser� realizado o pulo (Ex: L"0", L"1", etc)
	private int label = 0;
	private int auxLabel = 0;

	private List<Token> expressao = new ArrayList<Token>();

	private int countVariable = 0;

	private List<Integer> variaveisAlocadas = new ArrayList<Integer>();
	private List<Boolean> flagListaProcedimento = new ArrayList<Boolean>();
	private List<Boolean> flagListaFuncoes = new ArrayList<Boolean>();
	private List<String> nameOfFunction = new ArrayList<String>();

	public void analisadorSintatico() throws excecaoSintatico, IOException, excecaoSemantico {

		try {
			flagListaFuncoes.add(false);
			flagListaProcedimento.add(false);
			lexico.InicializadorArquivo();

			getToken();
			if (token.getSimbolo().equals(Constantes.programa)) {
				geradorCodigo.criarCodigo(Constantes.START, Constantes.EMPTY, Constantes.EMPTY);

				variaveisAlocadas.add(1);
				posicao++;
				geradorCodigo.criarCodigo(Constantes.ALLOC, variaveisAlocadas.get(variaveisAlocadas.size() - 1));

				getToken();
				if (token.getSimbolo().equals(Constantes.identificador)) {
					semantico.inserePrograma(token);
					getToken();
					System.out.println("analisadorSintatico 1 token = " + token.getSimbolo());
					if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
						analisaBloco();
						System.out.println("analisadorSintatico 2 token = " + token.getSimbolo());
						if (token.getSimbolo().equals(Constantes.ponto)) {
							// esse Token n�o usa a fun��o getToken pq ele serve para verificar se o arquivo
							// fechou ao fim, depois do Sponto, n�o entrando na lista de Tokens
							token = lexico.AnalisadorLexico();
							if (token.getSimbolo().equals(Constantes.eof)) {

								System.out.println("analisaBloco variableOfAlloc = " + variaveisAlocadas.size()
										+ " | flagProcedureList = " + flagListaProcedimento.size()
										+ " | flagListaFuncoes = " + flagListaFuncoes.size());

								posicao = posicao - variaveisAlocadas.get(variaveisAlocadas.size() - 1);
								geradorCodigo.criarCodigo(Constantes.DALLOC, -1);
								variaveisAlocadas.remove(variaveisAlocadas.size() - 1);

								geradorCodigo.criarCodigo(Constantes.HLT, Constantes.EMPTY, Constantes.EMPTY);
								geradorCodigo.gerarArquivo();

								semantico.limparTabela();

								System.out.println("Fim do programa, sucesso");

								montarTabelas();
								// Seta a cor como verde, que significa que tudo ocorreu bem
								MaquinaVirtual.CorDoFundo = Color.GREEN;

								MostarMensagem("Fim do programa, sucesso");

							} else {
								getToken();
								throw new excecaoSintatico(
										"Fim do programa, n�o pode haver mais items. Linha: " + token.getLinha());
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
		} catch (excecaoSintatico | excecaoSemantico e) {
			System.out.println(e);
			montarTabelas();
			MaquinaVirtual.CorDoFundo = Color.RED;

			MostarMensagem(String.valueOf(e));

		}
		// dispose(); // close window
		// setVisible(false); // hide window
		System.out.println("Fim do sintatico");
	}

	private void analisaBloco() throws excecaoSintatico, IOException, excecaoSemantico {
		getToken();

		analisaEtVariaveis();
		analisaSubrotinas();
		analisaComandos();

		System.out.println("analisaBloco variableOfAlloc = " + variaveisAlocadas.size() + " | flagProcedureList = "
				+ flagListaProcedimento.size() + " | flagListaFuncoes = " + flagListaFuncoes.size());

		if (variaveisAlocadas.size() > 0 && (!flagListaProcedimento.get(flagListaProcedimento.size() - 1))
				&& (!flagListaFuncoes.get(flagListaFuncoes.size() - 1))) {
			if (variaveisAlocadas.get(variaveisAlocadas.size() - 1) > 0) {
				posicao = posicao - variaveisAlocadas.get(variaveisAlocadas.size() - 1);
				geradorCodigo.criarCodigo(Constantes.DALLOC, -1);
				variaveisAlocadas.remove(variaveisAlocadas.size() - 1);
			} else {
				variaveisAlocadas.remove(variaveisAlocadas.size() - 1);
			}
		}
	}

	private void analisaEtVariaveis() throws excecaoSintatico, IOException, excecaoSemantico {
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

		variaveisAlocadas.add(countVariable);
		countVariable = 0;

		if (variaveisAlocadas.get(variaveisAlocadas.size() - 1) > 0) {
			geradorCodigo.criarCodigo(Constantes.ALLOC, variaveisAlocadas.get(variaveisAlocadas.size() - 1));
		}
	}

	private void analisaVariaveis() throws excecaoSintatico, IOException, excecaoSemantico {
		do {
			if (token.getSimbolo().equals(Constantes.identificador)) {

				semantico.procuraTabelaSimbolos(token);
				semantico.inserirVariavel(token, posicao);
				countVariable++;
				posicao++;
				System.out.println("analisaVariaveis posicao = " + posicao + " | variaveisAlocadas.size() = "
						+ variaveisAlocadas.size());

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
		} else {
			semantico.inserirTipoEmVariavel(token);
		}
		getToken();
	}

	private void analisaSubrotinas() throws excecaoSintatico, IOException, excecaoSemantico {
		int auxrot = 0, flag = 0;

		if ((token.getSimbolo().equals(Constantes.procedimento)) || (token.getSimbolo().equals(Constantes.funcao))) {
			auxrot = label;
			geradorCodigo.criarCodigo(Constantes.JMP, Constantes.LABEL + label, Constantes.EMPTY);
			label++;
			flag = 1;
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

		if (flag == 1) {
			geradorCodigo.criarCodigo(Constantes.LABEL + auxrot, Constantes.NULL, Constantes.EMPTY);
		}

	}

	private void analisaComandos() throws excecaoSintatico, IOException, excecaoSemantico {
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

	private void analisaComandoSimples() throws excecaoSintatico, IOException, excecaoSemantico {
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

	private void analisaAtribChprocedimento() throws excecaoSintatico, IOException, excecaoSemantico {
		Token aux = token;
		getToken();

		if (token.getSimbolo().equals(Constantes.atribuicao)) {
			semantico.procuraVariavelOuFuncao(aux);
			analisaAtribuicao(aux);
		} else {
			chamadaProcedimento(aux);
		}
	}

	private void analisaAtribuicao(Token attributionToken) throws excecaoSintatico, IOException, excecaoSemantico {
		getToken();
		analisaExpressao();

		System.out.println("------ analisaAtribuicao expressao = " + expressao);
		String aux = semantico.expressaoToPostfix(expressao);

		String newExpression = semantico.formatarExpressao(aux);
		System.out.println("------ analisaAtribuicao");
		geradorCodigo.criarCodigo(newExpression);

		String type = semantico.retornaTipoDaExpressao(aux);
		semantico.quemMeChamou(type, attributionToken.getLexema());

		expressao.clear();

		if (flagListaFuncoes.get(flagListaFuncoes.size() - 1)
				&& (nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema())) {
			semantico.inserirTokenListaFuncao(attributionToken);
		}

		if (nameOfFunction.size() > 0) {
			if (!((nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema()))) {
				geradorCodigo.criarCodigo(Constantes.STR, semantico.posicaoVariavel(attributionToken.getLexema()),
						Constantes.EMPTY);
			}
		} else {
			geradorCodigo.criarCodigo(Constantes.STR, semantico.posicaoVariavel(attributionToken.getLexema()),
					Constantes.EMPTY);
		}
	}

	private void chamadaProcedimento(Token auxToken) throws excecaoSemantico {
		semantico.procuraProcedimento(auxToken);
		// se houver erro, dentro do sem�ntico lancar� a exce��o. Caso seja um
		// procedimento
		// v�lido, continuar� a excecu��o

		int labelResult = semantico.procuraProcedimentoLabel(auxToken);
		geradorCodigo.criarCodigo(Constantes.CALL, Constantes.LABEL + labelResult, Constantes.EMPTY);
	}

	private void chamadaFuncao(int index) throws excecaoSemantico, IOException, excecaoSintatico {
		String simboloLexema = semantico.getLexemaDoSimbolo(index);
		semantico.procurarVersao(new Token(Constantes.EMPTY, simboloLexema, token.getLinha()));
		// se houver erro, dentro do sem�ntico lancar� a exce��o. Caso seja uma funcao
		// v�lida, continuar� a excecu��o

		getToken();
	}

	private void analisaLeia() throws excecaoSintatico, IOException, excecaoSemantico {
		geradorCodigo.criarCodigo(Constantes.RD, Constantes.EMPTY, Constantes.EMPTY);
		getToken();
		if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {
				semantico.procuraVariavel(token);
				geradorCodigo.criarCodigo(Constantes.STR, semantico.posicaoVariavel(token.getLexema()),
						Constantes.EMPTY);
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

	private void analisaEscreva() throws excecaoSintatico, IOException, excecaoSemantico {
		getToken();
		if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {

				boolean eFuncao = semantico.procuraVariavelOuFuncao(token);

				if (eFuncao) {
					int resultadoLabel = semantico.procurarFuncaoLabel(token);
					geradorCodigo.criarCodigo(Constantes.CALL, Constantes.LABEL + resultadoLabel, Constantes.EMPTY);
					geradorCodigo.criarCodigo(Constantes.LDV,"0", Constantes.EMPTY);
				} else {
					// LDV de Vari�vel para o PRN
					String positionOfVariable = semantico.posicaoVariavel(token.getLexema());
					geradorCodigo.criarCodigo(Constantes.LDV, positionOfVariable, Constantes.EMPTY);
				}

				getToken();

				if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
					geradorCodigo.criarCodigo(Constantes.PRN, Constantes.EMPTY, Constantes.EMPTY);
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

	private void analisaEnquanto() throws excecaoSintatico, IOException, excecaoSemantico {
		int auxrot1, auxrot2;

		auxrot1 = label;
		geradorCodigo.criarCodigo(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
		label++;

		getToken();
		analisaExpressao();

		String aux = semantico.expressaoToPostfix(expressao);

		String novaExpressao = semantico.formatarExpressao(aux);
		System.out.println("------ analisaEnquanto");
		geradorCodigo.criarCodigo(novaExpressao);

		String tipo = semantico.retornaTipoDaExpressao(aux);
		semantico.quemMeChamou(tipo, Constantes.ENQUANTO);
		expressao.clear();

		if (token.getSimbolo().equals(Constantes.faca)) {
			auxrot2 = label;
			geradorCodigo.criarCodigo(Constantes.JMPF, Constantes.LABEL + label, Constantes.EMPTY);
			label++;

			getToken();
			analisaComandoSimples();

			geradorCodigo.criarCodigo(Constantes.JMP, Constantes.LABEL + auxrot1, Constantes.EMPTY);
			geradorCodigo.criarCodigo(Constantes.LABEL + auxrot2, Constantes.NULL, Constantes.EMPTY);
		} else {
			throw new excecaoSintatico(Constantes.faca, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaSe() throws excecaoSintatico, IOException, excecaoSemantico {
		int auxrot1, auxrot2;

		auxLabel++;
		if (flagListaFuncoes.get(flagListaFuncoes.size() - 1)) {
			semantico.inserirTokenListaFuncao(
					new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
		}

		getToken();
		analisaExpressao();

		String aux = semantico.expressaoToPostfix(expressao);

		String newExpression = semantico.formatarExpressao(aux);
		System.out.println("------ analisaSe");
		geradorCodigo.criarCodigo(newExpression);

		String type = semantico.retornaTipoDaExpressao(aux);
		semantico.quemMeChamou(type, Constantes.SE);
		expressao.clear();

		if (token.getSimbolo().equals(Constantes.entao)) {
			auxrot1 = label;
			geradorCodigo.criarCodigo(Constantes.JMPF, Constantes.LABEL + label, Constantes.EMPTY);
			label++;

			if (flagListaFuncoes.get(flagListaFuncoes.size() - 1)) {
				semantico.inserirTokenListaFuncao(
						new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
			}

			getToken();
			analisaComandoSimples();
			if (token.getSimbolo().equals(Constantes.senao)) {
				auxrot2 = label;
				geradorCodigo.criarCodigo(Constantes.JMP, Constantes.LABEL + label, Constantes.EMPTY);
				label++;

				geradorCodigo.criarCodigo(Constantes.LABEL + auxrot1, Constantes.NULL, Constantes.EMPTY);

				if (flagListaFuncoes.get(flagListaFuncoes.size() - 1)) {
					semantico.inserirTokenListaFuncao(
							new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
				}

				getToken();
				analisaComandoSimples();
				geradorCodigo.criarCodigo(Constantes.LABEL + auxrot2, Constantes.NULL, Constantes.EMPTY);
			} else {
				geradorCodigo.criarCodigo(Constantes.LABEL + auxrot1, Constantes.NULL, Constantes.EMPTY);
			}
		} else {
			throw new excecaoSintatico(Constantes.entao, token.getSimbolo(), token.getLinha());
		}
		if (flagListaFuncoes.get(flagListaFuncoes.size() - 1)) {
			semantico.verificarListaFuncao(String.valueOf(auxLabel));
		}
		auxLabel--;
	}

	private void analisaDeclaracaoProcedimento() throws excecaoSintatico, IOException, excecaoSemantico {
		flagListaProcedimento.add(true);

		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			semantico.procuraProcedimentoComMesmoNome(token);
			semantico.inserirProcedimentoOuFuncao(token, Constantes.PROCEDIMENTO, label);

			geradorCodigo.criarCodigo(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
			label++;

			getToken();
			if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
				analisaBloco();
			} else {
				throw new excecaoSintatico(Constantes.ponto_virgula, token.getSimbolo(), token.getLinha());
			}
		} else {
			throw new excecaoSintatico(Constantes.identificador, token.getSimbolo(), token.getLinha());
		}
		System.out.println("analisaDeclaracaoProcedimento - cleanTableLevel - Debug:");
//		semantico.debugTable();
		System.out.println("Fim - Debug");
		semantico.limparTabela();

		if (variaveisAlocadas.get(variaveisAlocadas.size() - 1) > 0) {
			posicao = posicao - variaveisAlocadas.get(variaveisAlocadas.size() - 1);
			System.out.println("analisaDeclaracaoProcedimento posicao = " + posicao + " | variaveisAlocadas.size() = "
					+ variaveisAlocadas.size());
			geradorCodigo.criarCodigo(Constantes.DALLOC, -1);
			variaveisAlocadas.remove(variaveisAlocadas.size() - 1);
		} else {
			variaveisAlocadas.remove(variaveisAlocadas.size() - 1);
		}

		geradorCodigo.criarCodigo(Constantes.RETURN, Constantes.EMPTY, Constantes.EMPTY);

		flagListaProcedimento.remove(flagListaProcedimento.size() - 1);
	}

	private void analisaDeclaracaoFuncao() throws excecaoSintatico, IOException, excecaoSemantico {
		flagListaFuncoes.add(true);

		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			semantico.procuraFuncoesComMesmoNome(token);
			semantico.inserirProcedimentoOuFuncao(token, Constantes.FUNCAO, label);

			geradorCodigo.criarCodigo(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
			label++;

			nameOfFunction.add(token.getLexema());
			semantico.setLinha(token.getLinha());

			getToken();

			if (token.getSimbolo().equals(Constantes.doispontos)) {
				getToken();

				if (token.getSimbolo().equals(Constantes.inteiro) || token.getSimbolo().equals(Constantes.booleano)) {
					if (token.getSimbolo().equals(Constantes.inteiro)) {
						semantico.inserirTipoEmFuncao(Constantes.INTEIRO);
					} else {
						semantico.inserirTipoEmFuncao(Constantes.BOOLEANO);
					}
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

		semantico.limparTabela();
		flagListaFuncoes.remove(flagListaFuncoes.size() - 1);
		semantico.verificaSeAFuncaoTemRetorno(nameOfFunction.get(nameOfFunction.size() - 1));
		nameOfFunction.remove(nameOfFunction.size() - 1);

		geradorCodigo.criarCodigo(Constantes.STR, "0", Constantes.EMPTY);

		if (variaveisAlocadas.get(variaveisAlocadas.size() - 1) > 0) {
			posicao = posicao - variaveisAlocadas.get(variaveisAlocadas.size() - 1);
			geradorCodigo.criarCodigo(Constantes.DALLOC, -1);
			variaveisAlocadas.remove(variaveisAlocadas.size() - 1);
		}

		geradorCodigo.criarCodigo(Constantes.RETURN, Constantes.EMPTY, Constantes.EMPTY);

		semantico.limparListaFuncao();
	}

	private void analisaExpressao() throws excecaoSintatico, IOException, excecaoSemantico {
		analisaExpressaoSimples();
		if (token.getSimbolo().equals(Constantes.maior) || token.getSimbolo().equals(Constantes.maiorig)
				|| token.getSimbolo().equals(Constantes.ig) || token.getSimbolo().equals(Constantes.menor)
				|| token.getSimbolo().equals(Constantes.menorig) || token.getSimbolo().equals(Constantes.dif)) {
			expressao.add(token);
			getToken();
			analisaExpressaoSimples();
		}
	}

	private void analisaExpressaoSimples() throws excecaoSintatico, IOException, excecaoSemantico {
		if (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)) {
			Token aux = new Token(token.getSimbolo(), token.getLexema() + "u", token.getLinha()); // passando o operador
			// un�rio para o
			// semantico
			expressao.add(aux);
			getToken();
		}
		analisaTermo();
		while (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)
				|| token.getSimbolo().equals(Constantes.ou)) {
			expressao.add(token);
			getToken();
			analisaTermo();
		}
	}

	private void analisaTermo() throws excecaoSintatico, IOException, excecaoSemantico {
		analisaFator();
		while (token.getSimbolo().equals(Constantes.mult) || token.getSimbolo().equals(Constantes.div)
				|| token.getSimbolo().equals(Constantes.e)) {
			expressao.add(token);
			getToken();
			analisaFator();
		}
	}

	private void analisaFator() throws excecaoSintatico, IOException, excecaoSemantico {
		if (token.getSimbolo().equals(Constantes.identificador)) {

			int index = semantico.procurarSimbolo(token);
			if (semantico.funcaoEValida(index)) {
				expressao.add(token);
				chamadaFuncao(index);
			} else {
				expressao.add(token);
				getToken();
			}

		} else if (token.getSimbolo().equals(Constantes.numero)) {
			expressao.add(token);
			getToken();
		} else if (token.getSimbolo().equals(Constantes.nao)) {
			expressao.add(token);
			getToken();
			analisaFator();
		} else if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			expressao.add(token);
			getToken();
			analisaExpressao();
			if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
				expressao.add(token);
				getToken();
			} else {
				throw new excecaoSintatico(Constantes.fecha_parenteses, token.getSimbolo(), token.getLinha());
			}
		} else if (token.getSimbolo().equals(Constantes.verdadeiro) || token.getSimbolo().equals(Constantes.falso)) {
			expressao.add(token);
			getToken();
		} else {
			throw new excecaoSintatico("Express�o incompleta na linha: " + token.getLinha());
		}
	}

	private void getToken() throws IOException, excecaoSintatico {
		token = lexico.AnalisadorLexico();
		if (token.getSimbolo().equals(Constantes.erro)) {
			throw new excecaoSintatico("Erro na parte Lexica.");
		} else {
			vetorTokens.add(new Token(token.getLexema(), token.getSimbolo(), token.getLinha()));
		}
	}

	// ----------------------------------------------------------------------------
	// Aqui pra baixo � a parte da interface
	// ----------------------------------------------------------------------------

	private void montarTabelas() throws IOException {
		MaquinaVirtual.vetorTokens = this.vetorTokens;
	}

	public void MostarMensagem(String mensagem) {
		MaquinaVirtual.ErroDoTryCath = String.valueOf(mensagem);
	}

	public void TabelaSintatico() {

	}

	public void TabelaInstrucoes2() throws IOException {

	}

}
