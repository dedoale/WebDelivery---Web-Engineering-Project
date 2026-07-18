package it.univaq.webdelivery.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.univaq.webdelivery.data.dao.OrdineDAO;
import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Prodotto;
import it.univaq.webdelivery.data.model.RigaOrdine;
import it.univaq.webdelivery.data.model.Utente;
import it.univaq.webdelivery.util.OrarioConsegna;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "CheckoutServlet", urlPatterns = { "/checkout" })
public class CheckoutServlet extends BaseServlet {
    private static final int TEMPO_PERCORRENZA_MIN = 10;

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
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        List<RigaOrdine> carrello = CarrelloServlet.getCarrello(request);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("carrello", carrello);

        if (!carrello.isEmpty()) {
            Riepilogo r = calcolaRiepilogo(carrello);
            dataModel.put("totale", r.totale);
            dataModel.put("tempoPreparazione", r.tempoPreparazione);
            dataModel.put("tempoConsegnaStimato", r.tempoPreparazione + TEMPO_PERCORRENZA_MIN);
            dataModel.put("tempoPercorrenza", TEMPO_PERCORRENZA_MIN);

            LocalDateTime primoSlot = OrarioConsegna.primoSlotDisponibile(r.tempoPreparazione + TEMPO_PERCORRENZA_MIN);
            dataModel.put("orarioDefault", primoSlot);
        }

        renderTemplate("checkout.ftl", dataModel, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Utente utente = richiediAutenticazione(request, response);
        if (utente == null) return;

        List<RigaOrdine> carrello = CarrelloServlet.getCarrello(request);
        if (carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/menu");
            return;
        }

        String orario = request.getParameter("orario");

        // Ricalcola prezzi/tempi leggendo dal DB per sicurezza (non ci fidiamo
        // di quanto già calcolato quando le righe sono state aggiunte al carrello)
        Riepilogo r = calcolaRiepilogo(carrello);
        int tempoConsegnaStimato = r.tempoPreparazione + TEMPO_PERCORRENZA_MIN;

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("utente", utente);
        dataModel.put("carrello", carrello);
        dataModel.put("totale", r.totale);
        dataModel.put("tempoPreparazione", r.tempoPreparazione);
        dataModel.put("tempoConsegnaStimato", tempoConsegnaStimato);
        dataModel.put("tempoPercorrenza", TEMPO_PERCORRENZA_MIN);

        // Se l'orario non è indicato (campo opzionale), usiamo il primo slot disponibile
        LocalDateTime orarioRichiestoParsed;
        if (orario == null || orario.trim().isEmpty()) {
            orarioRichiestoParsed = OrarioConsegna.primoSlotDisponibile(tempoConsegnaStimato);
        } else {
            try {
                orarioRichiestoParsed = LocalDateTime.parse(orario);
            } catch (Exception e) {
                dataModel.put("error", "Formato orario non valido.");
                dataModel.put("orarioDefault", OrarioConsegna.primoSlotDisponibile(tempoConsegnaStimato));
                renderTemplate("checkout.ftl", dataModel, request, response);
                return;
            }
        }

        // Validazione lato server con 5 minuti di tolleranza
        if (!OrarioConsegna.isOrarioValido(orarioRichiestoParsed, Math.max(0, tempoConsegnaStimato - 5))) {
            dataModel.put("error", "L'orario di consegna richiesto non è valido o è fuori dalla fascia oraria. Riprova.");
            dataModel.put("orarioDefault", OrarioConsegna.primoSlotDisponibile(tempoConsegnaStimato));
            renderTemplate("checkout.ftl", dataModel, request, response);
            return;
        }

        Ordine ordine = new Ordine();
        ordine.setIdCliente(utente.getId());
        ordine.setCliente(utente.getNome());
        ordine.setPrezzoTotale(r.totale);
        ordine.setTempoConsegnaStimato(tempoConsegnaStimato);
        ordine.setOrarioRichiesto(orarioRichiestoParsed);
        ordine.setStatoAttuale("Inserito");
        ordine.setRighe(carrello);

        if (ordineDAO.create(ordine)) {
            dataModel.put("ordineConfermato", true);
            dataModel.put("confermatoOrdine", ordine);
            it.univaq.webdelivery.util.EmailUtil.sendOrderConfirmation(utente, ordine);

            // L'ordine è salvato: svuotiamo il carrello in sessione
            carrello.clear();
        } else {
            dataModel.put("error", "Si è verificato un errore durante il salvataggio dell'ordine. Riprova.");
        }

        renderTemplate("checkout.ftl", dataModel, request, response);
    }

    private Riepilogo calcolaRiepilogo(List<RigaOrdine> carrello) {
        double totale = 0;
        int tempoPreparazione = 0;

        for (RigaOrdine riga : carrello) {
            Prodotto p = prodottoDAO.getById(riga.getProdottoId());
            if (p == null) continue;

            riga.setNome(p.getNome());
            double prezzoRigaUnitario = p.getPrezzoBase();
            for (RigaOrdine.CaratteristicaScelta scelta : riga.getCaratteristicheScelte()) {
                prezzoRigaUnitario += getDifferenzaPrezzoCaratteristica(p, scelta.getIdCaratteristica());
            }
            riga.setPrezzoBaseAlMomento(prezzoRigaUnitario);

            totale += prezzoRigaUnitario * riga.getQty();
            tempoPreparazione += p.getTempoPreparazione() * riga.getQty();
        }

        Riepilogo r = new Riepilogo();
        r.totale = totale;
        r.tempoPreparazione = tempoPreparazione;
        return r;
    }

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

    private static class Riepilogo {
        double totale;
        int tempoPreparazione;
    }
}
