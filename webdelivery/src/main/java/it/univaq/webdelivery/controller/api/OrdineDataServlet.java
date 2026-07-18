package it.univaq.webdelivery.controller.api;

import java.io.IOException;
import java.util.List;

import it.univaq.webdelivery.controller.BaseServlet;
import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Utente;
import it.univaq.webdelivery.util.GsonProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Espone gli ordini in JSON,
 * orders.js, staff.js, proprietario.js caricano i dati con fetch
 *
 * Endpoint unico: cosa restituisce dipende dal ruolo dell'utente in sessione
 *
 * - Cliente        -> solo i propri ordini (storico-ordini.ftl)
 * - Personale      -> tutti gli ordini correnti, non ancora consegnati (personale.ftl)
 * - Proprietario   -> tutti gli ordini, nessun filtro (proprietario.ftl)
 */
@WebServlet(name = "OrdineDataServlet", urlPatterns = {"/api/ordini"})
public class OrdineDataServlet extends BaseServlet {
    private OrdineDAO ordineDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDAO = new OrdineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) {
            return; // richiediAutenticazione ha gia' fatto il redirect al login
        }

        List<Ordine> ordini;
        switch (utente.getRuolo()) {
            case "Proprietario":
                ordini = ordineDAO.getAll();
                break;
            case "Personale":
                ordini = ordineDAO.getOrdiniCorrenti();
                break;
            case "Cliente":
                ordini = ordineDAO.getOrdiniCliente(utente.getId());
                break;
            default:
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Ruolo non riconosciuto.");
                return;
        }

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(GsonProvider.get().toJson(ordini));
    }
}