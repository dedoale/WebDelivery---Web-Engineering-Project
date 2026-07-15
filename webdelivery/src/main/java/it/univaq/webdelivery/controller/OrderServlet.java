package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
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

@WebServlet(name = "OrderServlet", urlPatterns = {"/ordini"})
public class OrderServlet extends BaseServlet {
    private OrdineDAO ordineDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDAO = new OrdineDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        List<Ordine> ordini = ordineDAO.getOrdiniCliente(utente.getId());

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("ordini", ordini);

        renderTemplate("storico-ordini.ftl", dataModel, response);
    }
}
