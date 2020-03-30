/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import compilador.Token.TokenType;
import java.util.ArrayList;

/**
 *
 * @author tulio
 */
public class AnaliseLexica
{

    public static String ERROR = "ERRO_LEXICO";
    public static Simbolo FIM_CADEIA = new Simbolo("$", TokenType.FIM.name(), 0);
    private ArrayList<Erro> erro;
    private boolean continuar;
    private int linha;
    private String text;
    private boolean finalisado;
    //private final ArrayList<TokenType> tokens;

    public AnaliseLexica(String entrada)
    {
        linha = 1;
        if (!entrada.endsWith("\n"))
        {
            entrada += '\n';
        }
        text = entrada;
        erro = new ArrayList<>();
        finalisado = false;
    }

    public ArrayList<Erro> getErros()
    {
        return erro;
    }

    private void removerEspacosInicio()
    {
        int i = 0;
        while (i < text.length() && text.charAt(i) == ' ')
        {
            i++;
        }
        text = text.substring(i);
    }

    private boolean mais(char c)
    {
        return c == '+';
    }

    private boolean menos(char c)
    {
        return c == '-';
    }

    private boolean mult(char c)
    {
        return c == '*';
    }

    private boolean div(char c)
    {
        return c == '/';
    }

    private boolean ponto(char c)
    {
        return c == '.';
    }

    private boolean letra(char c)
    {
        return Character.isLetter(c);
    }

    private boolean numero(char c)
    {
        return Character.isDigit(c);
    }

    private boolean delimitador(char c)
    {
        return (!letra(c) && !numero(c));
    }

    private int findDelimitador(int posini)
    {
        while (posini < text.length() && !delimitador(text.charAt(posini)))
        {
            ++posini;
        }
        return posini;
    }

    private int findDelimitadorID(int p)
    {
        while (p < text.length() && letra(text.charAt(p)))
        {
            p++;
        }
        return p;
    }

    private int iniciaComNumero()
    {
        int i = 0;
        char c;
        while (i < text.length() && numero(text.charAt(i)))
        {
            i++;
        }
        if (i < text.length())
        { // tem mais simbolo
            if (ponto(text.charAt(i)))
            { // double
                ++i;
                while (i < text.length() && numero(text.charAt(i)))
                {
                    ++i;
                }
                if (i < text.length()
                        && text.charAt(i) == 'e' && ++i < text.length() /*exp*/
                        && (mais(c = text.charAt(i)) || menos(c)))
                {
                    i = findDelimitador(i + 1);
                } else
                {
                    i = findDelimitador(i);
                }
            } else if (text.charAt(i) == 'e' && ++i < text.length() /*exp*/
                    && (mais(c = text.charAt(i)) || menos(c)))
            {
                i = findDelimitador(i + 1);
            } else
            {
                i = findDelimitador(i);
            }
        }
        return i;
    }

    public Simbolo nextSimbolo()
    {

        if (finalisado)
        {
            return FIM_CADEIA;
        }
        String lex;
        String tipo = "";
        char c;
        ArrayList<TokenType> tks;
        TokenType tk;
        int fim;
        do
        {
            removerEspacosInicio();
            if (text.isEmpty())
            {
                finalisado = true;
                FIM_CADEIA.setLinha(linha);
                return FIM_CADEIA;
            }
            fim = 0;
            tk = null;
            tks = Token.list();
            continuar = false;
            c = text.charAt(0);
            /* somente exp e comentario que não são achados com o findDelimitador*/
            if (numero(c))
            { // tokens que iniciam por numero
                fim = iniciaComNumero();
            } else if (text.startsWith("\n"))
            {
                ++linha;
                continuar = true;
            } else if (letra(c))
            {
                fim = findDelimitadorID(0);
            } else if (text.length() >= 2)
            { // se pode ser comentario
                if (text.startsWith("//"))
                {
                    fim = text.indexOf("\n");
                } else if (text.startsWith("/*"))
                {
                    fim = text.indexOf("*/");
                    fim = fim < 0 ? text.length() : fim + 2;
                    for (int i = 0; i < fim; ++i)
                    {
                        if (text.charAt(i) == '\n')
                        {
                            ++linha;
                        }
                    }
                } else if (Token.parent(text.substring(0, 2)) != null)
                {
                    fim = 2;
                } else
                {
                    fim = findDelimitador(0);
                }
            }
            if (fim == 0)
            { // delimitador é o primeiro caracter
                ++fim;
            }
            lex = text.subSequence(0, fim) + "";
            tk = Token.parent(lex);
            if (tk != null)
            {
                switch (tk)
                {
                    case COMENTARIO:
                        continuar = true;
                        break;
                    case VALOR_INT:
                    case VALOR_FLUT:
                    case VALOR_FRASE:
                        tipo = tk.name().substring(tk.name().indexOf("_") + 1).toLowerCase();
                        break;
                }
            } else
            { // erro lexico

                // if (tk == null) {
                erro.add(new Erro(linha, ERROR + " = " + lex));
                //return new Simbolo(lex, TokenType.ERRO_LEXICO.name(), linha);
                continuar = true;
                //  }
            }
            text = text.substring(fim); // remove lexema atual

        } while (continuar);
        return new Simbolo(tipo, lex, tk.name(), linha);
    }
}
