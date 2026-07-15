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
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        if (!"Proprietario".equalsIgnoreCase(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso riservato al proprietario.");
            return;
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("prodotti", prodottoDAO.getAll());
        dataModel.put("staff", utenteDAO.getStaffList());
        dataModel.put("ordini", ordineDAO.getAll());

        renderTemplate("proprietario.ftl", dataModel, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        if (!"Proprietario".equalsIgnoreCase(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso riservato al proprietario.");
            return;
        }

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
                    e.printStackTrace();
                }
            }
        } else if ("/proprietario/menu/elimina".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    prodottoDAO.delete(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/proprietario");
    }
}
