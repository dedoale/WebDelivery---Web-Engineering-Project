package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.UtenteDAO;
import it.univaq.webdelivery.data.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/registrazione", "/logout"})
public class AuthServlet extends BaseServlet {
    private UtenteDAO utenteDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDAO = new UtenteDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }

        Map<String, Object> dataModel = new HashMap<>();
        
        // Passa l'eventuale utente loggato in sessione
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            dataModel.put("utente", session.getAttribute("utente"));
        }

        if ("/login".equals(path)) {
            renderTemplate("login.ftl", dataModel, response);
        } else if ("/registrazione".equals(path)) {
            renderTemplate("registrazione.ftl", dataModel, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        Map<String, Object> dataModel = new HashMap<>();

        if ("/login".equals(path)) {
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            Utente utente = utenteDAO.authenticate(email, password);
            if (utente != null) {
                HttpSession session = request.getSession(true);
                session.setAttribute("utente", utente);

                // Reindirizza in base al ruolo dell'utente
                if ("Proprietario".equalsIgnoreCase(utente.getRuolo())) {
                    response.sendRedirect(request.getContextPath() + "/proprietario");
                } else if ("Personale".equalsIgnoreCase(utente.getRuolo())) {
                    response.sendRedirect(request.getContextPath() + "/personale");
                } else {
                    response.sendRedirect(request.getContextPath() + "/");
                }
            } else {
                dataModel.put("error", "Email o password non valide.");
                renderTemplate("login.ftl", dataModel, response);
            }
        } else if ("/registrazione".equals(path)) {
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String telefono = request.getParameter("telefono");
            String indirizzo = request.getParameter("indirizzo");

            Utente u = new Utente();
            u.setNome(nome);
            u.setEmail(email);
            u.setPasswordHash(password); // In chiaro per i test iniziali
            u.setTelefono(telefono);
            u.setIndirizzo(indirizzo);
            u.setRuolo("Cliente");

            if (utenteDAO.registerCliente(u)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("utente", u);
                response.sendRedirect(request.getContextPath() + "/");
            } else {
                dataModel.put("error", "Errore durante la registrazione. Email già registrata.");
                renderTemplate("registrazione.ftl", dataModel, response);
            }
        }
    }
}
