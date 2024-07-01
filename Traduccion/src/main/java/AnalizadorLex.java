import java.io.*;
import java.util.regex.*;
import java.util.*;

class Token {
    TokenType type;
    String value;
    
    Token(TokenType tipo, String valor) {
        this.type = tipo;
        this.value = valor;
    }
}

enum TokenType {
    L_LLAVE, R_LLAVE, L_CORCHETE, R_CORCHETE, COMA, DOS_PUNTOS,
    LITERAL_CADENA, LITERAL_NUM, PR_TRUE, PR_FALSE, PR_NULL, EOF_TOKEN, ERROR
}
public class AnalizadorLex {
    private List<Token> tokens;
    private int currentTokenIndex;
    
    public AnalizadorLex(String source) {
        tokens = new ArrayList<>();
        lexer(source);
        tokens.add(new Token(TokenType.EOF_TOKEN, ""));
        currentTokenIndex = 0;
    }

    AnalizadorLex(BufferedReader reader) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public Token getNextToken() {
        if (currentTokenIndex < tokens.size()) {
            return tokens.get(currentTokenIndex++);
        } else {
            return new Token(TokenType.EOF_TOKEN, "");
        }
    }

    private void lexer(String input) {
        String pattern = "\\{|\\}|\\[|\\]|:|,|true|TRUE|false|FALSE|null|NULL|\"[^\"]*\"|-?\\d+";

        // Crear un patrón de expresión regular
        Pattern regex = Pattern.compile(pattern);
        // Usar el patrón para buscar coincidencias en la entrada
        Matcher matcher = regex.matcher(input);

        while (matcher.find()) {
            String token = matcher.group();
            switch (token) {
                case "{":
                    tokens.add(new Token(TokenType.L_LLAVE, token));
                    break;
                case "}":
                    tokens.add(new Token(TokenType.R_LLAVE, token));
                    break;
                case "[":
                    tokens.add(new Token(TokenType.L_CORCHETE, token));
                    break;
                case "]":
                    tokens.add(new Token(TokenType.R_CORCHETE, token));
                    break;
                case ":":
                    tokens.add(new Token(TokenType.DOS_PUNTOS, token));
                    break;
                case ",":
                    tokens.add(new Token(TokenType.COMA, token));
                    break;
                case "true":
                case "TRUE":
                    tokens.add(new Token(TokenType.PR_TRUE, token));
                    break;
                case "false":
                case "FALSE":
                    tokens.add(new Token(TokenType.PR_FALSE, token));
                    break;
                case "null":
                case "NULL":
                    tokens.add(new Token(TokenType.PR_NULL, token));
                    break;
                default:
                    if (token.matches("\"[^\"]*\"")) {
                        tokens.add(new Token(TokenType.LITERAL_CADENA, token));
                    } else if (token.matches("-?\\d+")) {
                        tokens.add(new Token(TokenType.LITERAL_NUM, token));
                    } else {
                        tokens.add(new Token(TokenType.ERROR, token));
                    }
            }
        }
    }

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("fuente.txt"));
            PrintWriter writer = new PrintWriter(new FileWriter("output.txt"));

            String line;
            while ((line = reader.readLine()) != null) {
                AnalizadorLex lexer = new AnalizadorLex(line);
                Token token;
                while ((token = lexer.getNextToken()).type != TokenType.EOF_TOKEN) {
                    writer.print(token.type + " ");
                }
                writer.println();  
            }

            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

