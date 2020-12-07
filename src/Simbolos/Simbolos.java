package Simbolos;

public class Simbolos {
	private String lexema;
	private boolean fechado;
	private String tipo;
	private int label;
	private int posicao;
	
	public Simbolos(String lexema, int label, int position) {
		this.setLexema(lexema);
		this.setFechado(false);
		this.setTipo(null);
		this.setLabel(label);
		this.setPosicao(position);
	}
	
	public boolean estaFechado() {
		return fechado;
	}
	
	public boolean naoEstaFechado() {
		return !fechado;
	}

	public void setFechado(boolean fechado) {
		this.fechado = fechado;
	}
	
	public String getType() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLexema() {
		return lexema;
	}

	public void setLexema(String lexema) {
		this.lexema = lexema;
	}

	public int getLabel() {
		return label;
	}

	public void setLabel(int label) {
		this.label = label;
	}

	public int getPosicao() {
		return posicao;
	}

	public void setPosicao(int posicao) {
		this.posicao = posicao;
	}
}
