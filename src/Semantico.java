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
	private int lineWithoutReturn;
	private int line;
	private boolean error;

	public Semantico() {
		tabelaDeSimbolos = new TabelaSimbolos();
	}

	/* Métodos envolvidos com a Tabela de Símbolos */

	/* Métodos de inserção */

	// Programa
	public void inserePrograma(Token token) {
		tabelaDeSimbolos.inserir(new Simbolos(token.getLexema(), -1, -1));
	}

	// Procedimento ou Função
	public void insertProcOrFunc(Token token, String type, int label) {
		if (Constantes.PROCEDIMENTO.equals(type)) {
			tabelaDeSimbolos.inserir(new Procedimento(token.getLexema(), label));
		} else if (Constantes.FUNCAO.equals(type)) {
			tabelaDeSimbolos.inserir(new Funcao(token.getLexema(), label));
		}
	}

	// Variável
	public void insertVariable(Token token, int position) {
		tabelaDeSimbolos.inserir(new Variavel(token.getLexema(), position));
	}

	// Tipo Função
	public void insertTypeOnFunction(String type) {
		tabelaDeSimbolos.inserirTipoFuncao(type);
	}

	// Tipo Variável
	public void insertTypeOnVariable(Token token) {
		tabelaDeSimbolos.inserirTipoVariavel(token.getLexema());
	}

	/* Métodos de busca */

	public void searchFunction(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLinha());
		}
	}

	public int searchFunctionLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void searchFunctionWithTheSameName(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procurarFuncao(token.getLexema())) {
			throw new excecaoSemantico("Já existe uma função com o mesmo nome da função da linha: " + token.getLinha());
		}
	}

	public void searchInTableOfSymbols(Token token) throws excecaoSemantico {

		if (tabelaDeSimbolos.procurar(token.getLexema())) {
			throw new excecaoSemantico(
					"Já existe uma variável com o mesmo nome da variável da linha: " + token.getLinha());
		}
	}

	public void searchProcedure(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarProcedimento(token.getLexema()))) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLinha());
		}
	}

	public int searchProcedureLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarProcedimentoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void searchProcedureWithTheSameName(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procurarProcedimento(token.getLexema())) {
			throw new excecaoSemantico(
					"Já existe um procedimento com o mesmo nome do procedimento da linha: " + token.getLinha());
		}
	}

	public int searchSymbol(Token token) throws excecaoSemantico {
		int index = tabelaDeSimbolos.procurarSimbolo(token.getLexema());

		if (index >= 0) {
			return index;
		}

		throw new excecaoSemantico("Variável ou Função '" + token.getLexema()
				+ "' não está definida no escopo atual. \n Linha: " + token.getLinha());
	}

	public void searchVariable(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarVariavel(token.getLexema()))) {
			throw new excecaoSemantico(
					"A variável " + token.getLexema() + " não está definida.\nLinha: " + token.getLinha());
		}
	}

	public boolean searchVariableOrFunction(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarVariavel(token.getLexema()) || tabelaDeSimbolos.procurarFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"A variável ou função " + token.getLexema() + " não está definida.\nLinha: " + token.getLinha());
		} else {
			// Variável
			if (tabelaDeSimbolos.procurarVariavel(token.getLexema())) {
				return false;
			}
			// Função
			return true;
		}
	}
	//pode
	//erro de retorno de função 
	/* Métodos de recuperação */
	public String getLexemaOfSymbol(int index) {
		return tabelaDeSimbolos.getSimbolo(index).getLexema();
	}

	/* Outros métodos sobre a tabela de simbolos */
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

	/* ============ Métodos Semânticos ============ */

	public String expressionToPostfix(List<Token> expression) {

		List<String> stack = new ArrayList<String>();
		String retorno = "";

		for (int a = 0; a < expression.size(); a++) {

			String parcel = expression.get(a).getLexema();
			String simbolo = expression.get(a).getSimbolo();
			this.line = expression.get(a).getLinha();

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
		System.out.println("Saída: " + retorno);
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
				|| Constantes.MAIS_SINAL.equals(parcel) || Constantes.MENOS_SINAL.equals(parcel) || Constantes.MAIOR_SINAL.equals(parcel)
				|| Constantes.MENOR_SINAL.equals(parcel) || Constantes.MAIOR_IGUAL_SINAL.equals(parcel)
				|| Constantes.MENOR_IGUAL_SINAL.equals(parcel) || Constantes.IGUAL_SINAL.equals(parcel) || Constantes.DIFERENTE_SINAL.equals(parcel)
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
						"Operações aritméticas(+ | - | * | div) devem envolver duas variáveis inteiras.\n" + "Linha: "
								+ line);
			} else if (verificaOperadorRelacional(operator)) {
				if (firstType == "0" && secondType == "0") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações relacionais(!= | = | < | <= | > | >=) devem envolver duas variáveis inteiras.\n"
								+ "Linha: " + line);
			} else {
				if (firstType == "1" && secondType == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações lógicas(e | ou) devem envolver duas variáveis booleanas.\n" + "Linha: " + line);
			}
		} else {
			if (verificaOperadorUnarioMatematico(operator)) {
				if (firstType == "0") {
					return "0";
				}

				throw new excecaoSemantico(
						"Operações envolvendo operadores unários(+ | -) devem ser com variáveis inteiras.\n" + "Linha: "
								+ line);
			} else {
				if (firstType == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações envolvendo operador unário(NÃO) devem ser com variáveis booleanas.\n" + "Linha: "
								+ line);
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

	public void whoCallsMe(String tipo, String quemChamou) throws excecaoSemantico {
		System.out.println("tipo = " + tipo + " | quemChamou = " + quemChamou);
		if (Constantes.SE.equals(quemChamou) || Constantes.ENQUANTO.equals(quemChamou)) {
			if (!(Constantes.BOOLEANO.equals(tipo))) {
				throw new excecaoSemantico(
						"A condição presente no '" + quemChamou.toUpperCase() + "' deveria resultar num tipo booleano");
			}
		} else {
			String tipoQuemChamou = tabelaDeSimbolos.procurarTipoVariavelOuProcedimento(quemChamou);

			if (!(tipo.equals(tipoQuemChamou))) {
				throw new excecaoSemantico("Não é possível realizar a atribuição de uma expressão do tipo " + tipo
						+ " em uma variável/função do tipo " + tipoQuemChamou);
			}
		}
	}

	public String posicaoVariavel(String variavel) {
		int posicao = tabelaDeSimbolos.procurarPosicaoVariavel(variavel);

		return Integer.toString(posicao);
	}

	// Formata a expressão para usar na geração de código
	public String formatarExpressao(String expressao) {
		String[] aux = expressao.split(" ");
		String newExpression = "";
		int auxPosition;

		for (int i = 0; i < aux.length; i++) {
			if (!tabelaDeSimbolos.procurarFuncao(aux[i])) {
				auxPosition = tabelaDeSimbolos.procurarPosicaoVariavel(aux[i]);

				if (auxPosition != -1) {
					newExpression = newExpression.concat("p" + auxPosition + " ");
				} else {
					newExpression = newExpression.concat(aux[i] + " ");
				}
			} else {
				int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(aux[i]);
				newExpression = newExpression.concat("funcao" + labelResult + " ");
			}

		}

		return newExpression;
	}

	/* Métodos envolvendo o retorno de função */

	public void insertTokenOnFunctionList(Token token) {
		functionTokenList.add(token);
	}

	public void verifyFunctionList(String label) {
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
					lineWithoutReturn = functionTokenList.get(i).getLinha();
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
					lineWithoutReturn = functionTokenList.get(i).getLinha();
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

		for (int i = 0; i < functionTokenList.size(); i++) {
			if (nameOfFunction.equals(functionTokenList.get(i).getLexema())) {
				aux++;
				if (aux == functionTokenList.size()) {
					return true;
				}
			}
		}

		error = true;
		if (lineWithoutReturn != 0)
			line = lineWithoutReturn;

		throw new excecaoSemantico("Nem todos os caminhos possíveis da função possuem retorno." + "\nLinha: " + line);
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
		this.line = line;
	}

	public int getLine() {
		return line;
	}

	public boolean hasError() {
		return error;
	}

	// Debug Tabela de Simbolos

	public void debugTable() {
		tabelaDeSimbolos.debugTable();
	}

}