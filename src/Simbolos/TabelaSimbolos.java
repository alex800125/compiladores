package Simbolos;

import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {

	private List<Simbolos> pilhaSimbolos;

	public TabelaSimbolos() {
		pilhaSimbolos = new ArrayList<Simbolos>();
	}

	public List<Simbolos> getStackDeSimbolos() {
		return pilhaSimbolos;
	}

	public Simbolos getSimbolo(int index) {
		return pilhaSimbolos.get(index);
	}

	public void inserir(Simbolos Simbolos) {
		pilhaSimbolos.add(Simbolos);
	}

	public void inserirTipoFuncao(String type) {
		Simbolos simbolo = pilhaSimbolos.get(pilhaSimbolos.size() - 1);

		if (simbolo instanceof Funcao && simbolo.getType() == null) {
			pilhaSimbolos.get(pilhaSimbolos.size() - 1).setTipo(type);
		}
	}

	public void inserirTipoVariavel(String type) {

		for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (pilhaSimbolos.get(i).getType() == null) {
					pilhaSimbolos.get(i).setTipo(type);
				}
			} else {
				break;
			}
		}
	}

	private boolean verificaNomePrograma(String lexema) {

		if (lexema.equals(pilhaSimbolos.get(0).getLexema())) {
			return true;
		}

		return false;
	}

	public boolean procurar(String lexema) {
		int i;

		for (i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			} else {
				break;
			}

		}

		for (int j = i; j >= 0; j--) {
			if ((pilhaSimbolos.get(i) instanceof Procedimento) || (pilhaSimbolos.get(i) instanceof Funcao)) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return verificaNomePrograma(lexema);
	}

	public int procurarSimbolo(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel || pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean procuraVariavel(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return verificaNomePrograma(lexema);
	}

	public boolean procurarProcedimento(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Procedimento) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return verificaNomePrograma(lexema);
	}

	public int procurarProcedimentoLabel(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Procedimento) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getLabel();
				}
			}
		}

		return -1;
	}

	public boolean procuraFuncao(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return verificaNomePrograma(lexema);
	}

	public int procurarFuncaoLabel(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getLabel();
				}
			}
		}

		return -1;
	}

	public String procurarTipoVariavelOuFuncao(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel || pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getType();
				}
			}
		}
		return null;
	}

	public int procurarPosicaoVariavel(String variable) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (variable.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getPosicao();
				}
			}
		}
		return -1;
	}

	public void limparLevel() {
		for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao || pilhaSimbolos.get(i) instanceof Procedimento) {
				if (pilhaSimbolos.get(i).naoEstaFechado()) {
					pilhaSimbolos.get(i).setFechado(true);
					break;
				} else {
					pilhaSimbolos.remove(i);
				}
			} else {
				pilhaSimbolos.remove(i);
			}
		}
//		System.out.println("Tabela Atualizada");
//		debugTable();
	}

	// usado para exibir a pilha (só é usado para testes)
	public void debugTable() {
		for (int i = 0; i < pilhaSimbolos.size(); i++) {
			System.out.println(pilhaSimbolos.get(i).getLexema() + " " + pilhaSimbolos.get(i).getType() + " "
					+ pilhaSimbolos.get(i).getPosicao());
		}
	}

}
