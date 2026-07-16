package it.univaq.webdelivery.data.model;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

public class Ordine {
    private String id; // Formato stringa atteso dal frontend (es. "WD-1")
    private int id_db; // ID numerico reale nel database
    private int id_cliente;
    private String cliente; // Nome del cliente per lo staff (es. "Mario Rossi")
    private double prezzo_totale; // prezzo_totale nel JSON
    private int tempo_consegna_stimato;
    private LocalDateTime orario_richiesto; // ISO String (es. "2026-07-14T20:15:00")
    private String stato_attuale; // stato_attuale nel JSON
    
    private List<RigaOrdine> righe = new ArrayList<>();
    private List<StoricoStatoOrdine> storico = new ArrayList<>();

    public Ordine() {}

    public Ordine(int id_db, int id_cliente, String cliente, double prezzo_totale, int tempo_consegna_stimato, LocalDateTime orario_richiesto, String stato_attuale) {
        this.id_db = id_db;
        this.id = "WD-" + id_db;
        this.id_cliente = id_cliente;
        this.cliente = cliente;
        this.prezzo_totale = prezzo_totale;
        this.tempo_consegna_stimato = tempo_consegna_stimato;
        this.orario_richiesto = orario_richiesto;
        this.stato_attuale = stato_attuale;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIdDb() {
        return id_db;
    }

    public void setIdDb(int id_db) {
        this.id_db = id_db;
        this.id = "WD-" + id_db;
    }

    public int getIdCliente() {
        return id_cliente;
    }

    public void setIdCliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public double getPrezzoTotale() {
        return prezzo_totale;
    }

    public void setPrezzoTotale(double prezzo_totale) {
        this.prezzo_totale = prezzo_totale;
    }

    public int getTempoConsegnaStimato() {
        return tempo_consegna_stimato;
    }

    public void setTempoConsegnaStimato(int tempo_consegna_stimato) {
        this.tempo_consegna_stimato = tempo_consegna_stimato;
    }

    public LocalDateTime getOrarioRichiesto() {
        return orario_richiesto;
    }

    public void setOrarioRichiesto(LocalDateTime orario_richiesto) {
        this.orario_richiesto = orario_richiesto;
    }

    public String getStatoAttuale() {
        return stato_attuale;
    }

    public void setStatoAttuale(String stato_attuale) {
        this.stato_attuale = stato_attuale;
    }

    public List<RigaOrdine> getRighe() {
        return righe;
    }

    public void setRighe(List<RigaOrdine> righe) {
        this.righe = righe;
    }

    public List<StoricoStatoOrdine> getStorico() {
        return storico;
    }

    public void setStorico(List<StoricoStatoOrdine> storico) {
        this.storico = storico;
    }
}