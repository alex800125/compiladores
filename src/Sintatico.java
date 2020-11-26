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

public class Sintatico extends MaquinaVirtual {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Lexico lexico = new Lexico();
	Semantico semantic = new Semantico();
	GeradorCodigo generator = new GeradorCodigo();
	Token token = null;
	BufferedReader br2;
	protected Vector<Token> vetorTokens = new Vector<Token>();

	// São as posições na memória que está alocado a variável
	private int position = 0;

	// São as nominações para onde será realizado o pulo (Ex: L"0", L"1", etc)
	private int label = 0;
	private int auxLabel = 0;

	private List<Token> expression = new ArrayList<Token>();

	private int countVariable = 0;

	private List<Integer> variableOfAlloc = new ArrayList<Integer>();
	private List<Boolean> flagProcedureList = new ArrayList<Boolean>();
	private List<Boolean> flagFunctionList = new ArrayList<Boolean>();
	private List<String> nameOfFunction = new ArrayList<String>();

	public void analisadorSintatico() throws excecaoSintatico, IOException, excecaoSemantico {

		try {
			flagFunctionList.add(false);
			flagProcedureList.add(false);
			br2 = lexico.InicializadorArquivo();
			
			getToken();
			if (token.getSimbolo().equals(Constantes.programa)) {
				generator.createCode(Constantes.START, Constantes.EMPTY, Constantes.EMPTY);
				getToken();
				if (token.getSimbolo().equals(Constantes.identificador)) {
					semantic.insertProgram(token);
					getToken();
					System.out.println("analisadorSintatico 1 token = " + token.getSimbolo());
					if (token.getSimbolo().equals(Constantes.ponto_virgula)) {
						analisaBloco();
						System.out.println("analisadorSintatico 2 token = " + token.getSimbolo());
						if (token.getSimbolo().equals(Constantes.ponto)) {
							// esse Token não usa a função getToken pq ele serve para verificar se o arquivo
							// fechou ao fim, depois do Sponto, não entrando na lista de Tokens
							token = lexico.AnalisadorLexico();
							if (token.getSimbolo().equals(Constantes.eof)) {

								generator.createCode(Constantes.HLT, Constantes.EMPTY, Constantes.EMPTY);
								generator.createFile();

								semantic.cleanTableLevel();

								System.out.println("Fim do programa, sucesso");

								montarTabelas();
								// Seta a cor como verde, que significa que tudo ocorreu bem
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
		} catch (excecaoSintatico | excecaoSemantico e) {
			System.out.println(e);
			montarTabelas();
			textErroSintatico.setBackground(Color.RED);
			MostarMensagem(String.valueOf(e));

		}

		System.out.println("Fim do sintatico");
	}

	private void analisaBloco() throws excecaoSintatico, IOException, excecaoSemantico {
		getToken();

		analisaEtVariaveis();
		analisaSubrotinas();
		analisaComandos();

		System.out.println("analisaBloco variableOfAlloc = " + variableOfAlloc.size() + " | flagProcedureList = " + flagProcedureList.size());
		if (variableOfAlloc.size() > 0 && (!flagProcedureList.get(flagProcedureList.size() - 1))
				&& (!flagFunctionList.get(flagFunctionList.size() - 1))) {
			if (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
				position = position - variableOfAlloc.get(variableOfAlloc.size() - 1);
				generator.createCode(Constantes.DALLOC, -1);
				variableOfAlloc.remove(variableOfAlloc.size() - 1);
			} else {
				variableOfAlloc.remove(variableOfAlloc.size() - 1);
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

		variableOfAlloc.add(countVariable);
		countVariable = 0;

		if (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			generator.createCode(Constantes.ALLOC, variableOfAlloc.get(variableOfAlloc.size() - 1));
		}
	}

	private void analisaVariaveis() throws excecaoSintatico, IOException, excecaoSemantico {
		do {
			if (token.getSimbolo().equals(Constantes.identificador)) {

				semantic.searchInTableOfSymbols(token);
				semantic.insertVariable(token, position);
				countVariable++;
				position++;

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
			semantic.insertTypeOnVariable(token);
		}
		getToken();
	}

	private void analisaSubrotinas() throws excecaoSintatico, IOException, excecaoSemantico {
		int auxrot = 0, flag = 0;

		if ((token.getSimbolo().equals(Constantes.procedimento)) || (token.getSimbolo().equals(Constantes.funcao))) {
			auxrot = label;
			generator.createCode(Constantes.JMP, Constantes.LABEL + label, Constantes.EMPTY);
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
			generator.createCode(Constantes.LABEL + auxrot, Constantes.NULL, Constantes.EMPTY);
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
			semantic.searchVariableOrFunction(aux);
			analisaAtribuicao(aux);
		} else {
			chamadaProcedimento(aux);
		}
	}

	private void analisaAtribuicao(Token attributionToken) throws excecaoSintatico, IOException, excecaoSemantico {
		getToken();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);

		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);

		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, attributionToken.getLexema());

		expression.clear();

		if (flagFunctionList.get(flagFunctionList.size() - 1)
				&& (nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema())) {
			semantic.insertTokenOnFunctionList(attributionToken);
		}

		if (nameOfFunction.size() > 0) {
			if (!((nameOfFunction.get(nameOfFunction.size() - 1)).equals(attributionToken.getLexema()))) {
				generator.createCode(Constantes.STR, semantic.positionOfVariable(attributionToken.getLexema()),
						Constantes.EMPTY);
			}
		} else {
			generator.createCode(Constantes.STR, semantic.positionOfVariable(attributionToken.getLexema()),
					Constantes.EMPTY);
		}
	}

	private void chamadaProcedimento(Token auxToken) throws excecaoSemantico {
		semantic.searchProcedure(auxToken);
		// se houver erro, dentro do semântico lancará a exceção. Caso seja um
		// procedimento
		// válido, continuará a excecução

		int labelResult = semantic.searchProcedureLabel(auxToken);
		generator.createCode(Constantes.CALL, Constantes.LABEL + labelResult, Constantes.EMPTY);
	}

	private void chamadaFuncao(int index) throws excecaoSemantico, IOException, excecaoSintatico {
		String symbolLexema = semantic.getLexemaOfSymbol(index);
		semantic.searchFunction(new Token(Constantes.EMPTY, symbolLexema, token.getLinha()));
		// se houver erro, dentro do semântico lancará a exceção. Caso seja uma funcao
		// válida, continuará a excecução

		getToken();
	}

	private void analisaLeia() throws excecaoSintatico, IOException, excecaoSemantico {
		generator.createCode(Constantes.RD, Constantes.EMPTY, Constantes.EMPTY);
		getToken();
		if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			getToken();
			if (token.getSimbolo().equals(Constantes.identificador)) {
				semantic.searchVariable(token);
				generator.createCode(Constantes.STR, semantic.positionOfVariable(token.getLexema()), Constantes.EMPTY);
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

				boolean isFunction = semantic.searchVariableOrFunction(token);

				if (isFunction) {
					int labelResult = semantic.searchFunctionLabel(token);
					generator.createCode(Constantes.CALL, Constantes.LABEL + labelResult, Constantes.EMPTY);
				} else {
					// LDV de Variável para o PRN
					String positionOfVariable = semantic.positionOfVariable(token.getLexema());
					generator.createCode(Constantes.LDV, positionOfVariable, Constantes.EMPTY);
				}

				getToken();

				if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
					generator.createCode(Constantes.PRN, Constantes.EMPTY, Constantes.EMPTY);
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
		generator.createCode(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
		label++;

		getToken();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);

		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);

		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constantes.ENQUANTO);
		expression.clear();

		if (token.getSimbolo().equals(Constantes.faca)) {
			auxrot2 = label;
			generator.createCode(Constantes.JMPF, Constantes.LABEL + label, Constantes.EMPTY);
			label++;

			getToken();
			analisaComandoSimples();

			generator.createCode(Constantes.JMP, Constantes.LABEL + auxrot1, Constantes.EMPTY);
			generator.createCode(Constantes.LABEL + auxrot2, Constantes.NULL, Constantes.EMPTY);
		} else {
			throw new excecaoSintatico(Constantes.faca, token.getSimbolo(), token.getLinha());
		}
	}

	private void analisaSe() throws excecaoSintatico, IOException, excecaoSemantico {
		int auxrot1, auxrot2;

		auxLabel++;
		if (flagFunctionList.get(flagFunctionList.size() - 1)) {
			semantic.insertTokenOnFunctionList(
					new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
		}

		getToken();
		analisaExpressao();

		String aux = semantic.expressionToPostfix(expression);

		String newExpression = semantic.formatExpression(aux);
		generator.createCode(newExpression);

		String type = semantic.returnTypeOfExpression(aux);
		semantic.whoCallsMe(type, Constantes.SE);
		expression.clear();

		if (token.getSimbolo().equals(Constantes.entao)) {
			auxrot1 = label;
			generator.createCode(Constantes.JMPF, Constantes.LABEL + label, Constantes.EMPTY);
			label++;

			if (flagFunctionList.get(flagFunctionList.size() - 1)) {
				semantic.insertTokenOnFunctionList(
						new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
			}

			getToken();
			analisaComandoSimples();
			if (token.getSimbolo().equals(Constantes.senao)) {
				auxrot2 = label;
				generator.createCode(Constantes.JMP, Constantes.LABEL + label, Constantes.EMPTY);
				label++;

				generator.createCode(Constantes.LABEL + auxrot1, Constantes.NULL, Constantes.EMPTY);

				if (flagFunctionList.get(flagFunctionList.size() - 1)) {
					semantic.insertTokenOnFunctionList(
							new Token(token.getSimbolo(), token.getLexema() + auxLabel, token.getLinha()));
				}

				getToken();
				analisaComandoSimples();
				generator.createCode(Constantes.LABEL + auxrot2, Constantes.NULL, Constantes.EMPTY);
			} else {
				generator.createCode(Constantes.LABEL + auxrot1, Constantes.NULL, Constantes.EMPTY);
			}
		} else {
			throw new excecaoSintatico(Constantes.entao, token.getSimbolo(), token.getLinha());
		}
		if (flagFunctionList.get(flagFunctionList.size() - 1)) {
			semantic.verifyFunctionList(String.valueOf(auxLabel));
		}
		auxLabel--;
	}

	private void analisaDeclaracaoProcedimento() throws excecaoSintatico, IOException, excecaoSemantico {
		flagProcedureList.add(true);

		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			semantic.searchProcedureWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constantes.PROCEDIMENTO, label);

			generator.createCode(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
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
		semantic.cleanTableLevel();

		if (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			position = position - variableOfAlloc.get(variableOfAlloc.size() - 1);
			generator.createCode(Constantes.DALLOC, -1);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		} else {
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}

		generator.createCode(Constantes.RETURN, Constantes.EMPTY, Constantes.EMPTY);

		flagProcedureList.remove(flagProcedureList.size() - 1);
	}

	private void analisaDeclaracaoFuncao() throws excecaoSintatico, IOException, excecaoSemantico {
		flagFunctionList.add(true);

		getToken();
		if (token.getSimbolo().equals(Constantes.identificador)) {
			semantic.searchFunctionWithTheSameName(token);
			semantic.insertProcOrFunc(token, Constantes.FUNCAO, label);

			generator.createCode(Constantes.LABEL + label, Constantes.NULL, Constantes.EMPTY);
			label++;

			nameOfFunction.add(token.getLexema());
			semantic.setLine(token.getLinha());

			getToken();

			if (token.getSimbolo().equals(Constantes.doispontos)) {
				getToken();

				if (token.getSimbolo().equals(Constantes.inteiro) || token.getSimbolo().equals(Constantes.booleano)) {
					if (token.getSimbolo().equals(Constantes.inteiro)) {
						semantic.insertTypeOnFunction(Constantes.inteiro);
					} else {
						semantic.insertTypeOnFunction(Constantes.booleano);
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

		semantic.cleanTableLevel();
		flagFunctionList.remove(flagFunctionList.size() - 1);
		semantic.thisFunctionHasReturn(nameOfFunction.get(nameOfFunction.size() - 1));
		nameOfFunction.remove(nameOfFunction.size() - 1);

		if (variableOfAlloc.get(variableOfAlloc.size() - 1) > 0) {
			position = position - variableOfAlloc.get(variableOfAlloc.size() - 1);
			generator.createCode(Constantes.RETURNF, -1);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		} else {
			generator.createCode(Constantes.RETURNF, 0);
			variableOfAlloc.remove(variableOfAlloc.size() - 1);
		}
		semantic.clearFunctionList();
	}

	private void analisaExpressao() throws excecaoSintatico, IOException, excecaoSemantico {
		analisaExpressaoSimples();
		if (token.getSimbolo().equals(Constantes.maior) || token.getSimbolo().equals(Constantes.maiorig)
				|| token.getSimbolo().equals(Constantes.ig) || token.getSimbolo().equals(Constantes.menor)
				|| token.getSimbolo().equals(Constantes.menorig) || token.getSimbolo().equals(Constantes.dif)) {
			expression.add(token);
			getToken();
			analisaExpressaoSimples();
		}
	}

	private void analisaExpressaoSimples() throws excecaoSintatico, IOException, excecaoSemantico {
		if (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)) {
			Token aux = new Token(token.getSimbolo(), token.getLexema() + "u", token.getLinha()); // passando o operador
			// unário para o
			// semantico
			expression.add(aux);
			getToken();
		}
		analisaTermo();
		while (token.getSimbolo().equals(Constantes.mais) || token.getSimbolo().equals(Constantes.menos)
				|| token.getSimbolo().equals(Constantes.ou)) {
			expression.add(token);
			getToken();
			analisaTermo();
		}
	}

	private void analisaTermo() throws excecaoSintatico, IOException, excecaoSemantico {
		analisaFator();
		while (token.getSimbolo().equals(Constantes.mult) || token.getSimbolo().equals(Constantes.div)
				|| token.getSimbolo().equals(Constantes.e)) {
			expression.add(token);
			getToken();
			analisaFator();
		}
	}

	private void analisaFator() throws excecaoSintatico, IOException, excecaoSemantico {
		if (token.getSimbolo().equals(Constantes.identificador)) {

			int index = semantic.searchSymbol(token);
			if (semantic.isValidFunction(index)) {
				expression.add(token);
				chamadaFuncao(index);
			} else {
				expression.add(token);
				getToken();
			}

		} else if (token.getSimbolo().equals(Constantes.numero)) {
			expression.add(token);
			getToken();
		} else if (token.getSimbolo().equals(Constantes.nao)) {
			expression.add(token);
			getToken();
			analisaFator();
		} else if (token.getSimbolo().equals(Constantes.abre_parenteses)) {
			expression.add(token);
			getToken();
			analisaExpressao();
			if (token.getSimbolo().equals(Constantes.fecha_parenteses)) {
				expression.add(token);
				getToken();
			} else {
				throw new excecaoSintatico(Constantes.fecha_parenteses, token.getSimbolo(), token.getLinha());
			}
		} else if (token.getSimbolo().equals(Constantes.verdadeiro) || token.getSimbolo().equals(Constantes.falso)) {
			expression.add(token);
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
