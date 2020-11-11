package Simbolos;

import java.util.ArrayList;
import java.util.List;

public class TabelaSimbolos {
	
	private List<Simbolos> pilhaSimbolos;

	public TabelaSimbolos() {
		pilhaSimbolos = new ArrayList<Simbolos>();
	}

	public List<Simbolos> getStackOfSymbols() {
		return pilhaSimbolos;
	}

	public Simbolos getSymbol(int index) {
		return pilhaSimbolos.get(index);
	}

	public void insert(Simbolos Simbolos) {
		pilhaSimbolos.add(Simbolos);
	}

	public void insertTypeOnFunction(String type) {
		Simbolos simbolo = pilhaSimbolos.get(pilhaSimbolos.size() - 1);

		if (simbolo instanceof Funcao && simbolo.getType() == null) {
			pilhaSimbolos.get(pilhaSimbolos.size() - 1).setType(type);
		}
	}

	public void insertTypeOnVariable(String type) {

		for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (pilhaSimbolos.get(i).getType() == null) {
					pilhaSimbolos.get(i).setType(type);
				}
			} else {
				break;
			}
		}
	}

	private boolean lookProgramName(String lexema) {
		if (lexema.equals(pilhaSimbolos.get(0).getLexema())) {
			return true;
		}
		return false;
	}

	public boolean search(String lexema) {
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

		return lookProgramName(lexema);
	}

	public int searchSymbol(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel || pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean searchVariable(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return lookProgramName(lexema);
	}

	public boolean searchProcedure(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Procedimento) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return lookProgramName(lexema);
	}

	public int searchProcedureLabel(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Procedimento) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getLabel();
				}
			}
		}

		return -1;
	}

	public boolean searchFunction(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return true;
				}
			}
		}

		return lookProgramName(lexema);
	}

	public int searchFunctionLabel(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getLabel();
				}
			}
		}

		return -1;
	}

	public String searchTypeOfVariableOrFunction(String lexema) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel || pilhaSimbolos.get(i) instanceof Funcao) {
				if (lexema.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getType();
				}
			}
		}
		return null;
	}

	public int searchPositionOfVariable(String variable) {
		for (int i = (pilhaSimbolos.size() - 1); i >= 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Variavel) {
				if (variable.equals(pilhaSimbolos.get(i).getLexema())) {
					return pilhaSimbolos.get(i).getPosition();
				}
			}
		}
		return -1;
	}

	public void cleanLevel() {
		for (int i = (pilhaSimbolos.size() - 1); i > 0; i--) {
			if (pilhaSimbolos.get(i) instanceof Funcao || pilhaSimbolos.get(i) instanceof Procedimento) {
				if (pilhaSimbolos.get(i).isNotClosed()) {
					pilhaSimbolos.get(i).setClosed(true);
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

	public void debugTable() {
		for (int i = 0; i < pilhaSimbolos.size(); i++) {
			System.out.println(pilhaSimbolos.get(i).getLexema() + " " + pilhaSimbolos.get(i).getType() + " "
					+ pilhaSimbolos.get(i).getPosition());
		}
	}

}
