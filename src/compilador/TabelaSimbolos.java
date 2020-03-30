/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.util.ArrayList;

/**
 *
 * @author tulio
 */
public class TabelaSimbolos
{

    private ArrayList<Simbolo> tabela;
    private ArrayList<Integer> index;

    public TabelaSimbolos()
    {
        tabela = new ArrayList<>();
        index = new ArrayList<>();
    }

    public boolean add(Simbolo simb)
    {
        tabela.add(simb);
        int pos = 0;
        if (tabela.size() > 1)
        {
            pos = find(simb.getId());
            if (pos >= index.size())
            {
                pos -= index.size();
            }
        }
        index.add(pos, tabela.size() - 1);
        return true;
    }

    public Simbolo search(Simbolo s)
    {
        if (tabela.isEmpty())
        {
            return null;
        }
        int pos = find(s.getId());
        if (pos >= tabela.size())
        {
            return null;
        }
        return tabela.get(index.get(pos));
    }

    private int find(String id)
    {
        int ini = 0, fim = index.size() - 1;
        int comp = 0, med = (ini + fim) >> 1;
        while (ini <= fim)
        {
            med = (ini + fim) >> 1;
            comp = id.compareTo(tabela.get(index.get(med)).getId());
            if (comp == 0)
                return med;
            if(comp > 0)
                ini = med + 1;
            else
                fim = med - 1;
        }
        if (comp > 0)
        {
            return med + index.size() + 1;
        }
        return med + index.size();
    }

    public ArrayList<Simbolo> getTable()
    {
        return tabela;
    }

    public ArrayList<Integer> getIndexList()
    {
        return index;
    }

}
