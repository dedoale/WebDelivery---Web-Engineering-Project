package it.univaq.webdelivery.controller.api;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import it.univaq.webdelivery.controller.BaseServlet;
import it.univaq.webdelivery.data.dao.UtenteDAO;
import it.univaq.webdelivery.data.dto.StaffDTO;
import it.univaq.webdelivery.data.model.Utente;
import it.univaq.webdelivery.util.GsonProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Espone la lista del personale in JSON, 
 * proprietario.js la carica con una fetch
 * 
 * Riservato al proprietario
 */
@WebServlet(name = "StaffDataServlet", urlPatterns = {"/api/personale"})
public class StaffDataServlet extends BaseServlet {
    private UtenteDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDAO = new UtenteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) {
            return;
        }
        if (!verificaRuoli(utente, response, "Proprietario")) {
            return;
        }

        List<StaffDTO> staff = utenteDAO.getStaffList().stream()
                .map(StaffDTO::new)
                .collect(Collectors.toList());

        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(GsonProvider.get().toJson(staff));
    }
}