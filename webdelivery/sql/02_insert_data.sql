USE webdelivery;

-- UTENTI
INSERT INTO Utente (id, ruolo, email, password_hash, nome, telefono, indirizzo) VALUES
(1, 'Proprietario', 'admin@univaqfooddelivery.it', 'hash_password', 'Amministratore', '0862000000', 'Via Vetoio, L''Aquila'),
(2, 'Personale', 'luca@univaqfooddelivery.it', 'hash_password', 'Luca B.', NULL, NULL),
(3, 'Personale', 'sara@univaqfooddelivery.it', 'hash_password', 'Sara M.', NULL, NULL),
(4, 'Personale', 'marco@univaqfooddelivery.it', 'hash_password', 'Marco T.', NULL, NULL),
(5, 'Cliente', 'mario.rossi@email.it', 'hash_password', 'Mario Rossi', '3331234567', 'Via Roma 1, L''Aquila'),
(6, 'Cliente', 'giulia.bianchi@email.it', 'hash_password', 'Giulia Bianchi', '3332222222', 'Via Milano 2, L''Aquila'),
(7, 'Cliente', 'andrea.verdi@email.it', 'hash_password', 'Andrea Verdi', '3333333333', 'Via Torino 3, L''Aquila'),
(8, 'Cliente', 'elena.conti@email.it', 'hash_password', 'Elena Conti', '3334444444', 'Via Napoli 4, L''Aquila');


-- PRODOTTI
INSERT INTO Prodotto (id, nome, descrizione, prezzo_base, immagine, tempo_preparazione, procedura) VALUES
(1, 'Bruschette miste', 'Pane casereccio tostato, pomodoro fresco, olio extravergine e basilico.', 5.50, 'bruschette.jpg', 8, 'Tostare il pane, strofinare con aglio, condire con pomodoro a cubetti, olio e basilico.'),
(2, 'Tagliatelle al ragù', 'Pasta fresca all''uovo con ragù di carne cotto lentamente per 4 ore.', 9.00, 'tagliatelle.jpg', 15, 'Cuocere la pasta 3 minuti in acqua salata, mantecare con il ragù caldo, spolverare di parmigiano.'),
(3, 'Pizza Margherita', 'Pomodoro San Marzano, fiordilatte, basilico fresco, cotta nel forno a legna.', 7.00, 'pizza-margherita.jpg', 12, 'Stendere l''impasto, condire con pomodoro e mozzarella, cuocere nel forno a legna a 400°C per 90 secondi, guarnire con basilico.'),
(4, 'Pizza Diavola', 'Pomodoro, fiordilatte, salame piccante, olio al peperoncino.', 8.00, 'pizza-diavola.jpg', 12, 'Stendere l''impasto, condire con pomodoro, mozzarella e salame, cuocere nel forno a legna, rifinire con olio piccante in uscita.'),
(5, 'Tiramisù della casa', 'Ricetta originale con savoiardi, mascarpone e caffè della torrefazione locale.', 4.50, 'tiramisu.jpg', 3, 'Porzionare dalla vaschetta preparata in mattinata, spolverare di cacao al momento del servizio.'),
(6, 'Caffè', 'Espresso della nostra torrefazione di fiducia.', 1.00, 'caffe.jpg', 2, 'Estrazione singola da macinato fresco, 25 secondi circa.');


-- INGREDIENTI E COMPOSIZIONI
INSERT INTO Ingrediente (id, nome) VALUES
(1, 'Pane casereccio'), (2, 'Pomodoro'), (3, 'Basilico fresco'), (4, 'Olio extravergine'),
(5, 'Tagliatelle fresche'), (6, 'Ragù di carne'), (7, 'Parmigiano'), (8, 'Impasto pizza'),
(9, 'Pomodoro San Marzano'), (10, 'Fiordilatte'), (11, 'Salame piccante'), (12, 'Olio al peperoncino'),
(13, 'Savoiardi'), (14, 'Crema al mascarpone'), (15, 'Caffè espresso'), (16, 'Cacao amaro'), (17, 'Miscela espresso');

INSERT INTO Composizione_Ingrediente (id_prodotto, id_ingrediente, quantita) VALUES
(1, 1, '4 fette'), (1, 2, '2 medi'), (1, 3, 'q.b.'), (1, 4, '2 cucchiai'),
(2, 5, '120 g'), (2, 6, '150 g'), (2, 7, '20 g'),
(3, 8, '250 g'), (3, 9, '80 g'), (3, 10, '100 g'), (3, 3, 'q.b.'),
(4, 8, '250 g'), (4, 2, '80 g'), (4, 10, '100 g'), (4, 11, '60 g'), (4, 12, 'q.b.'),
(5, 13, '4'), (5, 14, '150 g'), (5, 15, '30 ml'), (5, 16, 'q.b.'),
(6, 17, '7 g');


-- PERSONALIZZAZIONI
INSERT INTO Gruppo_Caratteristica (id, id_prodotto, nome, mutua_esclusione) VALUES
(1, 2, 'Porzione', 1),
(2, 3, 'Impasto', 1),
(3, 3, 'Aggiunte', 0),
(4, 4, 'Piccantezza', 1),
(5, 6, 'Zucchero', 1),
(6, 6, 'Aggiunte', 0);

INSERT INTO Caratteristica (id, id_gruppo, nome, differenza_prezzo, is_default) VALUES
(1, 1, 'Normale', 0.00, 1),
(2, 1, 'Abbondante', 2.00, 0),
(3, 2, 'Classico', 0.00, 1),
(4, 2, 'Integrale', 0.50, 0),
(5, 3, 'Extra mozzarella', 1.50, 0),
(6, 3, 'Origano fresco', 0.30, 0),
(7, 4, 'Normale', 0.00, 1),
(8, 4, 'Extra piccante', 0.00, 0),
(9, 5, 'Senza zucchero', -0.05, 0),
(10, 5, 'Zuccherato', 0.00, 1),
(11, 5, 'Molto zuccherato', 0.00, 0),
(12, 6, 'Con panna', 0.50, 0),
(13, 6, 'Freddo', 1.00, 0);


-- ORDINI
INSERT INTO Ordine (id, id_cliente, prezzo_totale, tempo_consegna_stimato, orario_richiesto, stato_attuale) VALUES
(1, 5, 24.50, 40, '2026-07-14 20:15:00', 'In preparazione'), -- Mario Rossi
(2, 6, 14.50, 35, '2026-07-14 20:00:00', 'In preparazione'), -- Giulia Bianchi
(3, 7, 16.00, 35, '2026-07-14 19:45:00', 'Pronto'),          -- Andrea Verdi
(4, 8, 12.50, 25, '2026-07-14 19:30:00', 'In consegna');     -- Elena Conti


INSERT INTO Riga_Ordine (id, id_ordine, id_prodotto, quantita, prezzo_base_al_momento) VALUES
(1, 1, 3, 1, 7.00), (2, 1, 4, 1, 8.00), (3, 1, 5, 2, 4.50), -- Ordine 1 (Mario)
(4, 2, 2, 1, 9.00), (5, 2, 1, 1, 5.50),                     -- Ordine 2 (Giulia)
(6, 3, 3, 2, 7.00), (7, 3, 6, 2, 1.00),                     -- Ordine 3 (Andrea)
(8, 4, 4, 1, 8.00), (9, 4, 5, 1, 4.50);                     -- Ordine 4 (Elena)


INSERT INTO Variante_Riga_Ordine (id_riga, id_caratteristica, diff_prezzo_al_momento) VALUES
(1, 4, 0.50),  -- Impasto integrale per Margherita di Mario
(2, 8, 0.00),  -- Extra piccante per Diavola di Mario
(4, 2, 2.00),  -- Abbondante per Tagliatelle di Giulia
(7, 10, 0.00); -- Zuccherato per Caffè di Andrea


-- STORICO DEGLI STATI
INSERT INTO Storico_Stato_Ordine (id_ordine, id_personale, stato, timestamp_modifica) VALUES
(1, 5, 'Inserito', '2026-07-14 19:40:00'), 
(1, 2, 'In preparazione', '2026-07-14 19:52:00'),
(2, 6, 'Inserito', '2026-07-14 19:20:00'), 
(2, 2, 'In preparazione', '2026-07-14 19:30:00'),
(3, 7, 'Inserito', '2026-07-14 19:05:00'), 
(3, 3, 'In preparazione', '2026-07-14 19:10:00'), 
(3, 3, 'Pronto', '2026-07-14 19:24:00'),
(4, 8, 'Inserito', '2026-07-14 18:50:00'), 
(4, 2, 'In preparazione', '2026-07-14 18:55:00'), 
(4, 2, 'Pronto', '2026-07-14 19:08:00'), 
(4, 4, 'In consegna', '2026-07-14 19:12:00');