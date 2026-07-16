package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Ordine;
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

@WebServlet(name = "StaffServlet", urlPatterns = {"/personale", "/personale/aggiorna-stato"})
public class StaffServlet extends BaseServlet {
    private OrdineDAO ordineDAO;
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDAO = new OrdineDAO();
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
        if (!"Personale".equalsIgnoreCase(utente.getRuolo()) && !"Proprietario".equalsIgnoreCase(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            return;
        }

        List<Ordine> ordiniCorrenti = ordineDAO.getOrdiniCorrenti();

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("ordiniCorrenti", ordiniCorrenti);
        dataModel.put("prodotti", prodottoDAO.getAll());

        renderTemplate("personale.ftl", dataModel, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        if (!"Personale".equalsIgnoreCase(utente.getRuolo()) && !"Proprietario".equalsIgnoreCase(utente.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso non autorizzato.");
            return;
        }

        String path = request.getServletPath();
        if ("/personale/aggiorna-stato".equals(path)) {
            String ordineIdStr = request.getParameter("ordine_id");
            String nuovoStato = request.getParameter("stato");

            if (ordineIdStr != null && nuovoStato != null) {
                try {
                    int idOrdine = Integer.parseInt(ordineIdStr);
                    ordineDAO.updateStato(idOrdine, nuovoStato, utente.getId());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/personale");
    }
}
