package it.univaq.webdelivery.data.model;

import java.util.ArrayList;
import java.util.List;

public class GruppoCaratteristica {
    private int id;
    private int id_prodotto;
    private String nome;
    private boolean mutua_esclusione; // matches mutua_esclusione in JSON
    private List<Caratteristica> opzioni = new ArrayList<>(); // matches opzioni in JSON

    public GruppoCaratteristica() {}

    public GruppoCaratteristica(int id, int id_prodotto, String nome, boolean mutua_esclusione) {
        this.id = id;
        this.id_prodotto = id_prodotto;
        this.nome = nome;
        this.mutua_esclusione = mutua_esclusione;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProdotto() {
        return id_prodotto;
    }

    public void setIdProdotto(int id_prodotto) {
        this.id_prodotto = id_prodotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isMutuaEsclusione() {
        return mutua_esclusione;
    }

    public void setMutuaEsclusione(boolean mutua_esclusione) {
        this.mutua_esclusione = mutua_esclusione;
    }

    public List<Caratteristica> getOpzioni() {
        return opzioni;
    }

    public void setOpzioni(List<Caratteristica> opzioni) {
        this.opzioni = opzioni;
    }
}
