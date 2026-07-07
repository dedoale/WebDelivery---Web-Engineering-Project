USE webdelivery;

-- UTENTI
-- In produzione le password saranno "hashate". Per ora usiamo testo in chiaro per facilitare i test.
INSERT INTO Utente (ruolo, email, password_hash, nome, telefono, indirizzo) VALUES
('Proprietario', 'admin@webdelivery.it', 'admin123', 'Mario Rossi', NULL, NULL),
('Personale', 'staff@webdelivery.it', 'staff123', 'Luigi Verdi', NULL, NULL),
('Cliente', 'cliente@email.it', 'cliente123', 'Giulia Bianchi', '3331234567', 'Via Roma 1, L''Aquila');


-- CATALOGO (Pizza Margherita)
INSERT INTO Prodotto (nome, descrizione, prezzo_base, tempo_preparazione, procedura, immagine) VALUES
('Pizza Margherita', 'La classica pizza con pomodoro e mozzarella', 6.00, 10, 'Stendere impasto, aggiungere pomodoro e mozzarella, infornare a 400 gradi per 2 minuti.', 'pizza-margherita.jpg');

-- Ingredienti
INSERT INTO Ingrediente (nome) VALUES ('Farina'), ('Pomodoro'), ('Mozzarella'), ('Basilico');


INSERT INTO Composizione_Ingrediente (id_prodotto, id_ingrediente, quantita) VALUES
(1, 1, '200g'), 
(1, 2, '100g'), 
(1, 3, '150g'), 
(1, 4, 'Qualche foglia');


-- CARATTERISTICHE E VARIANTI
-- Mutua esclusione, il cliente deve sceglierne solo uno
INSERT INTO Gruppo_Caratteristica (id_prodotto, nome, mutua_esclusione) VALUES (1, 'Tipo di Impasto', 1);

INSERT INTO Caratteristica (id_gruppo, nome, differenza_prezzo, is_default) VALUES
(1, 'Normale', 0.00, 1),        -- default
(1, 'Integrale', 1.50, 0),
(1, 'Senza Glutine', 2.00, 0);

-- No mutua esclusione, il cliente può sceglierne quante ne vuole
INSERT INTO Gruppo_Caratteristica (id_prodotto, nome, mutua_esclusione) VALUES (1, 'Aggiunte Extra', 0);

INSERT INTO Caratteristica (id_gruppo, nome, differenza_prezzo, is_default) VALUES
(2, 'Prosciutto Cotto', 1.50, 0),
(2, 'Funghi', 1.00, 0),
(2, 'Doppia Mozzarella', 1.00, 0);