package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.dao.UtenteDAO;
import it.univaq.webdelivery.data.model.Prodotto;
import it.univaq.webdelivery.data.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ProprietarioServlet", urlPatterns = {
    "/proprietario", 
    "/proprietario/personale/aggiungi", 
    "/proprietario/personale/rimuovi",
    "/proprietario/menu/elimina"
})
public class ProprietarioServlet extends BaseServlet {
    private OrdineDAO ordineDAO;
    private UtenteDAO utenteDAO;
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDAO = new OrdineDAO();
        this.utenteDAO = new UtenteDAO();
        this.prodottoDAO = new ProdottoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // controllo sessione
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Proprietario")) return;

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("prodotti", prodottoDAO.getAll());
        dataModel.put("staff", utenteDAO.getStaffList());
        dataModel.put("ordini", ordineDAO.getAll());

        renderTemplate("proprietario.ftl", dataModel, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // controllo sessione
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Proprietario")) return;

        String path = request.getServletPath();

        if ("/proprietario/personale/aggiungi".equals(path)) {
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (nome != null && email != null && password != null) {
                Utente u = new Utente();
                u.setNome(nome);
                u.setEmail(email);
                u.setPasswordHash(password);
                u.setRuolo("Personale");
                utenteDAO.addStaff(u);
            }
        } else if ("/proprietario/personale/rimuovi".equals(path)) {
            String idStr = request.getParameter("staff_id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    utenteDAO.removeStaff(id);
                } catch (NumberFormatException e) {
                    Logger.getLogger(ProprietarioServlet.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        } else if ("/proprietario/menu/elimina".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    prodottoDAO.delete(id);
                } catch (NumberFormatException e) {
                    Logger.getLogger(ProprietarioServlet.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/proprietario");
    }
}