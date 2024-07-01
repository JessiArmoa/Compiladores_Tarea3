import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("fuente.txt"));
            PrintWriter writer = new PrintWriter(new FileWriter("output.xml"));

            AnalizadorLex lexer = new AnalizadorLex(reader);
            Parser parser = new Parser(lexer, writer);
            parser.parse();

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
