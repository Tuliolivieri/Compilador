/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.ArrayList;

/**
 *
 * @author Aluno
 */

public abstract class Token {
    private static ArrayList<TokenType> list = null;
    
    public Token(){
        
    }

    public static ArrayList<TokenType> list() {
        if (list == null) {
            TokenType[] arr = TokenType.values();
            list = new ArrayList<>(arr.length);
            for (TokenType t : arr) {
                list.add(t);
            }
        }
        return list;
    }

    public static TokenType parent(String lexema) {
        for (TokenType tk : list()) {
            if (tk.matches(lexema)) {
                return tk;
            }
        }
        return null;
    }
    
    public static ArrayList<TokenType> contains(String lexema) {
        ArrayList<TokenType> list = new ArrayList<>();
        for (TokenType tk : list()) {
            if (tk.matches(lexema)
                    || (!tk.getRegex().contains("[") && tk.getRegex().startsWith(lexema))) {
                list.add(tk);
            }
        }
        return list.isEmpty() ? null : list;
    }

    public enum TokenType {
        BEGIN("begin"), 
        END("end"), 
        INT("int"), 
        FLUT("flut"), 
        FRASE("frase"), 
        ENQUANTO("enquanto"),
        SE("se"),
        PARA("para"),
        VIRGULA(","), 
        PONTO_VIRGULA(";"), 
        ABRE_PARENTESE("\\("), 
        FECHA_PARENTESE("\\)"), 
        ABRE_CHAVE("\\{"), 
        FECHA_CHAVE("\\}"), 
        MAIS("\\+"), 
        MENOS("-"), 
        MULT("\\*"), 
        DIV("/"), 
        ATRIBUIR("="), 
        MENOR("<"), 
        MAIOR(">"), 
        IGUAL("=="), 
        MENOR_IGUAL("<="), 
        MAIOR_IGUAL(">="), 
        DIFERENTE("!="), 
        AND("&&"), 
        OR("\\|\\|"), 
        NOT("!"), 
        VALOR_INT("[0-9]+"), 
        VALOR_FLUT("[0-9]+(\\.[0-9])*"), 
        VALOR_FRASE("[0-9]+(\\.[0-9]+)*e(\\+|-)[0-9]+"), 
        ID_VAR("[a-zA-Z](\\d|[a-zA-Z])*"), 
        COMENTARIO("///(.)*(\\n)?$"), 
        NL("\\n"), 
        FIM("\\$");

        private final String regex;
        TokenType(String regex) {
            this.regex = regex;
        }

        public String getRegex() {
            return regex;
        }

        public boolean matches(String lexema) {
            return lexema.matches(regex);
        }

    }

}