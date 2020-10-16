package Excecoes;

public class excecaoSintatico extends Exception {

	private static final long serialVersionUID = 1L;

	public excecaoSintatico(String mensagem) {
		super(mensagem);
	}

	public excecaoSintatico(String LexemaEsperado, String LexemaEncontrado, int linha) {
		super("Erro na linha: " + linha + " | Simbolo esperado: '" + LexemaEsperado + "' | Simbolo encontrado: '"
				+ LexemaEncontrado + "'");
	}

	public excecaoSintatico(String LexemaEsperado_1, String LexemaEsperado_2, String LexemaEncontrado, int linha) {
		super("Erro na linha: " + linha + " | Simbolo esperado: '" + LexemaEsperado_1 + "' ou '" + LexemaEsperado_2
				+ "' | Simbolo encontrado: '" + LexemaEncontrado + "'");
	}

}