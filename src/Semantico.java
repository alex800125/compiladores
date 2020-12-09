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

	private ArrayList<Token> ListaFuncaoToken = new ArrayList<Token>();
	private int LinhaSemRetorno;
	private int linha;
	private boolean erro;

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
	public void inserirProcedimentoOuFuncao(Token token, String type, int label) {
		if (Constantes.PROCEDIMENTO.equals(type)) {
			tabelaDeSimbolos.inserir(new Procedimento(token.getLexema(), label));
		} else if (Constantes.FUNCAO.equals(type)) {
			tabelaDeSimbolos.inserir(new Funcao(token.getLexema(), label));
		}
	}

	// Variável
	public void inserirVariavel(Token token, int position) {
		tabelaDeSimbolos.inserir(new Variavel(token.getLexema(), position));
	}

	// Tipo Função
	public void inserirTipoEmFuncao(String type) {
		tabelaDeSimbolos.inserirTipoFuncao(type);
	}

	// Tipo Variável
	public void inserirTipoEmVariavel(Token token) {
		tabelaDeSimbolos.inserirTipoVariavel(token.getLexema());
	}

	/* Métodos de busca */

	public void procurarVersao(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procuraFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLinha());
		}
	}

	public int procurarFuncaoLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Função '" + token.getLexema() + "' não está declarada.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void procuraFuncoesComMesmoNome(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procuraFuncao(token.getLexema())) {
			throw new excecaoSemantico("Já existe uma função com o mesmo nome da função da linha: " + token.getLinha());
		}
	}

	public void procuraTabelaSimbolos(Token token) throws excecaoSemantico {

		if (tabelaDeSimbolos.procurar(token.getLexema())) {
			throw new excecaoSemantico(
					"Já existe uma variável com o mesmo nome da variável da linha: " + token.getLinha());
		}
	}

	public void procuraProcedimento(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procurarProcedimento(token.getLexema()))) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLinha());
		}
	}

	public int procuraProcedimentoLabel(Token token) throws excecaoSemantico {
		int labelResult = tabelaDeSimbolos.procurarProcedimentoLabel(token.getLexema());

		if (labelResult == -1) {
			throw new excecaoSemantico(
					"Procedimento '" + token.getLexema() + "' não está declarado.\nLinha: " + token.getLinha());
		} else {
			return labelResult;
		}
	}

	public void procuraProcedimentoComMesmoNome(Token token) throws excecaoSemantico {
		if (tabelaDeSimbolos.procurarProcedimento(token.getLexema())) {
			throw new excecaoSemantico(
					"Já existe um procedimento com o mesmo nome do procedimento da linha: " + token.getLinha());
		}
	}

	public int procurarSimbolo(Token token) throws excecaoSemantico {
		int index = tabelaDeSimbolos.procurarSimbolo(token.getLexema());

		if (index >= 0) {
			return index;
		}

		throw new excecaoSemantico("Variável ou Função '" + token.getLexema()
				+ "' não está definida no escopo atual. \n Linha: " + token.getLinha());
	}

	public void procuraVariavel(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procuraVariavel(token.getLexema()))) {
			throw new excecaoSemantico(
					"A variável " + token.getLexema() + " não está definida.\nLinha: " + token.getLinha());
		}
	}

	public boolean procuraVariavelOuFuncao(Token token) throws excecaoSemantico {
		if (!(tabelaDeSimbolos.procuraVariavel(token.getLexema())
				|| tabelaDeSimbolos.procuraFuncao(token.getLexema()))) {
			throw new excecaoSemantico(
					"A variável ou função " + token.getLexema() + " não está definida.\nLinha: " + token.getLinha());
		} else {
			// Variável
			if (tabelaDeSimbolos.procuraVariavel(token.getLexema())) {
				return false;
			}
			// Função
			return true;
		}
	}

	/* Métodos de recuperação */
	public String getLexemaDoSimbolo(int index) {
		return tabelaDeSimbolos.getSimbolo(index).getLexema();
	}

	/* Outros métodos sobre a tabela de simbolos */
	public boolean funcaoEValida(int index) {
		if (tabelaDeSimbolos.getSimbolo(index) instanceof Funcao
				&& (tabelaDeSimbolos.getSimbolo(index).getType() == Constantes.INTEIRO
						|| tabelaDeSimbolos.getSimbolo(index).getType() == Constantes.BOOLEANO)) {
			return true;
		}

		return false;
	}

	public void limparTabela() {
		tabelaDeSimbolos.limparLevel();
	}

	/* ============ Métodos Semânticos ============ */

	public String expressaoToPostfix(List<Token> expressao) {

		List<String> stack = new ArrayList<String>();
		String retorno = "";

		for (int a = 0; a < expressao.size(); a++) {

			String parcel = expressao.get(a).getLexema();
			String simbolo = expressao.get(a).getSimbolo();
			this.linha = expressao.get(a).getLinha();

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
					int novaPrioridadeOperador;
					int stackOperadorMaiorPrioridade;
					int stackTop = stack.size() - 1;
					do {

						novaPrioridadeOperador = definicaoPrioridadeOperador(parcel);
						stackOperadorMaiorPrioridade = definicaoPrioridadeOperador(stack.get(stackTop));

						if (stackOperadorMaiorPrioridade >= novaPrioridadeOperador) {
							retorno = retorno.concat(stack.get(stackTop) + " ");
							stack.remove(stackTop);
							stackTop--;
						}

					} while (stackOperadorMaiorPrioridade >= novaPrioridadeOperador && !(stack.isEmpty()));

					if (stackOperadorMaiorPrioridade < novaPrioridadeOperador || stack.isEmpty()) {

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

	public String retornaTipoDaExpressao(String expressao) throws excecaoSemantico {
		String type = separaExpressaoPostFix(expressao);

		if (type == "0") {
			return Constantes.INTEIRO;
		} else {
			return Constantes.BOOLEANO;
		}
	}

	private String separaExpressaoPostFix(String expressao) throws excecaoSemantico {
		System.out.println("expressao: " + expressao);
		String[] aux = expressao.split(" ");
		List<String> ListaExpressoes = new ArrayList<String>(Arrays.asList(aux));

		for (int j = 0; j < ListaExpressoes.size(); j++) {
			String parcel = ListaExpressoes.get(j);
//			System.out.println("parcel: " + parcel);

			if (!(verificaOperador(parcel)) && !(eOperadorUnario(parcel))) {
				if (Constantes.INTEIRO.equals(tabelaDeSimbolos.procurarTipoVariavelOuFuncao(parcel))) {
					System.out.println("parcel: " + parcel);
					ListaExpressoes.set(j, "0");
				} else if (Constantes.BOOLEANO.equals(tabelaDeSimbolos.procurarTipoVariavelOuFuncao(parcel))) {
					ListaExpressoes.set(j, "1");
				} else if (Constantes.VERDADEIRO.equals(parcel) || Constantes.FALSO.equals(parcel)) {
					ListaExpressoes.set(j, "1");
				} else {
					ListaExpressoes.set(j, "0");
				}
			}

		}

		for (int i = 0; i < ListaExpressoes.size(); i++) {
			System.out.println("expressionList.get(i) = " + ListaExpressoes.get(i));
			if (verificaOperador(ListaExpressoes.get(i))) {

				String operation = retornoTipoOperacao(ListaExpressoes.get(i - 2), ListaExpressoes.get(i - 1),
						ListaExpressoes.get(i));

				ListaExpressoes.remove(i);
				ListaExpressoes.remove(i - 1);
				ListaExpressoes.remove(i - 2);
				ListaExpressoes.add(i - 2, operation);

				i = 0;
			} else if (eOperadorUnario(ListaExpressoes.get(i))) {
				String operation = retornoTipoOperacao(ListaExpressoes.get(i - 1), null, ListaExpressoes.get(i));

				ListaExpressoes.remove(i);
				ListaExpressoes.remove(i - 1);
				ListaExpressoes.add(i - 1, operation);

				i = 0;
			}
		}
		return ListaExpressoes.get(0);
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

	private String retornoTipoOperacao(String primeiroTipo, String segundoTipo, String operador) throws excecaoSemantico {
		// 0 representa um tipo inteiro
		// 1 representa um tipo booleano

		if (verificaOperador(operador)) {
			if (verificaOperadorMatematico(operador)) {
				if (primeiroTipo == "0" && segundoTipo == "0") {
					return "0";
				}

				throw new excecaoSemantico(
						"Operações aritméticas devem envolver variáveis inteiras.\n" + "Linha: " + linha);
			} else if (verificaOperadorRelacional(operador)) {
				if (primeiroTipo == "0" && segundoTipo == "0") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações relacionais (!= | = | < | <= | > | >=) devem envolver variáveis inteiras.\n"
								+ "Linha: " + linha);
			} else {
				if (primeiroTipo == "1" && segundoTipo == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações lógicas (e | ou) devem envolver variáveis booleanas.\n" + "Linha: " + linha);
			}
		} else {
			if (verificaOperadorUnario(operador)) {
				if (primeiroTipo == "0") {
					return "0";
				}

				throw new excecaoSemantico(
						"Operações envolvendo operadores unários (+ | -) devem ser com variáveis inteiras.\n"
								+ "Linha: " + linha);
			} else {
				if (primeiroTipo == "1") {
					return "1";
				}

				throw new excecaoSemantico(
						"Operações envolvendo operador unário (NÃO) devem ser com variáveis booleanas.\n" + "Linha: "
								+ linha);
			}
		}
	}

	private boolean eOperadorUnario(String parcel) {

		if (Constantes.MAIS_UNARIO.equals(parcel) || Constantes.MENOS_UNARIO.equals(parcel)
				|| Constantes.NAO.equals(parcel)) {
			return true;
		}

		return false;
	}

	private boolean verificaOperadorUnario(String parcel) {

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
						"A condição presente no '" + quemChamou.toUpperCase() + "' deveria resultar num tipo booleano");
			}
		} else {
			String tipoQuemChamou = tabelaDeSimbolos.procurarTipoVariavelOuFuncao(quemChamou);

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
		String novaExpressao = "";
		int auxPosicao;

		for (int i = 0; i < aux.length; i++) {
			if (!tabelaDeSimbolos.procuraFuncao(aux[i])) {
				auxPosicao = tabelaDeSimbolos.procurarPosicaoVariavel(aux[i]);

				if (auxPosicao != -1) {
					novaExpressao = novaExpressao.concat("p" + auxPosicao + " ");
				} else {
					novaExpressao = novaExpressao.concat(aux[i] + " ");
				}
			} else {
				int labelResult = tabelaDeSimbolos.procurarFuncaoLabel(aux[i]);
				novaExpressao = novaExpressao.concat("funcao" + labelResult + " p0 ");
			}

		}

		return novaExpressao;
	}

	/* Métodos envolvendo o retorno de função */

	public void inserirTokenListaFuncao(Token token) {
		ListaFuncaoToken.add(token);
	}

	public void verificarListaFuncao(String label) {
		Token auxToken = null;

		boolean conditionalThenReturn = false;
		boolean conditionalElseReturn = false;
		int thenPosition = -1;
		int elsePosition = thenPosition;

		for (int i = 0; i < ListaFuncaoToken.size(); i++) {
			if (Constantes.se.equals(ListaFuncaoToken.get(i).getSimbolo())
					&& ListaFuncaoToken.get(i).getLexema().contains(label)) {
				ListaFuncaoToken.remove(i);
				i--;
			} else if (Constantes.entao.equals(ListaFuncaoToken.get(i).getSimbolo())
					&& ListaFuncaoToken.get(i).getLexema().contains(label)) {
				if (ListaFuncaoToken.size() > (i + 1)) {
					if (Constantes.identificador.equals(ListaFuncaoToken.get(i + 1).getSimbolo())) {
						conditionalThenReturn = true;
						auxToken = ListaFuncaoToken.get(i + 1);
					}
				} else {
					LinhaSemRetorno = ListaFuncaoToken.get(i).getLinha();
				}
				thenPosition = i;
			} else if (Constantes.senao.equals(ListaFuncaoToken.get(i).getSimbolo())
					&& ListaFuncaoToken.get(i).getLexema().contains(label)) {
				if (ListaFuncaoToken.size() > (i + 1)) {
					if (Constantes.identificador.equals(ListaFuncaoToken.get(i + 1).getSimbolo())) {
						conditionalElseReturn = true;
						elsePosition = i + 1;
						auxToken = ListaFuncaoToken.get(i + 1);
					}
				} else {
					LinhaSemRetorno = ListaFuncaoToken.get(i).getLinha();
					elsePosition = i;
				}

			}
		}

		if (elsePosition == (-1))
			elsePosition = ListaFuncaoToken.size() - 1;

		removeIf(elsePosition, thenPosition, (conditionalThenReturn && conditionalElseReturn), auxToken);
	}

	public boolean verificaSeAFuncaoTemRetorno(String nomeDaFuncao) throws excecaoSemantico {
		int aux = 0;
		System.out.println("ListaFuncaoToken.size() = " + ListaFuncaoToken.size());
		for (int i = 0; i < ListaFuncaoToken.size(); i++) {
			if (nomeDaFuncao.equals(ListaFuncaoToken.get(i).getLexema())) {
				aux++;
				if (aux == ListaFuncaoToken.size()) {
					return true;
				}
			}
		}

		erro = true;
		if (LinhaSemRetorno != 0)
			linha = LinhaSemRetorno;

		throw new excecaoSemantico("Nem todos os caminhos possíveis da função possuem retorno." + "\nLinha: " + linha);
	}

	private void removeIf(int start, int end, boolean functionReturn, Token tokenFunction) {
		for (int i = start; i >= end; i--) {
			ListaFuncaoToken.remove(i);
		}

		if (functionReturn && tokenFunction != null) {
			ListaFuncaoToken.add(tokenFunction);
		}
	}

	public void limparListaFuncao() {
		ListaFuncaoToken.clear();
	}

	public void debugTableFunction() {
		for (int i = 0; i < ListaFuncaoToken.size(); i++) {
			System.out.println(ListaFuncaoToken.get(i).getLexema());
		}
	}
	//

	public void setLinha(int linha) {
		this.linha = linha;
	}

	public int getLinha() {
		return linha;
	}

	public boolean verificaSeHaErro() {
		return erro;
	}

	// Debug Tabela de Simbolos

	public void debugTable() {
		tabelaDeSimbolos.debugTable();
	}

}