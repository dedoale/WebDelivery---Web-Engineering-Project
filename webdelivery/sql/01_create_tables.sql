CREATE DATABASE IF NOT EXISTS webdelivery;

    USE webdelivery;

    DROP TABLE IF EXISTS Storico_Stato_Ordine;
    DROP TABLE IF EXISTS Variante_Riga_Ordine;
    DROP TABLE IF EXISTS Riga_Ordine;
    DROP TABLE IF EXISTS Ordine;
    DROP TABLE IF EXISTS Caratteristica;
    DROP TABLE IF EXISTS Gruppo_Caratteristica;
    DROP TABLE IF EXISTS Composizione_Ingrediente;
    DROP TABLE IF EXISTS Ingrediente;
    DROP TABLE IF EXISTS Prodotto;
    DROP TABLE IF EXISTS Utente;


    -- UTENTI
    CREATE TABLE Utente (
        id INT AUTO_INCREMENT PRIMARY KEY,
        ruolo ENUM('Proprietario', 'Personale', 'Cliente', 'Utente-Eliminato') NOT NULL,
        email VARCHAR(255) UNIQUE NOT NULL,
        password_hash VARCHAR(255) NOT NULL,
        nome VARCHAR(100) NOT NULL,
        telefono VARCHAR(20),
        indirizzo VARCHAR(255)
    );

    -- CATALOGO
    CREATE TABLE Prodotto (
        id INT AUTO_INCREMENT PRIMARY KEY,
        nome VARCHAR(100) NOT NULL,
        descrizione TEXT NOT NULL,
        prezzo_base DECIMAL(6,2) NOT NULL,
        categoria VARCHAR(50) DEFAULT 'Altro',
        immagine VARCHAR(255),
        tempo_preparazione INT NOT NULL, -- espresso in minuti
        procedura TEXT,
        is_active BOOLEAN NOT NULL DEFAULT 1
    );

    CREATE TABLE Ingrediente (
        id INT AUTO_INCREMENT PRIMARY KEY,
        nome VARCHAR(100) NOT NULL
    );

    CREATE TABLE Composizione_Ingrediente (
        id_prodotto INT,
        id_ingrediente INT,
        quantita VARCHAR(50),
        PRIMARY KEY (id_prodotto, id_ingrediente),
        FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id) ON DELETE CASCADE,
        FOREIGN KEY (id_ingrediente) REFERENCES Ingrediente(id) ON DELETE CASCADE
    );

    CREATE TABLE Gruppo_Caratteristica (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_prodotto INT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        mutua_esclusione BOOLEAN NOT NULL DEFAULT 0,
        FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id) ON DELETE CASCADE
    );

    CREATE TABLE Caratteristica (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_gruppo INT NOT NULL,
        nome VARCHAR(100) NOT NULL,
        descrizione VARCHAR(255),
        differenza_prezzo DECIMAL(6,2) NOT NULL DEFAULT 0.00,
        is_default BOOLEAN NOT NULL DEFAULT 0,
        FOREIGN KEY (id_gruppo) REFERENCES Gruppo_Caratteristica(id) ON DELETE CASCADE
    );

    -- ORDINI
    CREATE TABLE Ordine (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_cliente INT NOT NULL,
        prezzo_totale DECIMAL(8,2) NOT NULL,
        tempo_consegna_stimato INT NOT NULL, -- Espresso in minuti
        orario_richiesto DATETIME NOT NULL,
        stato_attuale ENUM('Inserito', 'In preparazione', 'Pronto', 'In consegna', 'Consegnato') NOT NULL DEFAULT 'Inserito',
        FOREIGN KEY (id_cliente) REFERENCES Utente(id)
    );

    CREATE TABLE Riga_Ordine (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_ordine INT NOT NULL,
        id_prodotto INT NOT NULL,
        quantita INT NOT NULL DEFAULT 1,
        prezzo_base_al_momento DECIMAL(6,2) NOT NULL,
        FOREIGN KEY (id_ordine) REFERENCES Ordine(id) ON DELETE CASCADE,
        FOREIGN KEY (id_prodotto) REFERENCES Prodotto(id)
    );

    -- PERSONALIZZAZIONI ORDINI
    CREATE TABLE Variante_Riga_Ordine (
        id_riga INT NOT NULL,
        id_caratteristica INT NOT NULL,
        diff_prezzo_al_momento DECIMAL(6,2) NOT NULL,
        PRIMARY KEY (id_riga, id_caratteristica),
        FOREIGN KEY (id_riga) REFERENCES Riga_Ordine(id) ON DELETE CASCADE,
        FOREIGN KEY (id_caratteristica) REFERENCES Caratteristica(id)
    );

    CREATE TABLE Storico_Stato_Ordine (
        id INT AUTO_INCREMENT PRIMARY KEY,
        id_ordine INT NOT NULL,
        id_personale INT NOT NULL,
        stato ENUM('Inserito', 'In preparazione', 'Pronto', 'In consegna', 'Consegnato') NOT NULL,
        timestamp_modifica DATETIME DEFAULT CURRENT_TIMESTAMP,
        FOREIGN KEY (id_ordine) REFERENCES Ordine(id) ON DELETE CASCADE,
        FOREIGN KEY (id_personale) REFERENCES Utente(id)
    );