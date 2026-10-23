package br.com.unipds.estatisticas;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

class ContadorPalavras implements Iterable<ContadorPalavras.ContemPalavra> {

    record ContemPalavra(String palavra, int ocorrencias) {

    }

    public Iterator<ContemPalavra> iterator() {
        return new Iterator<>() {
            Iterator<Map.Entry<String,Integer>> it = mapa.entrySet().iterator();
            @Override
            public boolean hasNext() {
                return it.hasNext();
            }
            @Override
            public ContemPalavra next() {
                Map.Entry<String,Integer> entry = it.next();
                return new ContemPalavra(entry.getKey(),entry.getValue());
            }
        };
    }


    private Map<String,Integer> mapa = new TreeMap<>();

    public void adicionarPalavra(String palavra){
         mapa.merge(palavra,1, Integer::sum);
    }
    public Iterable<Map.Entry<String,Integer>> entrySet(){
        return mapa.entrySet();
    }
}
