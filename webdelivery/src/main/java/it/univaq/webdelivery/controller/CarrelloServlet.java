package it.univaq.webdelivery.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Caratteristica;
import it.univaq.webdelivery.data.model.GruppoCaratteristica;
import it.univaq.webdelivery.data.model.Prodotto;
import it.univaq.webdelivery.data.model.RigaOrdine;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/** Gestisce il carrello lato server (sessione),
 * JS disattivato: aggiungere/rimuovere un articolo funziona comunque tramite <form>
 * 
 * JS attivo: menu.js intercetta gli stessi endpoint via fetch per
 * offrire l'esperienza "arricchita" (modale, drawer animato, ecc)
 */
@WebServlet(name = "CarrelloServlet", urlPatterns = { "/carrello", "/carrello/aggiungi", "/carrello/rimuovi" })
public class CarrelloServlet extends BaseServlet {
    public static final String SESSION_KEY = "carrello";

    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.prodottoDAO = new ProdottoDAO();
    }

    @SuppressWarnings("unchecked")
    public static List<RigaOrdine> getCarrello(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        List<RigaOrdine> carrello = (List<RigaOrdine>) session.getAttribute(SESSION_KEY);
        if (carrello == null) {
            carrello = new ArrayList<>();
            session.setAttribute(SESSION_KEY, carrello);
        }
        return carrello;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<RigaOrdine> carrello = getCarrello(request);

        double totale = 0;
        int tempoPreparazione = 0;
        for (RigaOrdine r : carrello) {
            totale += r.getPrezzoBaseAlMomento() * r.getQty();
        }
        for (RigaOrdine r : carrello) {
            Prodotto p = prodottoDAO.getById(r.getProdottoId());
            if (p != null) tempoPreparazione += p.getTempoPreparazione() * r.getQty();
        }

        // Le richieste fatte via fetch da menu.js chiedono i dati in JSON per
        // poter aggiornare il drawer senza ricaricare la pagina.
        if ("fetch".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setContentType("application/json;charset=UTF-8");
            StringBuilder json = new StringBuilder("{\"totale\":").append(totale)
                    .append(",\"tempoPreparazione\":").append(tempoPreparazione)
                    .append(",\"righe\":[");
            for (int i = 0; i < carrello.size(); i++) {
                RigaOrdine r = carrello.get(i);
                if (i > 0) json.append(",");
                json.append("{\"indice\":").append(i)
                        .append(",\"nome\":\"").append(escapeJson(r.getNome())).append("\"")
                        .append(",\"qty\":").append(r.getQty())
                        .append(",\"prezzoUnitario\":").append(r.getPrezzoBaseAlMomento())
                        .append(",\"opzioni\":\"").append(escapeJson(String.join(", ", r.getOpzioni()))).append("\"")
                        .append("}");
            }
            json.append("]}");
            response.getWriter().write(json.toString());
            return;
        }

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("carrello", carrello);
        dataModel.put("totale", totale);
        dataModel.put("tempoPreparazione", tempoPreparazione);

        renderTemplate("carrello.ftl", dataModel, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        List<RigaOrdine> carrello = getCarrello(request);

        if ("/carrello/aggiungi".equals(path)) {
            aggiungi(request, carrello);
        } else if ("/carrello/rimuovi".equals(path)) {
            rimuovi(request, carrello);
        }

        // Se la richiesta arriva via fetch/JS, rispondiamo senza redirect così lo
        // script può aggiornare il DOM senza ricaricare la pagina.
        if ("fetch".equalsIgnoreCase(request.getHeader("X-Requested-With"))) {
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        // Percorso "senza JS": torniamo semplicemente alla pagina da cui si è
        // partiti (o al menu se non disponibile).
        String referer = request.getHeader("Referer");
        response.sendRedirect(referer != null ? referer : request.getContextPath() + "/menu");
    }

    private void aggiungi(HttpServletRequest request, List<RigaOrdine> carrello) {
        int prodottoId = getIntParameter(request, "prodotto_id", -1);
        int qty = Math.max(1, getIntParameter(request, "qty", 1));
        if (prodottoId == -1) return;

        Prodotto p = prodottoDAO.getById(prodottoId);
        if (p == null) return;

        RigaOrdine riga = new RigaOrdine();
        riga.setProdottoId(p.getId());
        riga.setNome(p.getNome());
        riga.setQty(qty);

        double prezzoUnitario = p.getPrezzoBase();
        List<String> nomiScelti = new ArrayList<>();

        for (GruppoCaratteristica gruppo : p.getGruppi()) {
            // Caratteristiche scelte esplicitamente dal form (checkbox/radio "car_<idGruppo>")
            String[] selezionate = request.getParameterValues("car_" + gruppo.getId());

            if (selezionate != null) {
                for (String idStr : selezionate) {
                    Caratteristica c = trovaCaratteristica(gruppo, idStr);
                    if (c != null) {
                        prezzoUnitario += c.getDifferenzaPrezzo();
                        nomiScelti.add(c.getNome());
                        riga.getCaratteristicheScelte()
                                .add(new RigaOrdine.CaratteristicaScelta(c.getId(), c.getDifferenzaPrezzo()));
                    }
                }
            } else {
                // Fallback senza JS/senza selezione esplicita: usa le opzioni di default
                for (Caratteristica c : gruppo.getOpzioni()) {
                    if (c.isDefault()) {
                        prezzoUnitario += c.getDifferenzaPrezzo();
                        nomiScelti.add(c.getNome());
                        riga.getCaratteristicheScelte()
                                .add(new RigaOrdine.CaratteristicaScelta(c.getId(), c.getDifferenzaPrezzo()));
                    }
                }
            }
        }

        riga.setPrezzoBaseAlMomento(prezzoUnitario);
        riga.setOpzioni(nomiScelti);
        carrello.add(riga);
    }

    private void rimuovi(HttpServletRequest request, List<RigaOrdine> carrello) {
        int indice = getIntParameter(request, "indice", -1);
        if (indice >= 0 && indice < carrello.size()) {
            carrello.remove(indice);
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private Caratteristica trovaCaratteristica(GruppoCaratteristica gruppo, String idStr) {
        try {
            int id = Integer.parseInt(idStr);
            for (Caratteristica c : gruppo.getOpzioni()) {
                if (c.getId() == id) return c;
            }
        } catch (NumberFormatException ignored) {}
        return null;
    }
}
