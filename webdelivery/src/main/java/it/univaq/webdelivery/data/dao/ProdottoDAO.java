package it.univaq.webdelivery.data.dao;

import it.univaq.webdelivery.data.DbConnectionManager;
import it.univaq.webdelivery.data.model.Caratteristica;
import it.univaq.webdelivery.data.model.GruppoCaratteristica;
import it.univaq.webdelivery.data.model.Prodotto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProdottoDAO {

    public List<Prodotto> getAll() {
        List<Prodotto> prodotti = new ArrayList<>();
        // Estraiamo solo i prodotti attualmente attivi
        String sql = "SELECT * FROM Prodotto WHERE is_active = 1";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Prodotto p = mapProdotto(rs);
                p.setIngredienti(getIngredientiPerProdotto(conn, p.getId()));
                p.setGruppi(getGruppiPerProdotto(conn, p.getId()));
                prodotti.add(p);
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return prodotti;
    }

    public Prodotto getById(int id) {
        String sql = "SELECT * FROM Prodotto WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Prodotto p = mapProdotto(rs);
                    p.setIngredienti(getIngredientiPerProdotto(conn, p.getId()));
                    p.setGruppi(getGruppiPerProdotto(conn, p.getId()));
                    return p;
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return null;
    }

    public boolean create(Prodotto p) {
        String sql = "INSERT INTO Prodotto (nome, descrizione, prezzo_base, immagine, tempo_preparazione, procedura, categoria) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setString(4, p.getImmagine());
            ps.setInt(5, p.getTempoPreparazione());
            ps.setString(6, p.getProcedura());
            ps.setString(7, p.getCategoria());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                saveIngredienti(conn, p);
                saveGruppi(conn, p);
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public boolean update(Prodotto p) {
        String sql = "UPDATE Prodotto SET nome = ?, descrizione = ?, prezzo_base = ?, immagine = ?, tempo_preparazione = ?, procedura = ?, categoria = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setString(4, p.getImmagine());
            ps.setInt(5, p.getTempoPreparazione());
            ps.setString(6, p.getProcedura());
            ps.setString(7, p.getCategoria());
            ps.setInt(8, p.getId());

            boolean updated = ps.executeUpdate() > 0;
            if (updated) {
                updateIngredienti(conn, p);
                updateGruppi(conn, p);
            }
            return updated;
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public boolean delete(int id) {
        // Al posto di cancellare la riga, la "disattiviamo"
        String sql = "UPDATE Prodotto SET is_active = 0 WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    // prende i prodotti inattivi
    public List<Prodotto> getInactive() {
        List<Prodotto> prodotti = new ArrayList<>();
        String sql = "SELECT * FROM Prodotto WHERE is_active = 0";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                prodotti.add(mapProdotto(rs)); // Ricicla il metodo di mappatura esistente
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore DB", e);
        }
        return prodotti;
    }

    // ripristina i prodotti inattivi
    public boolean restore(int id) {
        String sql = "UPDATE Prodotto SET is_active = 1 WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore DB", e);
        }
        return false;
    }

    // Carica ingredienti e quantità per un prodotto
    private List<Prodotto.IngredienteConQuantita> getIngredientiPerProdotto(Connection conn, int idProdotto) throws SQLException {
        List<Prodotto.IngredienteConQuantita> list = new ArrayList<>();
        String sql = "SELECT i.nome, ci.quantita FROM Composizione_Ingrediente ci " +
                     "JOIN Ingrediente i ON ci.id_ingrediente = i.id WHERE ci.id_prodotto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Prodotto.IngredienteConQuantita(
                        rs.getString("nome"),
                        rs.getString("quantita")
                    ));
                }
            }
        }
        return list;
    }

    // Carica i gruppi di caratteristiche per un prodotto
    private List<GruppoCaratteristica> getGruppiPerProdotto(Connection conn, int idProdotto) throws SQLException {
        List<GruppoCaratteristica> list = new ArrayList<>();
        String sql = "SELECT * FROM Gruppo_Caratteristica WHERE id_prodotto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProdotto);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    GruppoCaratteristica g = new GruppoCaratteristica(
                        rs.getInt("id"),
                        rs.getInt("id_prodotto"),
                        rs.getString("nome"),
                        rs.getBoolean("mutua_esclusione")
                    );
                    g.setOpzioni(getCaratteristichePerGruppo(conn, g.getId()));
                    list.add(g);
                }
            }
        }
        return list;
    }

    // Carica le caratteristiche di un determinato gruppo
    private List<Caratteristica> getCaratteristichePerGruppo(Connection conn, int idGruppo) throws SQLException {
        List<Caratteristica> list = new ArrayList<>();
        String sql = "SELECT * FROM Caratteristica WHERE id_gruppo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGruppo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Caratteristica(
                        rs.getInt("id"),
                        rs.getInt("id_gruppo"),
                        rs.getString("nome"),
                        rs.getString("descrizione"),
                        rs.getDouble("differenza_prezzo"),
                        rs.getBoolean("is_default")
                    ));
                }
            }
        }
        return list;
    }

    private void saveIngredienti(Connection conn, Prodotto p) throws SQLException {
        if (p.getIngredienti() == null || p.getIngredienti().isEmpty()) return;
        
        String findIngrediente = "SELECT id FROM Ingrediente WHERE LOWER(nome) = LOWER(?)";
        String insertIngrediente = "INSERT INTO Ingrediente (nome) VALUES (?)";
        String insertComposizione = "INSERT INTO Composizione_Ingrediente (id_prodotto, id_ingrediente, quantita) VALUES (?, ?, ?)";
        
        for (Prodotto.IngredienteConQuantita i : p.getIngredienti()) {
            int idIngrediente = -1;
            try (PreparedStatement psFind = conn.prepareStatement(findIngrediente)) {
                psFind.setString(1, i.getNome());
                try (ResultSet rs = psFind.executeQuery()) {
                    if (rs.next()) {
                        idIngrediente = rs.getInt("id");
                    }
                }
            }
            if (idIngrediente == -1) {
                try (PreparedStatement psIns = conn.prepareStatement(insertIngrediente, Statement.RETURN_GENERATED_KEYS)) {
                    psIns.setString(1, i.getNome());
                    psIns.executeUpdate();
                    try (ResultSet rs = psIns.getGeneratedKeys()) {
                        if (rs.next()) idIngrediente = rs.getInt(1);
                    }
                }
            }
            if (idIngrediente != -1) {
                try (PreparedStatement psComp = conn.prepareStatement(insertComposizione)) {
                    psComp.setInt(1, p.getId());
                    psComp.setInt(2, idIngrediente);
                    psComp.setString(3, i.getQuantita());
                    psComp.executeUpdate();
                }
            }
        }
    }

    private void updateIngredienti(Connection conn, Prodotto p) throws SQLException {
        // Eliminiamo le vecchie associazioni (safe perché non ci sono FK dipendenti da Composizione_Ingrediente)
        String deleteSql = "DELETE FROM Composizione_Ingrediente WHERE id_prodotto = ?";
        try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, p.getId());
            ps.executeUpdate();
        }
        // Inseriamo quelle nuove
        saveIngredienti(conn, p);
    }

    private void saveGruppi(Connection conn, Prodotto p) throws SQLException {
        if (p.getGruppi() == null) return;
        String insertGruppo = "INSERT INTO Gruppo_Caratteristica (id_prodotto, nome, mutua_esclusione) VALUES (?, ?, ?)";
        String insertCaratteristica = "INSERT INTO Caratteristica (id_gruppo, nome, differenza_prezzo, is_default) VALUES (?, ?, ?, ?)";

        for (GruppoCaratteristica g : p.getGruppi()) {
            int idGruppo = -1;
            try (PreparedStatement psG = conn.prepareStatement(insertGruppo, Statement.RETURN_GENERATED_KEYS)) {
                psG.setInt(1, p.getId());
                psG.setString(2, g.getNome());
                psG.setBoolean(3, g.isMutuaEsclusione());
                psG.executeUpdate();
                try (ResultSet rs = psG.getGeneratedKeys()) {
                    if (rs.next()) idGruppo = rs.getInt(1);
                }
            }
            if (idGruppo != -1 && g.getOpzioni() != null) {
                for (Caratteristica c : g.getOpzioni()) {
                    try (PreparedStatement psC = conn.prepareStatement(insertCaratteristica)) {
                        psC.setInt(1, idGruppo);
                        psC.setString(2, c.getNome());
                        psC.setDouble(3, c.getDifferenzaPrezzo());
                        psC.setBoolean(4, c.isDefault());
                        psC.executeUpdate();
                    }
                }
            }
        }
    }

    private void updateGruppi(Connection conn, Prodotto p) throws SQLException {
        if (p.getGruppi() == null) return;
        
        String updateGruppo = "UPDATE Gruppo_Caratteristica SET nome = ?, mutua_esclusione = ? WHERE id = ?";
        String updateCaratteristica = "UPDATE Caratteristica SET nome = ?, differenza_prezzo = ?, is_default = ? WHERE id = ?";
        
        // I nuovi inserimenti per aggiornamenti usano logiche simili a save
        String insertGruppo = "INSERT INTO Gruppo_Caratteristica (id_prodotto, nome, mutua_esclusione) VALUES (?, ?, ?)";
        String insertCaratteristica = "INSERT INTO Caratteristica (id_gruppo, nome, differenza_prezzo, is_default) VALUES (?, ?, ?, ?)";
        
        // Per semplicità non facciamo hard delete per evitare conflitti con gli ordini esistenti, facciamo solo update/insert.
        for (GruppoCaratteristica g : p.getGruppi()) {
            int idGruppo = g.getId();
            if (idGruppo > 0) {
                try (PreparedStatement ps = conn.prepareStatement(updateGruppo)) {
                    ps.setString(1, g.getNome());
                    ps.setBoolean(2, g.isMutuaEsclusione());
                    ps.setInt(3, idGruppo);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insertGruppo, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, p.getId());
                    ps.setString(2, g.getNome());
                    ps.setBoolean(3, g.isMutuaEsclusione());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) idGruppo = rs.getInt(1);
                    }
                }
            }
            
            if (idGruppo != -1 && g.getOpzioni() != null) {
                for (Caratteristica c : g.getOpzioni()) {
                    if (c.getId() > 0) {
                        try (PreparedStatement ps = conn.prepareStatement(updateCaratteristica)) {
                            ps.setString(1, c.getNome());
                            ps.setDouble(2, c.getDifferenzaPrezzo());
                            ps.setBoolean(3, c.isDefault());
                            ps.setInt(4, c.getId());
                            ps.executeUpdate();
                        }
                    } else {
                        try (PreparedStatement ps = conn.prepareStatement(insertCaratteristica)) {
                            ps.setInt(1, idGruppo);
                            ps.setString(2, c.getNome());
                            ps.setDouble(3, c.getDifferenzaPrezzo());
                            ps.setBoolean(4, c.isDefault());
                            ps.executeUpdate();
                        }
                    }
                }
            }
        }
    }

    private Prodotto mapProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto(
            rs.getInt("id"),
            rs.getString("nome"),
            rs.getString("descrizione"),
            rs.getDouble("prezzo_base"),
            rs.getString("immagine"),
            rs.getInt("tempo_preparazione"),
            rs.getString("procedura")
        );
       p.setCategoria(rs.getString("categoria"));
        p.setAttivo(rs.getBoolean("is_active"));
        return p;
    }
}