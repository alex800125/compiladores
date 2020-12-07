import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import Excecoes.excecaoSemantico;
import Simbolos.Funcao;
import Simbolos.Procedimento;
import Simbolos.Simbolos;
import Simbolos.TabelaSimbolos;
import Simbolos.Variavel;

public class Semantico {
	private TabelaSimbolos tabelaDeSimbolos;

	private ArrayList<Token> functionTokenList = new ArrayList<Token>();
	private int LinhaSemRetorno;
	private int linha;
	private boolean erro;

	public Semantico() {
		tabelaDeSimbolos = new TabelaSimbolos();
	}

	/* M�todos envolvidos com a Tabela de S�mbolos */

	/* M�todos de inser��o */

	// Programa
	public void inserePrograma(Token token) {
		tabelaDeSimbolos.inserir(new Simbolos(token.getLexema(), -1, -1));
	}

	// Procedimento ou Fun��o
	public void insertProcOrFunc(Token token, String type, int label) {
		if (Constantes.PROCEDIMENTO.equals(type)) {
			tabelaDeSimbolos.inserir(new Procedimento(token.getLexema(), label));
		} else if (Constantes.FUNCAO.equals(type)) {
			tabelaDeSimbolos.inserir(new Funcao(token.getLexema(), label));
		}
	}

	// Vari�vel
	public void insertVariable(Token token, int position) {
		tabelaDeSimbolos.inserir(new Variavel(token.getLexema(), position));
	}

	// Tipo Fun��o
	public void insertTypeOnFunction(String type) {
		tabelaDeSimbolos.inserirTipoFuncao(type);
	}

	// Tipo Vari�vel
	public void insertTypeOnVariable(Token token) {
		tabelaDeSimbolos.inserirTipoVariavel(token.getLexema());
	}

	/* M�todos de busca */

	public void searchFunction(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"Fun��o '" + token.getLexema() + "' n�o est� declarada.\nLinha: " + token.getLinha());
		}
	}

	public int searchFunctionLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Fun��o '" + token.getLexema() + "' n�o est� declarada.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void searchFunctionWithTheSameName(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procurarFuncao(token.getLexema())) {
			throw new excecaoSemantico("J� existe uma fun��o com o mesmo nome da fun��o da linha: " + token.getLinha());
		}
	}

	public void searchInTableOfSymbols(Token token) throws excecaoSemantico {

		if (tabelaDeSimbolos.procurar(token.getLexema())) {
			throw new excecaoSemantico(
					"J� existe uma vari�vel com o mesmo nome da vari�vel da linha: " + token.getLinha());
		}
	}

	public void searchProcedure(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarProcedimento(token.getLexema()))) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' n�o est� declarado.\nLinha: " + token.getLinha());
		}
	}

	public int searchProcedureLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarProcedimentoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' n�o est� declarado.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void searchProcedureWithTheSameName(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procurarProcedimento(token.getLexema())) {
			throw new excecaoSemantico(
					"J� existe um procedimento com o mesmo nome do procedimento da linha: " + token.getLinha());
		}
	}

	public int searchSymbol(Token token) throws excecaoSemantico {
		int index = tabelaDeSimbolos.procurarSimbolo(token.getLexema());

		if (index >= 0) {
			return index;
		}

		throw new excecaoSemantico("Vari�vel ou Fun��o '" + token.getLexema()
				+ "' n�o est� definida no escopo atual. \n Linha: " + token.getLinha());
	}

	public void searchVariable(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarVariavel(token.getLexema()))) {
			throw new excecaoSemantico(
					"A vari�vel " + token.getLexema() + " n�o est� definida.\nLinha: " + token.getLinha());
		}
	}

	public boolean searchVariableOrFunction(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarVariavel(token.getLexema())
				|| tabelaDeSimbolos.procurarFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"A vari�vel ou fun��o " + token.getLexema() + " n�o est� definida.\nLinha: " + token.getLinha());
		} else {
			// Vari�vel
			if (tabelaDeSimbolos.procurarVariavel(token.getLexema())) {
				return false;
			}
			// Fun��o
			return true;
		}
	}

	/* M�todos de recupera��o */
	public String getLexemaOfSymbol(int index) {
		return tabelaDeSimbolos.getSimbolo(index).getLexema();
	}

	/* Outros m�todos sobre a tabela de simbolos */
	public boolean isValidFunction(int index) {
		if (tabelaDeSimbolos.getSimbolo(index) instanceof Funcao
				&& (tabelaDeSimbolos.getSimbolo(index).getType() == Constantes.INTEIRO
						|| tabelaDeSimbolos.getSimbolo(index).getType() == Constantes.BOOLEANO)) {
			return true;
		}

		return false;
	}

	public void cleanTableLevel() {
		tabelaDeSimbolos.limparLevel();
	}

	/* ============ M�todos Sem�nticos ============ */

	public String expressionToPostfix(List<Token> expression) {

		List<String> stack = new ArrayList<String>();
		String retorno = "";

		for (int a = 0; a < expression.size(); a++) {

			String parcel = expression.get(a).getLexema();
			String simbolo = expression.get(a).getSimbolo();
			this.linha = expression.get(a).getLinha();

			if (Constantes.numero.equals(simbolo) || Constantes.identificador.equals(simbolo)
					|| Constantes.verdadeiro.equals(simbolo) || Constantes.falso.equals(simbolo)) {

				retorno = retorno.concat(parcel + " ");
			} else if (Constantes.abre_parenteses.equals(simbolo)) {

				stack.add(parcel);

			} else if (Constantes.fecha_parenteses.equals(simbolo)) {

				int stackTop = stack.size() - 1;

				while (!(Constantes.ABRE_PARENTESES.equals(stack.get(stackTop)))) {
					retorno = retorno.concat(stack.get(stackTop) + " ");
					stack.remove(stackTop);
					stackTop--;
				}
				stack.remove(stackTop); // remove o abre parenteses sem inclui-lo na saida

			} else {
				if (stack.isEmpty()) {

					stack.add(parcel);

				} else {
					int newOperatorPriority;
					int stackTopOperatorPriority;
					int stackTop = stack.size() - 1;
					do {

						newOperatorPriority = definicaoPrioridadeOperador(parcel);
						stackTopOperatorPriority = definicaoPrioridadeOperador(stack.get(stackTop));

						if (stackTopOperatorPriority >= newOperatorPriority) {
							retorno = retorno.concat(stack.get(stackTop) + " ");
							stack.remove(stackTop);
							stackTop--;
						}

					} while (stackTopOperatorPriority >= newOperatorPriority && !(stack.isEmpty()));

					if (stackTopOperatorPriority < newOperatorPriority || stack.isEmpty()) {

						stack.add(parcel);

					}
				}
			}

		}

		int stackTop = stack.size() - 1;
		if (!stack.isEmpty()) {
			for (int i = stackTop; i >= 0; i--) {
				retorno = retorno.concat(stack.get(i) + " ");
				stack.remove(i);
			}
		}
		System.out.println("Sa�da: " + retorno);
		return retorno;
	}

	public String returnTypeOfExpression(String expression) throws excecaoSemantico {
		String type = separatePostFixExpression(expression);

		if (type == "0") {
			return Constantes.INTEIRO;
		} else {
			return Constantes.BOOLEANO;
		}
	}

	private String separatePostFixExpression(String expression) throws excecaoSemantico {
		System.out.println("expression: " + expression);
		String[] aux = expression.split(" ");
		List<String> expressionList = new ArrayList<String>(Arrays.asList(aux));

		for (int j = 0; j < expressionList.size(); j++) {
			String parcel = expressionList.get(j);
//			System.out.println("parcel: " + parcel);

			if (!(verificaOperador(parcel)) && !(isUnaryOperator(parcel))) {
				if (Constantes.INTEIRO.equals(tabelaDeSimbolos.procurarTipoVariavelOuProcedimento(parcel))) {
					System.out.println("parcel: " + parcel);
					expressionList.set(j, "0");
				} else if (Constantes.BOOLEANO.equals(tabelaDeSimbolos.procurarTipoVariavelOuProcedimento(parcel))) {
					expressionList.set(j, "1");
				} else if (Constantes.VERDADEIRO.equals(parcel) || Constantes.FALSO.equals(parcel)) {
					expressionList.set(j, "1");
				} else {
					expressionList.set(j, "0");
				}
			}

		}

		for (int i = 0; i < expressionList.size(); i++) {
			System.out.println("expressionList.get(i) = " + expressionList.get(i));
			if (verificaOperador(expressionList.get(i))) {

				String operation = retornoTipoOperacao(expressionList.get(i - 2), expressionList.get(i - 1),
						expressionList.get(i));

				expressionList.remove(i);
				expressionList.remove(i - 1);
				expressionList.remove(i - 2);
				expressionList.add(i - 2, operation);

				i = 0;
			} else if (isUnaryOperator(expressionList.get(i))) {
				String operation = retornoTipoOperacao(expressionList.get(i - 1), null, expressionList.get(i));

				expressionList.remove(i);
				expressionList.remove(i - 1);
				expressionList.add(i - 1, operation);

				i = 0;
			}
		}
		return expressionList.get(0);
	}

	private boolean verificaOperador(String parcel) {

		if (Constantes.MULTIPLICACAO_SINAL.equals(parcel) || Constantes.DIVISAO_SINAL.equals(parcel)
				|| Constantes.MAIS_SINAL.equals(parcel) || Constantes.MENOS_SINAL.equals(parcel)
				|| Constantes.MAIOR_SINAL.equals(parcel) || Constantes.MENOR_SINAL.equals(parcel)
				|| Constantes.MAIOR_IGUAL_SINAL.equals(parcel) || Constantes.MENOR_IGUAL_SINAL.equals(parcel)
				|| Constantes.IGUAL_SINAL.equals(parcel) || Constantes.DIFERENTE_SINAL.equals(parcel)
				|| Constantes.E_SINAL.equals(parcel) || Constantes.OU_SINAL.equals(parcel)) {

			return true;
		}

		return false;
	}

	private String retornoTipoOperacao(String firstType, String secondType, String operator) throws excecaoSemantico {
		// 0 representa um tipo inteiro
		// 1 representa um tipo booleano

		if (verificaOperador(operator)) {
			if (verificaOperadorMatematico(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "0";
				}

				throw new excecaoSemantico(
						"Opera��es aritm�ticas devem envolver vari�veis inteiras.\n" + "Linha: " + linha);
			} else if (verificaOperadorRelacional(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "1";
				}

				throw new excecaoSemantico(
						"Opera��es relacionais (!= | = | < | <= | > | >=) devem envolver vari�veis inteiras.\n"
								+ "Linha: " + linha);
			} else {
				if (firstType == "1" && secondType == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Opera��es l�gicas (e | ou) devem envolver vari�veis booleanas.\n" + "Linha: " + linha);
			}
		} else {
			if (verificaOperadorUnarioMatematico(operator)) {
				if (firstType == "0") {
					return "0";
				}

				throw new excecaoSemantico(
						"Opera��es envolvendo operadores un�rios (+ | -) devem ser com vari�veis inteiras.\n"
								+ "Linha: " + linha);
			} else {
				if (firstType == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Opera��es envolvendo operador un�rio (N�O) devem ser com vari�veis booleanas.\n" + "Linha: "
								+ linha);
			}
		}
	}

	private boolean isUnaryOperator(String parcel) {

		if (Constantes.MAIS_UNARIO.equals(parcel) || Constantes.MENOS_UNARIO.equals(parcel)
				|| Constantes.NAO.equals(parcel)) {
			return true;
		}

		return false;
	}

	private boolean verificaOperadorUnarioMatematico(String parcel) {

		if (Constantes.MAIS_UNARIO.equals(parcel) || Constantes.MENOS_UNARIO.equals(parcel)) {
			return true;
		}

		return false;
	}

	private boolean verificaOperadorMatematico(String parcel) {

		if (Constantes.MULTIPLICACAO_SINAL.equals(parcel) || Constantes.DIVISAO_SINAL.equals(parcel)
				|| Constantes.MAIS_SINAL.equals(parcel) || Constantes.MENOS_SINAL.equals(parcel)) {

			return true;
		}

		return false;
	}

	private boolean verificaOperadorRelacional(String parcel) {

		if (Constantes.MAIOR_SINAL.equals(parcel) || Constantes.MENOR_SINAL.equals(parcel)
				|| Constantes.MAIOR_IGUAL_SINAL.equals(parcel) || Constantes.MENOR_IGUAL_SINAL.equals(parcel)
				|| Constantes.IGUAL_SINAL.equals(parcel) || Constantes.DIFERENTE_SINAL.equals(parcel)) {

			return true;
		}

		return false;
	}

	private int definicaoPrioridadeOperador(String operador) {
		if (Constantes.MAIS_UNARIO.equals(operador) || Constantes.MENOS_UNARIO.equals(operador)
				|| Constantes.NAO.equals(operador)) {
			return 5;
		} else if (Constantes.MULTIPLICACAO_SINAL.equals(operador) || Constantes.DIVISAO_SINAL.equals(operador)) {
			return 4;
		} else if (Constantes.MAIS_SINAL.equals(operador) || Constantes.MENOS_SINAL.equals(operador)) {
			return 3;
		} else if (Constantes.MAIOR_SINAL.equals(operador) || Constantes.MENOR_SINAL.equals(operador)
				|| Constantes.MAIOR_IGUAL_SINAL.equals(operador) || Constantes.MENOR_IGUAL_SINAL.equals(operador)
				|| Constantes.IGUAL_SINAL.equals(operador) || Constantes.DIFERENTE_SINAL.equals(operador)) {
			return 2;
		} else if (Constantes.E_SINAL.equals(operador)) {
			return 1;
		} else if (Constantes.OU_SINAL.equals(operador)) {
			return 0;
		}

		return -1;
	}

	public void quemMeChamou(String tipo, String quemChamou) throws excecaoSemantico {
		System.out.println("tipo = " + tipo + " | quemChamou = " + quemChamou);
		if (Constantes.SE.equals(quemChamou) || Constantes.ENQUANTO.equals(quemChamou)) {
			if (!(Constantes.BOOLEANO.equals(tipo))) {
				throw new excecaoSemantico(
						"A condi��o presente no '" + quemChamou.toUpperCase() + "' deveria resultar num tipo booleano");
			}
		} else {
			String tipoQuemChamou = tabelaDeSimbolos.procurarTipoVariavelOuProcedimento(quemChamou);

			if (!(tipo.equals(tipoQuemChamou))) {
				throw new excecaoSemantico("N�o � poss�vel realizar a atribui��o de uma express�o do tipo " + tipo
						+ " em uma vari�vel/fun��o do tipo " + tipoQuemChamou);
			}
		}
	}

	public String posicaoVariavel(String variavel) {
		int posicao = tabelaDeSimbolos.procurarPosicaoVariavel(variavel);

		return Integer.toString(posicao);
	}

	// Formata a express�o para usar na gera��o de c�digo
	public String formatarExpressao(String expressao) {
		String[] aux = expressao.split(" ");
		String novaExpressao = "";
		int auxPosicao;

		for (int i = 0; i < aux.length; i++) {
			if (!tabelaDeSimbolos.procurarFuncao(aux[i])) {
				auxPosicao = tabelaDeSimbolos.procurarPosicaoVariavel(aux[i]);

				if (auxPosicao != -1) {
					novaExpressao = novaExpressao.concat("p" + auxPosicao + " ");
				} else {
					novaExpressao = novaExpressao.concat(aux[i] + " ");
				}
			} else {
				int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(aux[i]);
				novaExpressao = novaExpressao.concat("funcao" + labelResult + " ");
			}

		}

		return novaExpressao;
	}

	/* M�todos envolvendo o retorno de fun��o */

	public void inserirTokenListaFuncao(Token token) {
		functionTokenList.add(token);
	}

	public void verificarListaFuncao(String label) {
		Token auxToken = null;

		boolean conditionalThenReturn = false;
		boolean conditionalElseReturn = false;
		int thenPosition = -1;
		int elsePosition = thenPosition;

		for (int i = 0; i < functionTokenList.size(); i++) {
			if (Constantes.se.equals(functionTokenList.get(i).getSimbolo())
					&& functionTokenList.get(i).getLexema().contains(label)) {
				functionTokenList.remove(i);
				i--;
			} else if (Constantes.entao.equals(functionTokenList.get(i).getSimbolo())
					&& functionTokenList.get(i).getLexema().contains(label)) {
				if (functionTokenList.size() > (i + 1)) {
					if (Constantes.identificador.equals(functionTokenList.get(i + 1).getSimbolo())) {
						conditionalThenReturn = true;
						auxToken = functionTokenList.get(i + 1);
					}
				} else {
					LinhaSemRetorno = functionTokenList.get(i).getLinha();
				}
				thenPosition = i;
			} else if (Constantes.senao.equals(functionTokenList.get(i).getSimbolo())
					&& functionTokenList.get(i).getLexema().contains(label)) {
				if (functionTokenList.size() > (i + 1)) {
					if (Constantes.identificador.equals(functionTokenList.get(i + 1).getSimbolo())) {
						conditionalElseReturn = true;
						elsePosition = i + 1;
						auxToken = functionTokenList.get(i + 1);
					}
				} else {
					LinhaSemRetorno = functionTokenList.get(i).getLinha();
					elsePosition = i;
				}

			}
		}

		if (elsePosition == (-1))
			elsePosition = functionTokenList.size() - 1;

		removeIf(elsePosition, thenPosition, (conditionalThenReturn && conditionalElseReturn), auxToken);
	}

	public boolean thisFunctionHasReturn(String nameOfFunction) throws excecaoSemantico {
		int aux = 0;
		System.out.println("functionTokenList.size() = " + functionTokenList.size());
		for (int i = 0; i < functionTokenList.size(); i++) {
			if (nameOfFunction.equals(functionTokenList.get(i).getLexema())) {
				aux++;
				if (aux == functionTokenList.size()) {
					return true;
				}
			}
		}

		erro = true;
		if (LinhaSemRetorno != 0)
			linha = LinhaSemRetorno;

		throw new excecaoSemantico("Nem todos os caminhos poss�veis da fun��o possuem retorno." + "\nLinha: " + linha);
	}

	private void removeIf(int start, int end, boolean functionReturn, Token tokenFunction) {
		for (int i = start; i >= end; i--) {
			functionTokenList.remove(i);
		}

		if (functionReturn && tokenFunction != null) {
			functionTokenList.add(tokenFunction);
		}
	}

	public void clearFunctionList() {
		functionTokenList.clear();
	}

	public void debugTableFunction() {
		for (int i = 0; i < functionTokenList.size(); i++) {
			System.out.println(functionTokenList.get(i).getLexema());
		}
	}
	//

	public void setLine(int line) {
		this.linha = line;
	}

	public int getLine() {
		return linha;
	}

	public boolean hasError() {
		return erro;
	}

	// Debug Tabela de Simbolos

	public void debugTable() {
		tabelaDeSimbolos.debugTable();
	}

}