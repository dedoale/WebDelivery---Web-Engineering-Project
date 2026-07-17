package it.univaq.webdelivery.data.model;

import java.util.ArrayList;
import java.util.List;

public class Prodotto {
    private int id;
    private String nome;
    private String descrizione;
    private double prezzo_base; // matches prezzo_base in JSON
    private String immagine;
    private int tempo_preparazione; // matches tempo_preparazione in JSON
    private String procedura;
    private boolean is_active = true;
    
    // Per semplicità di JSON mapping per il client, inseriamo direttamente le relazioni
    private List<IngredienteConQuantita> ingredienti = new ArrayList<>();
    private List<GruppoCaratteristica> gruppi = new ArrayList<>();

    private String categoria = "Altro"; 

    public static class IngredienteConQuantita {
        private String nome;
        private String quantita;

        public IngredienteConQuantita() {}

        public IngredienteConQuantita(String nome, String quantita) {
            this.nome = nome;
            this.quantita = quantita;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getQuantita() {
            return quantita;
        }

        public void setQuantita(String quantita) {
            this.quantita = quantita;
        }
    }

    public Prodotto() {}

    public Prodotto(int id, String nome, String descrizione, double prezzo_base, String immagine, int tempo_preparazione, String procedura) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
        this.prezzo_base = prezzo_base;
        this.immagine = immagine;
        this.tempo_preparazione = tempo_preparazione;
        this.procedura = procedura;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public double getPrezzoBase() {
        return prezzo_base;
    }

    public void setPrezzoBase(double prezzo_base) {
        this.prezzo_base = prezzo_base;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public int getTempoPreparazione() {
        return tempo_preparazione;
    }

    public void setTempoPreparazione(int tempo_preparazione) {
        this.tempo_preparazione = tempo_preparazione;
    }

    public String getProcedura() {
        return procedura;
    }

    public void setProcedura(String procedura) {
        this.procedura = procedura;
    }

    public List<IngredienteConQuantita> getIngredienti() {
        return ingredienti;
    }

    public void setIngredienti(List<IngredienteConQuantita> ingredienti) {
        this.ingredienti = ingredienti;
    }

    public List<GruppoCaratteristica> getGruppi() {
        return gruppi;
    }

    public void setGruppi(List<GruppoCaratteristica> gruppi) {
        this.gruppi = gruppi;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isAttivo() {
        return is_active;
    }

    public void setAttivo(boolean is_attivo) {
        this.is_active = is_attivo;
    }
}