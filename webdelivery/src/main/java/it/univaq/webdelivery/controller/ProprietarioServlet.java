package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.dao.UtenteDAO;

import it.univaq.webdelivery.data.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import it.univaq.webdelivery.data.model.RigaOrdine;
import it.univaq.webdelivery.data.model.Ordine;
import java.time.LocalDateTime;

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

        List<Ordine> ordini = ordineDAO.getAll();

        String statDateParam = request.getParameter("statDate");
        LocalDate targetDate = LocalDate.of(2026, 7, 14); // Default mock date
        if (statDateParam != null && !statDateParam.isEmpty()) {
            try {
                targetDate = LocalDate.parse(statDateParam);
            } catch (DateTimeParseException ignored) {}
        }

        BigDecimal incassoGiorno = BigDecimal.ZERO;
        BigDecimal incassoMese = BigDecimal.ZERO;
        int ordiniOggi = 0;
        Map<String, Integer> freqProdotti = new HashMap<>();

        for (Ordine o : ordini) {
            if (o.getOrarioRichiesto() != null) {
                String orarioStr = o.getOrarioRichiesto().replace(" ", "T");
                LocalDate oDate;
                try {
                    oDate = LocalDateTime.parse(orarioStr).toLocalDate();
                } catch (DateTimeParseException e) {
                    continue;
                }
                
                if (oDate.getYear() == targetDate.getYear() && oDate.getMonth() == targetDate.getMonth()) {
                    incassoMese = incassoMese.add(BigDecimal.valueOf(o.getPrezzoTotale()));
                }

                if (oDate.equals(targetDate)) {
                    incassoGiorno = incassoGiorno.add(BigDecimal.valueOf(o.getPrezzoTotale()));
                    ordiniOggi++;

                    for (RigaOrdine r : o.getRighe()) {
                        String pNome = r.getNome();
                        if (pNome == null || pNome.isEmpty()) {
                            pNome = "Prodotto #" + r.getProdottoId();
                        }
                        freqProdotti.put(pNome, freqProdotti.getOrDefault(pNome, 0) + r.getQty());
                    }
                }
            }
        }

        List<Map.Entry<String, Integer>> listFrequenze = new ArrayList<>(freqProdotti.entrySet());
        listFrequenze.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<Map.Entry<String, Integer>> topProdotti = listFrequenze.stream().limit(3).collect(Collectors.toList());
        List<Map.Entry<String, Integer>> flopProdotti = new ArrayList<>();
        if (!listFrequenze.isEmpty()) {
            int flopSize = Math.min(3, listFrequenze.size());
            for (int i = listFrequenze.size() - 1; i >= listFrequenze.size() - flopSize; i--) {
                flopProdotti.add(listFrequenze.get(i));
            }
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("prodotti", prodottoDAO.getAll());
        dataModel.put("staff", utenteDAO.getStaffList());
        dataModel.put("ordini", ordini);
        
        // Pass statistics to model
        dataModel.put("statDate", targetDate.toString());
        dataModel.put("incassoGiorno", incassoGiorno);
        dataModel.put("incassoMese", incassoMese);
        dataModel.put("ordiniOggi", ordiniOggi);
        dataModel.put("topProdotti", topProdotti);
        dataModel.put("flopProdotti", flopProdotti);

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
