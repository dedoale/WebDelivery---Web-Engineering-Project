package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "StaffServlet", urlPatterns = {"/personale", "/personale/aggiorna-stato"})
public class StaffServlet extends BaseServlet {
    private OrdineDAO ordineDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDAO = new OrdineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // verifica autenticazione e ruolo
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Personale", "Proprietario")) return;

        List<Ordine> ordiniCorrenti = ordineDAO.getOrdiniCorrenti();

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("ordiniCorrenti", ordiniCorrenti);

        renderTemplate("personale.ftl", dataModel, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // verifica autenticazione e ruolo
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Personale", "Proprietario")) return;

        // aggiornamento stato dell'ordine 
        String path = request.getServletPath();
        if ("/personale/aggiorna-stato".equals(path)) {
            int idOrdine = getIntParameter(request, "ordine_id", -1);
            String nuovoStato = request.getParameter("stato");

            if (idOrdine != -1 && nuovoStato != null) {
                // esegue l'aggiornamento e controlla se è andato a buon fine
                boolean successo = ordineDAO.updateStato(idOrdine, nuovoStato, utente.getId());
                
                if (!successo) {
                    System.out.println("Aggiornamento dello stato fallito per l'ordine #" + idOrdine);
                }
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/personale");
        
        response.sendRedirect(request.getContextPath() + "/personale");
    }
}