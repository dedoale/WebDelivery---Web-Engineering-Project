package it.univaq.webdelivery.data.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.univaq.webdelivery.data.DbConnectionManager;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.RigaOrdine;
import it.univaq.webdelivery.data.model.StoricoStatoOrdine;

public class OrdineDAO {

    public boolean create(Ordine o) {
        String insertOrdineSql = "INSERT INTO Ordine (id_cliente, prezzo_totale, tempo_consegna_stimato, orario_richiesto, stato_attuale) VALUES (?, ?, ?, ?, ?)";
        String insertRigaSql = "INSERT INTO Riga_Ordine (id_ordine, id_prodotto, quantita, prezzo_base_al_momento) VALUES (?, ?, ?, ?)";
        String insertVarianteSql = "INSERT INTO Variante_Riga_Ordine (id_riga, id_caratteristica, diff_prezzo_al_momento) VALUES (?, ?, ?)";
        String insertStoricoSql = "INSERT INTO Storico_Stato_Ordine (id_ordine, id_personale, stato, timestamp_modifica) VALUES (?, ?, 'Inserito', CURRENT_TIMESTAMP)";

        Connection conn = null;
        try {
            conn = DbConnectionManager.getConnection();
            conn.setAutoCommit(false); // Inizia transazione

            // Inserisci Ordine
            try (PreparedStatement ps = conn.prepareStatement(insertOrdineSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, o.getIdCliente());
                ps.setDouble(2, o.getPrezzoTotale());
                ps.setInt(3, o.getTempoConsegnaStimato());
                // Formato MySQL DATETIME: YYYY-MM-DD HH:MM:SS
                ps.setTimestamp(4, java.sql.Timestamp.valueOf(o.getOrarioRichiesto()));
                ps.setString(5, o.getStatoAttuale());

                int rows = ps.executeUpdate();
                if (rows == 0) {
                    throw new SQLException("Creazione ordine fallita, nessuna riga inserita.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        o.setIdDb(rs.getInt(1));
                    } else {
                        throw new SQLException("Creazione ordine fallita, nessun ID generato recuperato.");
                    }
                }
            }

            // Inserisci righe ordine
            for (RigaOrdine riga : o.getRighe()) {
                try (PreparedStatement ps = conn.prepareStatement(insertRigaSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, o.getIdDb());
                    ps.setInt(2, riga.getProdottoId());
                    ps.setInt(3, riga.getQty());
                    ps.setDouble(4, riga.getPrezzoBaseAlMomento());

                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            riga.setId(rs.getInt(1));
                        } else {
                            throw new SQLException("Inserimento riga ordine fallito, nessun ID generato recuperato.");
                        }
                    }
                }

                // Inserisci varianti per ciascuna riga
                for (RigaOrdine.CaratteristicaScelta var : riga.getCaratteristicheScelte()) {
                    try (PreparedStatement ps = conn.prepareStatement(insertVarianteSql)) {
                        ps.setInt(1, riga.getId());
                        ps.setInt(2, var.getIdCaratteristica());
                        ps.setDouble(3, var.getDiffPrezzoAlMomento());
                        ps.executeUpdate();
                    }
                }
            }

            // Inserisci lo stato iniziale nello Storico (id_personale = id_cliente poiché l'ordine viene inserito dal cliente)
            try (PreparedStatement ps = conn.prepareStatement(insertStoricoSql)) {
                ps.setInt(1, o.getIdDb());
                ps.setInt(2, o.getIdCliente());
                ps.executeUpdate();
            }

            conn.commit(); // Conferma transazione
            return true;

        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback in caso di errore
                } catch (SQLException ex) {
                    Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", ex);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        }
        return false;
    }

    public List<Ordine> getOrdiniCliente(int idCliente) {
        List<Ordine> list = new ArrayList<>();
        String sql = "SELECT o.*, u.nome AS nome_cliente FROM Ordine o " +
                     "JOIN Utente u ON o.id_cliente = u.id WHERE o.id_cliente = ? ORDER BY o.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(fetchOrdineRelazioni(conn, rs));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return list;
    }

    public List<Ordine> getOrdiniCorrenti() {
        List<Ordine> list = new ArrayList<>();
        String sql = "SELECT o.*, u.nome AS nome_cliente FROM Ordine o " +
                     "JOIN Utente u ON o.id_cliente = u.id WHERE o.stato_attuale != 'Consegnato' ORDER BY o.id ASC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(fetchOrdineRelazioni(conn, rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return list;
    }

    public List<Ordine> getAll() {
        List<Ordine> list = new ArrayList<>();
        String sql = "SELECT o.*, u.nome AS nome_cliente FROM Ordine o " +
                     "JOIN Utente u ON o.id_cliente = u.id ORDER BY o.id DESC";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(fetchOrdineRelazioni(conn, rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return list;
    }

    public Ordine getById(int idOrdine) {
        String sql = "SELECT o.*, u.nome AS nome_cliente FROM Ordine o " +
                     "JOIN Utente u ON o.id_cliente = u.id WHERE o.id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return fetchOrdineRelazioni(conn, rs);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return null;
    }

    public boolean updateStato(int idOrdine, String nuovoStato, int idStaff) {
        // Definizione della sequenza corretta degli stati
        List<String> sequenzaStati = List.of("Inserito", "In preparazione", "Pronto", "In consegna", "Consegnato");

        String selectSql = "SELECT stato_attuale FROM Ordine WHERE id = ? FOR UPDATE";
        String updateSql = "UPDATE Ordine SET stato_attuale = ? WHERE id = ?";
        String insertStoricoSql = "INSERT INTO Storico_Stato_Ordine (id_ordine, id_personale, stato, timestamp_modifica) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        
        Connection conn = null;
        try {
            conn = DbConnectionManager.getConnection();
            conn.setAutoCommit(false);

            // Recupero stato attuale db
            String statoAttuale = null;
            try (PreparedStatement psSelect = conn.prepareStatement(selectSql)) {
                psSelect.setInt(1, idOrdine);
                try (ResultSet rs = psSelect.executeQuery()) {
                    if (rs.next()) {
                        statoAttuale = rs.getString("stato_attuale");
                    }
                }
            }

            // Se l'ordine non esiste, annulla la transazione
            if (statoAttuale == null) {
                conn.rollback();
                return false;
            }

            // Controllo progressione dello stato
            int indexAttuale = sequenzaStati.indexOf(statoAttuale);
            int indexNuovo = sequenzaStati.indexOf(nuovoStato);

            // Si puo' avanzare solo di 1 step
            if (indexAttuale == -1 || indexNuovo == -1 || indexNuovo != indexAttuale + 1) {
                System.out.println("[SICUREZZA] Tentativo di transizione di stato non valida da " + statoAttuale + " a " + nuovoStato);
                conn.rollback(); // Annulla la transazione
                return false; 
            }

            // Update stato dell'ordine nel db
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setString(1, nuovoStato);
                ps.setInt(2, idOrdine);
                ps.executeUpdate();
            }

            // Inserimento nello storico nel db
            try (PreparedStatement ps = conn.prepareStatement(insertStoricoSql)) {
                ps.setInt(1, idOrdine);
                ps.setInt(2, idStaff);
                ps.setString(3, nuovoStato);
                ps.executeUpdate();
            }

            conn.commit();
            return true;


        } catch (SQLException e) {
            Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
            if (conn != null) {
                try {
                    conn.rollback(); // rollback totale in caso di eccezioni sql
                } catch (SQLException ex) {
                    Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", ex);
                }
            }
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // ripristina l'auto-commit
                    conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(OrdineDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        }
        return false;
    }

    private Ordine fetchOrdineRelazioni(Connection conn, ResultSet rs) throws SQLException {
        Ordine o = new Ordine(
            rs.getInt("id"),
            rs.getInt("id_cliente"),
            rs.getString("nome_cliente"),
            rs.getDouble("prezzo_totale"),
            rs.getInt("tempo_consegna_stimato"),
            rs.getTimestamp("orario_richiesto").toLocalDateTime(),
            rs.getString("stato_attuale")
        );
        o.setRighe(getRighePerOrdine(conn, o.getIdDb()));
        o.setStorico(getStoricoPerOrdine(conn, o.getIdDb()));
        return o;
    }

    private List<RigaOrdine> getRighePerOrdine(Connection conn, int idOrdine) throws SQLException {
        List<RigaOrdine> list = new ArrayList<>();
        String sql = "SELECT r.*, p.nome AS nome_prodotto FROM Riga_Ordine r " +
                     "JOIN Prodotto p ON r.id_prodotto = p.id WHERE r.id_ordine = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RigaOrdine r = new RigaOrdine(
                        rs.getInt("id"),
                        rs.getInt("id_ordine"),
                        rs.getInt("id_prodotto"),
                        rs.getString("nome_prodotto"),
                        rs.getInt("quantita"),
                        rs.getDouble("prezzo_base_al_momento")
                    );
                    r.setOpzioni(getVariantiNomiPerRiga(conn, r.getId()));
                    list.add(r);
                }
            }
        }
        return list;
    }

    private List<String> getVariantiNomiPerRiga(Connection conn, int idRiga) throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT c.nome FROM Variante_Riga_Ordine v " +
                     "JOIN Caratteristica c ON v.id_caratteristica = c.id WHERE v.id_riga = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idRiga);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(rs.getString("nome"));
                }
            }
        }
        return list;
    }

    private List<StoricoStatoOrdine> getStoricoPerOrdine(Connection conn, int idOrdine) throws SQLException {
        List<StoricoStatoOrdine> list = new ArrayList<>();
        String sql = "SELECT s.*, u.nome AS nome_personale, u.ruolo FROM Storico_Stato_Ordine s " +
                     "JOIN Utente u ON s.id_personale = u.id WHERE s.id_ordine = ? ORDER BY s.id ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idOrdine);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ruolo = rs.getString("ruolo");
                    // Se lo stato è inserito dal cliente, mostriamo null (viene visualizzato come "Cliente")
                    String nomePersonale = "Cliente".equals(ruolo) ? null : rs.getString("nome_personale");
                    
                    list.add(new StoricoStatoOrdine(
                        rs.getInt("id"),
                        rs.getInt("id_ordine"),
                        rs.getInt("id_personale"),
                        nomePersonale,
                        rs.getString("stato"),
                        rs.getTimestamp("timestamp_modifica").toLocalDateTime()
                    ));
                }
            }
        }
        return list;
    }
}