package it.univaq.webdelivery.data.model;

public class Utente {
    private int id;
    private String ruolo; // 'Proprietario', 'Personale', 'Cliente'
    private String email;
    private String passwordHash; // può contenere anche la password in chiaro per i test iniziali
    private String nome;
    private String telefono;
    private String indirizzo;

    public Utente() {}

    public Utente(int id, String ruolo, String email, String passwordHash, String nome, String telefono, String indirizzo) {
        this.id = id;
        this.ruolo = ruolo;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nome = nome;
        this.telefono = telefono;
        this.indirizzo = indirizzo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRuolo() {
        return ruolo;
    }

    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }
}
