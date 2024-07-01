import java.io.*;

public class Parser {
    private AnalizadorLex lexer;
    private Token currentToken;
    private boolean errorOccurred;
    private PrintWriter writer;

    // Constructor: inicializa el lexer y obtiene el primer token
    public Parser(AnalizadorLex lexer, PrintWriter writer) {
        this.lexer = lexer;
        this.currentToken = lexer.getNextToken();
        this.errorOccurred = false;
        this.writer = writer;
    }

    // Método principal para iniciar el análisis sintáctico
    public void parse() {
        writer.println("<json>");
        json();
        writer.println("</json>");
        if (!errorOccurred) {
            System.out.println("EL JSON RECIBIDO ES SINTACTICAMENTE CORRECTO");
        }
        writer.close();
    }

    // Método que define la regla de inicio del JSON
    private void json() {
        elemento();
        match(TokenType.EOF_TOKEN);
    }

    // Método que analiza un elemento, que puede ser un objeto o un array
    private void elemento() {
        if (currentToken.type == TokenType.L_LLAVE) {
            objeto();
        } else if (currentToken.type == TokenType.L_CORCHETE) {
            array();
        } else {
            reportError("SE ESPERA UN ELEMENTO PARA INICIAR EL JSON");
        }
    }

    // Método que analiza un array
    private void array() {
        match(TokenType.L_CORCHETE);
        writer.println("<array>");
        if (currentToken.type != TokenType.R_CORCHETE) {
            listaElementos();
        }
        match(TokenType.R_CORCHETE);
        writer.println("</array>");
    }

    // Método que analiza una lista de elementos dentro de un array
    private void listaElementos() {
        elemento();
        while (currentToken.type == TokenType.COMA) {
            match(TokenType.COMA);
            elemento();
        }
    }

    // Método que analiza un objeto
    private void objeto() {
        match(TokenType.L_LLAVE);
        writer.println("<object>");
        if (currentToken.type != TokenType.R_LLAVE) {
            listaAtributos();
        }
        match(TokenType.R_LLAVE);
        writer.println("</object>");
    }

    // Método que analiza una lista de atributos dentro de un objeto
    private void listaAtributos() {
        atributo();
        while (currentToken.type == TokenType.COMA) {
            match(TokenType.COMA);
            atributo();
        }
    }

    // Método que analiza un atributo dentro de un objeto
    private void atributo() {
        String attributeName = currentToken.value;
        match(TokenType.LITERAL_CADENA);
        match(TokenType.DOS_PUNTOS);
        writer.print("<" + attributeName + ">");
        valorAtributo();
        writer.println("</" + attributeName + ">");
    }

    // Método que analiza el valor de un atributo, que puede ser un literal, un objeto o un array
    private void valorAtributo() {
        if (currentToken.type == TokenType.LITERAL_CADENA || currentToken.type == TokenType.LITERAL_NUM || currentToken.type == TokenType.PR_TRUE || currentToken.type == TokenType.PR_FALSE || currentToken.type == TokenType.PR_NULL) {
            writer.print(currentToken.value);
            match(currentToken.type);
        } else if (currentToken.type == TokenType.L_LLAVE || currentToken.type == TokenType.L_CORCHETE) {
            elemento();
        } else {
            reportError("VALOR INVALIDO");
        }
    }

    // Método que verifica si el token actual coincide con el tipo esperado
    private void match(TokenType expectedType) {
        if (currentToken.type == expectedType) {
            currentToken = lexer.getNextToken();
        } else {
            reportError("TOKEN INESPERADO: " + currentToken.value);
        }
    }

    // Método para reportar un error de sintaxis y llamar al modo pánico
    private void reportError(String message) {
        errorOccurred = true;
        System.err.println("ERROR DE SINTAXIS: " + message + " PARA EL TOKEN  " + currentToken.value);

        // Llamar al modo pánico para recuperarse del error
        synchronize();
    }

    // Modo pánico: saltar tokens hasta encontrar un punto de sincronización
    private void synchronize() {
        while (currentToken.type != TokenType.EOF_TOKEN) {
            if (currentToken.type == TokenType.COMA || currentToken.type == TokenType.R_LLAVE || currentToken.type == TokenType.R_CORCHETE) {
                currentToken = lexer.getNextToken();
                return;
            }
            currentToken = lexer.getNextToken();
        }
    }
}
