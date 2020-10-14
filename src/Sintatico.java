import java.io.IOException;

public class Sintatico {

	public void analisadorSintatico() {
		Lexico lexico = new Lexico();
		lexico.InicializadorArquivo();

		Token token = null;

		do {
			try {
				token = lexico.AnalisadorEntrada();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("Token lexema = " + token.getLexema() + " | simbolo = " + token.getSimbolo());
		} while (token.getSimbolo() != "Sfim");
	}

}
