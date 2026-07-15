package it.univaq.webdelivery.data.model;

import java.util.ArrayList;
import java.util.List;

public class RigaOrdine {
    private int id;
    private int id_ordine;
    private int prodotto_id; // Corrisponde a prodotto_id nel JSON dello staff
    private String nome;      // Corrisponde a nome nel JSON del cliente (es. "Pizza Margherita")
    private int qty;          // Corrisponde a qty nel JSON (quantità in DB)
    private double prezzo_base_al_momento;
    private List<String> opzioni = new ArrayList<>(); // Nomi delle caratteristiche selezionate
    private List<CaratteristicaScelta> caratteristicheScelte = new ArrayList<>();

    public static class CaratteristicaScelta {
        private int idCaratteristica;
        private double diffPrezzoAlMomento;

        public CaratteristicaScelta() {}

        public CaratteristicaScelta(int idCaratteristica, double diffPrezzoAlMomento) {
            this.idCaratteristica = idCaratteristica;
            this.diffPrezzoAlMomento = diffPrezzoAlMomento;
        }

        public int getIdCaratteristica() {
            return idCaratteristica;
        }

        public void setIdCaratteristica(int idCaratteristica) {
            this.idCaratteristica = idCaratteristica;
        }

        public double getDiffPrezzoAlMomento() {
            return diffPrezzoAlMomento;
        }

        public void setDiffPrezzoAlMomento(double diffPrezzoAlMomento) {
            this.diffPrezzoAlMomento = diffPrezzoAlMomento;
        }
    }

    public RigaOrdine() {}

    public RigaOrdine(int id, int id_ordine, int prodotto_id, String nome, int qty, double prezzo_base_al_momento) {
        this.id = id;
        this.id_ordine = id_ordine;
        this.prodotto_id = prodotto_id;
        this.nome = nome;
        this.qty = qty;
        this.prezzo_base_al_momento = prezzo_base_al_momento;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrdine() {
        return id_ordine;
    }

    public void setIdOrdine(int id_ordine) {
        this.id_ordine = id_ordine;
    }

    public int getProdottoId() {
        return prodotto_id;
    }

    public void setProdottoId(int prodotto_id) {
        this.prodotto_id = prodotto_id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrezzoBaseAlMomento() {
        return prezzo_base_al_momento;
    }

    public void setPrezzoBaseAlMomento(double prezzo_base_al_momento) {
        this.prezzo_base_al_momento = prezzo_base_al_momento;
    }

    public List<String> getOpzioni() {
        return opzioni;
    }

    public void setOpzioni(List<String> opzioni) {
        this.opzioni = opzioni;
    }

    public List<CaratteristicaScelta> getCaratteristicheScelte() {
        return caratteristicheScelte;
    }

    public void setCaratteristicheScelte(List<CaratteristicaScelta> caratteristicheScelte) {
        this.caratteristicheScelte = caratteristicheScelte;
    }
}
