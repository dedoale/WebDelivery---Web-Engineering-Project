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
    
    // Per semplicità di JSON mapping per il client, inseriamo direttamente le relazioni
    private List<IngredienteConQuantita> ingredienti = new ArrayList<>();
    private List<GruppoCaratteristica> gruppi = new ArrayList<>();

    // La categoria del prodotto (può essere ricavata o passata per raggruppamento se necessario,
    // sebbene nella tabella SQL non ci sia un campo categoria esplicito! 
    // Aspetta, controlliamo la tabella Prodotto nel file SQL:
    // CREATE TABLE Prodotto (
    //     id INT AUTO_INCREMENT PRIMARY KEY,
    //     nome VARCHAR(100) NOT NULL,
    //     descrizione TEXT NOT NULL,
    //     prezzo_base DECIMAL(6,2) NOT NULL,
    //     immagine VARCHAR(255),
    //     tempo_preparazione INT NOT NULL,
    //     procedura TEXT
    // );
    // Nessun campo "categoria" nella tabella Prodotto! 
    // Ma nel file data.js abbiamo `categoria: "Antipasti"`, ecc.
    // Come viene gestita la categoria se non c'è nel database? 
    // Aspetta, il file 02_insert_data.sql fa questo:
    // INSERT INTO Prodotto (nome, descrizione, prezzo_base, tempo_preparazione, procedura, immagine) VALUES ...
    // E non specifica una categoria. Come mai?
    // Facciamo un controllo rapido: la specifica menziona: "L'attività disporrà di un menu composto da prodotti ognuno dotato almeno di un nome, una descrizione, un prezzo e, opzionalmente, una (o più) immagini. Internamente (cioè in modo non visibile ai clienti), ogni prodotto avrà anche associati un tempo di preparazione, una lista di ingredienti..."
    // E nelle operazioni: "Gli utenti anonimi potranno visualizzare il menu (magari paginato, suddiviso per tipologia di prodotto... cercate di organizzarlo al meglio)"
    // Se la categoria non c'è nel database, possiamo aggiungerla come campo facoltativo o String `categoria` nella classe Java. 
    // Aspetta, se non c'è nella tabella Prodotto, forse l'utente l'ha dimenticata o vuole che la ricaviamo in altro modo, o forse possiamo aggiungerla nella tabella Prodotto. 
    // Diamo un'occhiata: la tabella Prodotto definita in 01_create_tables.sql NON ha il campo `categoria`. 
    // Possiamo aggiungere una proprietà `categoria` in `Prodotto.java` per permetterne la serializzazione (anche valorizzandola con un default o un valore ricavato, o modificando la tabella Prodotto per includere `categoria` - che sarebbe l'ideale! Ma non modifichiamo tabelle a meno che non sia necessario, anche se un campo `categoria VARCHAR(50)` è molto utile. Lasciamola in Java come proprietà privata con getter/setter, così Gson la serializza se impostata!)
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
}
