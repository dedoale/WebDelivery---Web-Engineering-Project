package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Prodotto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "MenuServlet", urlPatterns = {"", "/menu", "/index.html"})
public class MenuServlet extends BaseServlet {
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.prodottoDAO = new ProdottoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Prodotto> prodotti = prodottoDAO.getAll();
        
        // Estrai categorie distinte dai prodotti caricati
        List<String> categorie = new ArrayList<>();
        for (Prodotto p : prodotti) {
            if (!categorie.contains(p.getCategoria())) {
                categorie.add(p.getCategoria());
            }
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("prodotti", prodotti);
        dataModel.put("categorie", categorie);

        // Aggiungi utente in sessione se presente
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            dataModel.put("utente", session.getAttribute("utente"));
        }

        renderTemplate("index.ftl", dataModel, response);
    }
}
