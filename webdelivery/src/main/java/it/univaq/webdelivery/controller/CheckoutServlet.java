package it.univaq.webdelivery.controller;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Prodotto;
import it.univaq.webdelivery.data.model.RigaOrdine;
import it.univaq.webdelivery.data.model.Utente;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout"})
public class CheckoutServlet extends BaseServlet {
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
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);

        renderTemplate("checkout.ftl", dataModel, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utente");
        String orario = request.getParameter("orario");
        String cartData = request.getParameter("cart_data");

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);

        if (orario == null || cartData == null || cartData.trim().isEmpty()) {
            dataModel.put("error", "Dati dell'ordine non completi.");
            renderTemplate("checkout.ftl", dataModel, response);
            return;
        }

        // 1. Parsifica il carrello JSON inviato dal client
        List<RigaOrdine> righe = parseCartJson(cartData);
        if (righe.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/menu");
            return;
        }

        // 2. Calcola i prezzi e i tempi reali leggendo dal DB per sicurezza
        double totaleOrdine = 0;
        int tempoPreparazioneTotale = 0;

        for (RigaOrdine riga : righe) {
            Prodotto p = prodottoDAO.getById(riga.getProdottoId());
            if (p != null) {
                riga.setNome(p.getNome());
                // Ricalcoliamo il prezzo totale per sicurezza lato server
                double prezzoRigaUnitario = p.getPrezzoBase();
                
                // Cerca le differenze prezzo delle caratteristiche scelte nel DB
                for (RigaOrdine.CaratteristicaScelta scelta : riga.getCaratteristicheScelte()) {
                    // Troviamo il prezzo della caratteristica associata
                    double diff = getDifferenzaPrezzoCaratteristica(p, scelta.getIdCaratteristica());
                    scelta.setDiffPrezzoAlMomento(diff);
                    prezzoRigaUnitario += diff;
                }
                
                riga.setPrezzoBaseAlMomento(prezzoRigaUnitario);
                totaleOrdine += (prezzoRigaUnitario * riga.getQty());
                tempoPreparazioneTotale += (p.getTempoPreparazione() * riga.getQty());
            }
        }

        // Aggiungiamo 10 minuti di percorrenza stimata, come definito in checkout.js
        int tempoConsegnaStimato = tempoPreparazioneTotale + 10;

        // 3. Crea l'oggetto Ordine
        Ordine ordine = new Ordine();
        ordine.setIdCliente(utente.getId());
        ordine.setCliente(utente.getNome());
        ordine.setPrezzoTotale(totaleOrdine);
        ordine.setTempoConsegnaStimato(tempoConsegnaStimato);
        ordine.setOrarioRichiesto(orario);
        ordine.setStatoAttuale("Inserito");
        ordine.setRighe(righe);

        // 4. Salva nel database
        if (ordineDAO.create(ordine)) {
            dataModel.put("ordineConfermato", true);
            dataModel.put("confermatoOrdine", ordine);
        } else {
            dataModel.put("error", "Si è verificato un errore durante il salvataggio dell'ordine. Riprova.");
        }

        renderTemplate("checkout.ftl", dataModel, response);
    }

    // Cerca la differenza di prezzo nel catalogo caricato
    private double getDifferenzaPrezzoCaratteristica(Prodotto p, int idCaratteristica) {
        for (var gruppo : p.getGruppi()) {
            for (var opt : gruppo.getOpzioni()) {
                if (opt.getId() == idCaratteristica) {
                    return opt.getDifferenzaPrezzo();
                }
            }
        }
        return 0.0;
    }

    // Parser JSON personalizzato basato su Regex (Opzione A - Senza Gson)
    private List<RigaOrdine> parseCartJson(String json) {
        List<RigaOrdine> righe = new ArrayList<>();
        if (json == null || !json.contains("{")) return righe;

        List<String> items = splitJsonArray(json);
        for (String item : items) {
            RigaOrdine riga = new RigaOrdine();

            int prodottoId = extractInt(item, "\"prodotto\"\\s*:\\s*\\{\\s*\"id\"\\s*:\\s*(\\d+)");
            int qty = extractInt(item, "\"qty\"\\s*:\\s*(\\d+)");
            double prezzo = extractDouble(item, "\"prezzoUnitario\"\\s*:\\s*([\\d.]+)");

            riga.setProdottoId(prodottoId);
            riga.setQty(qty);
            riga.setPrezzoBaseAlMomento(prezzo);

            String opzioniListStr = extractString(item, "\"opzioniScelte\"\\s*:\\s*\\[(.*?)\\]");
            if (opzioniListStr != null && !opzioniListStr.trim().isEmpty()) {
                Pattern optPattern = Pattern.compile("\\{\\s*\"id\"\\s*:\\s*(\\d+).*?\"differenza_prezzo\"\\s*:\\s*([\\d.-]+)");
                Matcher optMatcher = optPattern.matcher(opzioniListStr);
                while (optMatcher.find()) {
                    int optId = Integer.parseInt(optMatcher.group(1));
                    double diff = Double.parseDouble(optMatcher.group(2));
                    riga.getCaratteristicheScelte().add(new RigaOrdine.CaratteristicaScelta(optId, diff));
                }
            }
            righe.add(riga);
        }
        return righe;
    }

    private List<String> splitJsonArray(String json) {
        List<String> list = new ArrayList<>();
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : json.toCharArray()) {
            if (c == '{') {
                depth++;
            }
            if (depth > 0) {
                sb.append(c);
            }
            if (c == '}') {
                depth--;
                if (depth == 0) {
                    list.add(sb.toString());
                    sb.setLength(0);
                }
            }
        }
        return list;
    }

    private int extractInt(String json, String patternStr) {
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return 0;
    }

    private double extractDouble(String json, String patternStr) {
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0.0;
    }

    private String extractString(String json, String patternStr) {
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
