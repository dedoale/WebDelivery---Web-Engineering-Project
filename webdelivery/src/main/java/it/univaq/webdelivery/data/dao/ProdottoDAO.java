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
        String sql = "SELECT * FROM Prodotto";
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
        String sql = "INSERT INTO Prodotto (nome, descrizione, prezzo_base, immagine, tempo_preparazione, procedura) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setString(4, p.getImmagine());
            ps.setInt(5, p.getTempoPreparazione());
            ps.setString(6, p.getProcedura());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public boolean update(Prodotto p) {
        String sql = "UPDATE Prodotto SET nome = ?, descrizione = ?, prezzo_base = ?, immagine = ?, tempo_preparazione = ?, procedura = ? WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNome());
            ps.setString(2, p.getDescrizione());
            ps.setDouble(3, p.getPrezzoBase());
            ps.setString(4, p.getImmagine());
            ps.setInt(5, p.getTempoPreparazione());
            ps.setString(6, p.getProcedura());
            ps.setInt(7, p.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Prodotto WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(ProdottoDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
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
        p.setCategoria(determineCategory(p.getId(), p.getNome()));
        return p;
    }

    // Determina la categoria basandosi sul nome del piatto (compatibilità all'indietro con SQL fornito)
    private String determineCategory(int id, String name) {
        String n = name.toLowerCase();
        if (n.contains("pizza")) return "Pizze";
        if (n.contains("tagliatelle") || n.contains("primo") || n.contains("pasta")) return "Primi";
        if (n.contains("bruschette") || n.contains("antipasto")) return "Antipasti";
        if (n.contains("tiramisù") || n.contains("tiramisu") || n.contains("dolce") || n.contains("panna cotta")) return "Dolci";
        if (n.contains("caffè") || n.contains("caffe") || n.contains("bevanda") || n.contains("acqua") || n.contains("birra") || n.contains("cola")) return "Bevande";
        return "Antipasti"; // Categoria di default
    }
}