package it.univaq.webdelivery.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.dao.UtenteDAO;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Prodotto;
import it.univaq.webdelivery.data.model.RigaOrdine;
import it.univaq.webdelivery.data.model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "ProprietarioServlet", urlPatterns = {
    "/proprietario", 
    "/proprietario/personale/aggiungi", 
    "/proprietario/personale/rimuovi",
    "/proprietario/menu/elimina",
    "/proprietario/menu/aggiungi",
    "/proprietario/menu/modifica",
    "/proprietario/menu/ripristina"
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
        // controllo sessione
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Proprietario")) return;

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
            LocalDateTime orario = o.getOrarioRichiesto(); 
        
            if (orario != null) {
                LocalDate oDate = orario.toLocalDate();
                
                // calcolo incasso mensile 
                if (oDate.getYear() == targetDate.getYear() && oDate.getMonth() == targetDate.getMonth()) {
                    incassoMese = incassoMese.add(BigDecimal.valueOf(o.getPrezzoTotale()));
                }

                // calcolo incasso giornaliero
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
        
        // passa statistiche al modello
        dataModel.put("statDate", targetDate.toString());
        dataModel.put("incassoGiorno", incassoGiorno);
        dataModel.put("incassoMese", incassoMese);
        dataModel.put("ordiniOggi", ordiniOggi);
        dataModel.put("topProdotti", topProdotti);
        dataModel.put("flopProdotti", flopProdotti);
        dataModel.put("prodottiInattivi", prodottoDAO.getInactive());

        renderTemplate("proprietario.ftl", dataModel, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // controllo sessione
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        if (!verificaRuoli(utente, response, "Proprietario")) return;

        String path = request.getServletPath();

        if ("/proprietario/personale/aggiungi".equals(path)) {
            String nome = request.getParameter("nome");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            if (nome != null && email != null && password != null && password.length() >= 8) {
                Utente u = new Utente();
                u.setNome(nome);
                u.setEmail(email);
                u.setPasswordHash(it.univaq.webdelivery.util.PasswordUtil.hash(password));
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
                    Logger.getLogger(ProprietarioServlet.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        } else if ("/proprietario/menu/elimina".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                try {
                    int id = Integer.parseInt(idStr);
                    prodottoDAO.delete(id);
                } catch (NumberFormatException e) {
                    Logger.getLogger(ProprietarioServlet.class.getName()).log(Level.SEVERE, "Errore durante l'accesso al database", e);
                }
            }
        } else if ("/proprietario/menu/ripristina".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                prodottoDAO.restore(Integer.parseInt(idStr));
            }
        } else if ("/proprietario/menu/aggiungi".equals(path)) {
            Prodotto p = new Prodotto();
            p.setNome(request.getParameter("nome"));
            p.setPrezzoBase(Double.parseDouble(request.getParameter("prezzo_base")));
            p.setTempoPreparazione(Integer.parseInt(request.getParameter("tempo_preparazione")));
            p.setDescrizione(request.getParameter("descrizione"));
            
            // se l'admin lascia la categoria vuoto mette "Altro" di default
            String cat = request.getParameter("categoria");
            if (cat == null || cat.trim().isEmpty()) {
                p.setCategoria("Altro");
            } else {
                p.setCategoria(cat.trim());
            }
            
            prodottoDAO.create(p);
        } else if ("/proprietario/menu/modifica".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                Prodotto p = prodottoDAO.getById(Integer.parseInt(idStr));
                if (p != null) {
                    p.setNome(request.getParameter("nome"));
                    p.setPrezzoBase(Double.parseDouble(request.getParameter("prezzo_base")));
                    p.setTempoPreparazione(Integer.parseInt(request.getParameter("tempo_preparazione")));
                    p.setDescrizione(request.getParameter("descrizione"));
                    
                    // se l'admin lascia la categoria vuoto mette "Altro" di default
                    String cat = request.getParameter("categoria");
                    if (cat == null || cat.trim().isEmpty()) {
                        p.setCategoria("Altro");
                    } else {
                        p.setCategoria(cat.trim());
                    }
                    
                    prodottoDAO.update(p);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/proprietario");
    }
}