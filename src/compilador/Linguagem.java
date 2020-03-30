/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import static compilador.Token.TokenType.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author aluno
 */
public final class Linguagem
{

    private static Linguagem linguagem = null;
    private final HashMap<String, String[]> mapfirst, mapfollow;
    private final String[] arrbegin = new String[]
    {
        BEGIN.getRegex()
    };
    private final String[] arrend = new String[]
    {
        END.getRegex()
    };
    private final String[] arrfinal = new String[]
    {
        "$"
    };
    private final String[] arrtipos = new String[]
    {
        INT.getRegex(), FLUT.getRegex(), FRASE.getRegex()
    };
    private final String[] arridvar = new String[]
    {
        ID_VAR.getRegex()
    };
    private final String[] arrabrechave = new String[]
    {
        ABRE_CHAVE.getRegex()
    };
    private final String[] arrfechachave = new String[]
    {
        FECHA_CHAVE.getRegex()
    };
    private final String[] arrabreparentese = new String[]
    {
        ABRE_PARENTESE.getRegex()
    };
    private final String[] arrfechaparentese = new String[]
    {
        FECHA_PARENTESE.getRegex()
    };
    private final String[] arrcmd = new String[]
    {
        ENQUANTO.getRegex(), SE.getRegex(), ID_VAR.getRegex()
    };
    private final String[] arrnot = new String[]
    {
        NOT.getRegex()
    };
    private final String[] arroprel = new String[]
    {
        MENOR.getRegex(), MAIOR.getRegex(), IGUAL.getRegex(),
        MENOR_IGUAL.getRegex(), MAIOR_IGUAL.getRegex(), DIFERENTE.getRegex()
    };
    private final String[] arropari = new String[]
    {
        MAIS.getRegex(), MENOS.getRegex(), MULT.getRegex(), DIV.getRegex()
    };
    private final String[] arrvirgula = new String[]
    {
        VIRGULA.getRegex()
    };
    private final String[] arrse = new String[]
    {
        SE.getRegex()
    };
    private final String[] arrmenor = new String[]
    {
        MENOR.getRegex()
    };
    private final String[] arrmaior = new String[]
    {
        MAIOR.getRegex()
    };
    private final String[] arrigual = new String[]
    {
        IGUAL.getRegex()
    };
    private final String[] arrmenorigual = new String[]
    {
        MENOR_IGUAL.getRegex()
    };
    private final String[] arrmaiorigual = new String[]
    {
        MAIOR_IGUAL.getRegex()
    };
    private final String[] arrdiferente = new String[]
    {
        DIFERENTE.getRegex()
    };
    private final String[] arrand = new String[]
    {
        AND.getRegex()
    };
    private final String[] arror = new String[]
    {
        OR.getRegex()
    };
    private final String[] arrenquanto = new String[]
    {
        ENQUANTO.getRegex()
    };
    private final String[] arrpontovirgula = new String[]
    {
        PONTO_VIRGULA.getRegex()
    };
    private final String[] arratribuidor = new String[]
    {
        ATRIBUIR.getRegex()
    };
    private final String[] arrvalorint = new String[]
    {
        VALOR_INT.getRegex()
    };
    private final String[] arrvalorflut = new String[]
    {
        VALOR_FLUT.getRegex()
    };
    private final String[] arrvalorexp = new String[]
    {
        VALOR_FRASE.getRegex()
    };

    private Linguagem()
    {
        // inicializando map isFirst
        mapfirst = new HashMap<>();

        mapfirst.put("start", arrbegin);
        mapfirst.put("begin", arrbegin);
        mapfirst.put("end", arrend);
        mapfirst.put("cmd_var", arrtipos);
        mapfirst.put("tipo_var", arrtipos);
        mapfirst.put("definir_var", arridvar);
        mapfirst.put("virgula", arrvirgula);
        mapfirst.put("id_var", arridvar);
        mapfirst.put("cmd", arrcmd);
        mapfirst.put("cmd_if", arrse);
        mapfirst.put("valor_int", arrvalorint);
        mapfirst.put("valor_flut", arrvalorflut);
        mapfirst.put("valor_frase", arrvalorexp);
        mapfirst.put("valor", join(arrvalorint, arrvalorflut, arrvalorexp));
        mapfirst.put("valor_gen", join(new String[]
        {
            MENOS.getRegex()
        }, first("valor"),
                first("id_var"), arrabreparentese));
        mapfirst.put("abre_parentese", arrabreparentese);
        mapfirst.put("fecha_parentese", arrfechaparentese);
        mapfirst.put("abre_chave", arrabrechave);
        mapfirst.put("fecha_chave", arrfechachave);
        mapfirst.put("bool", join(arrnot, arridvar, first("valor"), arrabreparentese));
        mapfirst.put("operador_relacional", arroprel);
        mapfirst.put("menor", arrmenor);
        mapfirst.put("maior", arrmaior);
        mapfirst.put("igual", arrigual);
        mapfirst.put("menor_igual", arrmenorigual);
        mapfirst.put("maior_igual", arrmaiorigual);
        mapfirst.put("diferente", arrdiferente);
        mapfirst.put("and_or", join(arrand, arror));
        mapfirst.put("and", arrand);
        mapfirst.put("or", arror);
        mapfirst.put("not", arrnot);
        mapfirst.put("cmd_while", arrenquanto);
        mapfirst.put("while", arrenquanto);
        mapfirst.put("ponto_virgula", arrpontovirgula);
        mapfirst.put("operador_aritmetico", arropari);
        mapfirst.put("atribuidor", arratribuidor);
        mapfirst.put("atribuicao", arridvar);
        mapfirst.put("operacao_aritmetica", first("valor_gen"));
        mapfirst.put("reservado", join(first("begin"), first("end"), first("tipo_var"), arrse));
        //mapfirst.put("operacao_aritmetica", first("valor_gen"));

        //mapfirst.put("", );
        // inicializando map follow
        mapfollow = new HashMap<>(mapfirst.size());

        mapfollow.put("start", arrfinal);
        mapfollow.put("begin", first("cmd"));
        mapfollow.put("end", arrfinal);
        mapfollow.put("cmd_var", first("cmd"));
        mapfollow.put("tipo_var", arridvar);
        mapfollow.put("definir_var", join(arrvirgula, first("cmd")));
        mapfollow.put("virgula", first("definir_var"));
        mapfollow.put("id_var", join(first("atribuidor"), arrvirgula, arrpontovirgula, first("and_or"), first("operador_relacional")));
        mapfollow.put("cmd", join(first("end"), arrfechachave));
        mapfollow.put("cmd_if", follow("cmd"));
        mapfollow.put("bool", join(arrfechaparentese, first("and_or"),
                first("operador_relacional"), first("operador_aritmetico")));
        mapfollow.put("atribuicao", arrpontovirgula);
        mapfollow.put("valor_gen", join(first("operador_aritmetico"), follow("bool"),
                follow("atribuicao"), first("operador_aritmetico"),
                first("operacao_aritmetica"), first("fecha_parentese")));
        mapfollow.put("abre_parentese", join(arrnot, arrabreparentese, first("valor_gen")));
        mapfollow.put("fecha_parentese", join(arrpontovirgula, arrabrechave, first("operador_aritmetico"), first("operador_relacional")));
        mapfollow.put("abre_chave", first("cmd"));
        mapfollow.put("fecha_chave", join(first("cmd"), follow("cmd")));
        mapfollow.put("operador_relacional", join(first("valor_gen")));
        mapfollow.put("menor", first("valor_gen"));
        mapfollow.put("maior", follow("menor"));
        mapfollow.put("igual", follow("menor"));
        mapfollow.put("menor_igual", follow("menor"));
        mapfollow.put("maior_igual", follow("menor"));
        mapfollow.put("diferente", follow("menor"));
        mapfollow.put("and_or", join(first("valor_gen"), first("bool")));
        mapfollow.put("and", follow("and_or"));
        mapfollow.put("or", follow("and"));
        mapfollow.put("not", arrabreparentese);
        mapfollow.put("cmd_enquanto", follow("cmd"));
        mapfollow.put("enquanto", arrabreparentese);
        mapfollow.put("ponto_virgula", join(first("cmd"), first("cmd_var")));
        mapfollow.put("valor", follow("valor_gen"));
        mapfollow.put("atribuidor", first("valor_gen"));
        mapfollow.put("operador_aritmetico", first("valor_gen"));
        mapfollow.put("operacao_aritmetica", join(first("fecha_parentese"),
                follow("valor_gen")));
    }

    public static Linguagem getInstace()
    {
        if (linguagem == null)
        {
            linguagem = new Linguagem();
        }
        return linguagem;
    }

    public String[] first(String exp)
    {
        return mapfirst.get(exp);
    }

    public String[] follow(String exp)
    {
        return mapfollow.get(exp);
    }

    public String[] join(String[]... a)
    {
        HashSet<String> set = new HashSet<>(a.length);

        for (String[] arr : a)
        {
            set.addAll(Arrays.asList(arr));
        }

        String[] arr = new String[set.size()];
        return set.toArray(arr);
    }

    public boolean isFirst(String keyparent, String lexchild)
    {
//        if(tkparent.contains("var") && isReserved(tkchild))
//            return false;
        String[] tks = mapfirst.get(keyparent);
        if (tks != null)
        {
            for (String ts : tks)
            {
                if (lexchild.matches(ts))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isReserved(String word)
    {
        String[] res = mapfirst.get("reservado");
        for (String re : res)
        {
            if (word.matches(re))
            {
                return true;
            }
        }
        return false;
    }

    public boolean isFirst(String simb)
    {
        for (Iterator<Map.Entry<String, String[]>> it = mapfirst.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String, String[]> entry = it.next();
            for (String str : entry.getValue())
            {
                if (simb.matches(str))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public void exibeMapFirst()
    {
        for (Iterator<Map.Entry<String, String[]>> it = mapfirst.entrySet().iterator(); it.hasNext();)
        {
            Map.Entry<String, String[]> entry = it.next();
            System.out.print(entry.getKey() + " : ");
            for (String str : entry.getValue())
            {
                System.out.print(str + ", ");
            }
            System.out.println("");
        }
    }

    public boolean isFollow(String keyparent, String lexchild)
    {
        String[] tks = mapfollow.get(keyparent);
        for (String ts : tks)
        {
            if (lexchild.matches(ts))
            {
                return true;
            }
        }
        return false;
    }

}
