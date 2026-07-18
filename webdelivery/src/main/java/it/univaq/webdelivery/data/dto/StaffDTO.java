package it.univaq.webdelivery.data.dto;

import it.univaq.webdelivery.data.model.Utente;

/**
 * Rappresenta un membro del personale come deve essere visto dal
 * proprietario nella dashboard (proprietario.js). Serializzare
 * direttamente un Utente esporrebbe anche passwordHash, telefono e
 * indirizzo: campi che non servono a quella vista e che non devono
 * comunque finire in una risposta JSON.
 */
public class StaffDTO {
    private final int id;
    private final String nome;
    private final String email;

    public StaffDTO(Utente u) {
        this.id = u.getId();
        this.nome = u.getNome();
        this.email = u.getEmail();
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}