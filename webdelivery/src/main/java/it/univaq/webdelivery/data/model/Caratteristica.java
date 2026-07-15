package it.univaq.webdelivery.data.model;

public class Caratteristica {
    private int id;
    private int id_gruppo;
    private String nome;
    private String descrizione;
    private double differenza_prezzo;
    private boolean is_default;

    public Caratteristica() {
    }

    public Caratteristica(int id, int id_gruppo, String nome, String descrizione, double differenza_prezzo,
            boolean is_default) {
        this.id = id;
        this.id_gruppo = id_gruppo;
        this.nome = nome;
        this.descrizione = descrizione;
        this.differenza_prezzo = differenza_prezzo;
        this.is_default = is_default;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGruppo() {
        return id_gruppo;
    }

    public void setIdGruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
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

    public double getDifferenzaPrezzo() {
        return differenza_prezzo;
    }

    public void setDifferenzaPrezzo(double differenza_prezzo) {
        this.differenza_prezzo = differenza_prezzo;
    }

    public boolean isDefault() {
        return is_default;
    }

    public void setDefault(boolean is_default) {
        this.is_default = is_default;
    }
}
