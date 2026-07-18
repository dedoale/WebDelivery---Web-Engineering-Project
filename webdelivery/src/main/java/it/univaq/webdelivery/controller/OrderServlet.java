package it.univaq.webdelivery.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import it.univaq.webdelivery.data.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "OrderServlet", urlPatterns = {"/ordini"})
public class OrderServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // controllo sessione
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        // i dati arrivano a orders.js via fetch da /api/ordini (basato su Gson).
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);

        renderTemplate("storico-ordini.ftl", dataModel, request, response);
    }
}