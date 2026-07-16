package it.univaq.webdelivery.data.model;

import java.time.LocalDateTime;

public class StoricoStatoOrdine {
    private int id;
    private int id_ordine;
    private Integer id_personale; // può essere null se inserito dal cliente
    private String personale;      // Nome del membro del personale (es. "Luca B.") per il JSON
    private String stato;
    private LocalDateTime timestamp;      // Stringa ISO del timestamp per il JSON

    public StoricoStatoOrdine() {}

    public StoricoStatoOrdine(int id, int id_ordine, Integer id_personale, String personale, String stato, LocalDateTime timestamp) {
        this.id = id;
        this.id_ordine = id_ordine;
        this.id_personale = id_personale;
        this.personale = personale;
        this.stato = stato;
        this.timestamp = timestamp;
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

    public Integer getIdPersonale() {
        return id_personale;
    }

    public void setIdPersonale(Integer id_personale) {
        this.id_personale = id_personale;
    }

    public String getPersonale() {
        return personale;
    }

    public void setPersonale(String personale) {
        this.personale = personale;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
