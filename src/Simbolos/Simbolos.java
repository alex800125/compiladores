package Simbolos;

public class Simbolos {
	private String lexema;
	private boolean closed;
	private String type;
	private int label;
	private int position;
	
	public Simbolos(String lexema, int label, int position) {
		this.setLexema(lexema);
		this.setClosed(false);
		this.setType(null);
		this.setLabel(label);
		this.setPosition(position);
	}
	
	public boolean isClosed() {
		return closed;
	}
	
	public boolean isNotClosed() {
		return !closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}
