package it.univaq.webdelivery.data.dao;

import it.univaq.webdelivery.data.DbConnectionManager;
import it.univaq.webdelivery.data.model.Utente;
import it.univaq.webdelivery.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UtenteDAO {

    public Utente authenticate(String email, String password) {
        String sql = "SELECT * FROM Utente WHERE email = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utente utente = mapUtente(rs);
                    if (PasswordUtil.verify(password, utente.getPasswordHash())) {
                        return utente;
                    }
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return null;
    }

    public boolean registerCliente(Utente u) {
        String sql = "INSERT INTO Utente (ruolo, email, password_hash, nome, telefono, indirizzo) VALUES ('Cliente', ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getNome());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getIndirizzo());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public List<Utente> getStaffList() {
        List<Utente> staff = new ArrayList<>();
        String sql = "SELECT * FROM Utente WHERE ruolo = 'Personale'";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                staff.add(mapUtente(rs));
            }
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return staff;
    }

    public boolean addStaff(Utente u) {
        String sql = "INSERT INTO Utente (ruolo, email, password_hash, nome, telefono, indirizzo) VALUES ('Personale', ?, ?, ?, NULL, NULL)";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getEmail());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getNome());
            
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public boolean removeStaff(int id) {
        // Sostituita la DELETE con un UPDATE per declassare l'utente
        String sql = "UPDATE Utente SET ruolo = 'Utente-Eliminato' WHERE id = ? AND ruolo = 'Personale'";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return false;
    }

    public Utente getById(int id) {
        String sql = "SELECT * FROM Utente WHERE id = ?";
        try (Connection conn = DbConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUtente(rs);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(UtenteDAO.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
        }
        return null;
    }

    private Utente mapUtente(ResultSet rs) throws SQLException {
        return new Utente(
            rs.getInt("id"),
            rs.getString("ruolo"),
            rs.getString("email"),
            rs.getString("password_hash"),
            rs.getString("nome"),
            rs.getString("telefono"),
            rs.getString("indirizzo")
        );
    }
}