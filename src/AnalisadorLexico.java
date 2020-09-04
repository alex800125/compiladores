import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import javax.swing.JFileChooser;

public class AnalisadorLexico {
	void AnalisadorEntrada() 
	{
	int nlinha;
	
	String[] linha;
	String[] argumento;
	String[] linhaComentario;
	nlinha = 0;

	JFileChooser fileChooser = new JFileChooser();
	int returnValue = fileChooser.showOpenDialog(null);
	if (returnValue == JFileChooser.APPROVE_OPTION) {

		File selectedFile = fileChooser.getSelectedFile();

		try {
			FileInputStream fstream = new FileInputStream(selectedFile);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;

			while ((strLine = br.readLine()) != null) {
				nlinha++;
				System.err.println(nlinha);
				
				
			}
		} catch (Exception e1) { // Catch exception if any
			System.err.println("Error: " + e1.getMessage());
		}
	}

}
	int Analisador(String Lexema)
	{
		switch (Lexema)
		{
		case "programa": // Carregar constante (passado por parametro)
			System.out.println("sprograma");
			break;
		}
		return 0;
	}
}
