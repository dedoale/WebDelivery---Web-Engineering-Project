package it.univaq.webdelivery.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.univaq.webdelivery.data.dao.ProdottoDAO;
import it.univaq.webdelivery.data.model.Caratteristica;
import it.univaq.webdelivery.data.model.GruppoCaratteristica;
import it.univaq.webdelivery.data.model.Prodotto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Espone il catalogo prodotti in JSON

@WebServlet(name = "MenuDataServlet", urlPatterns = {"/api/prodotti"})
public class MenuDataServlet extends BaseServlet {
    private ProdottoDAO prodottoDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        this.prodottoDAO = new ProdottoDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Prodotto> prodotti = prodottoDAO.getAll();

        List<String> categorie = new ArrayList<>();
        for (Prodotto p : prodotti) {
            if (!categorie.contains(p.getCategoria())) {
                categorie.add(p.getCategoria());
            }
        }

        response.setContentType("application/json;charset=UTF-8");

        StringBuilder json = new StringBuilder();
        json.append("{\"categorie\":[");
        for (int i = 0; i < categorie.size(); i++) {
            if (i > 0) json.append(",");
            json.append(quote(categorie.get(i)));
        }
        json.append("],\"prodotti\":[");
        for (int i = 0; i < prodotti.size(); i++) {
            if (i > 0) json.append(",");
            json.append(prodottoToJson(prodotti.get(i)));
        }
        json.append("]}");

        response.getWriter().write(json.toString());
    }

    private String prodottoToJson(Prodotto p) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(p.getId()).append(",");
        sb.append("\"categoria\":").append(quote(p.getCategoria())).append(",");
        sb.append("\"nome\":").append(quote(p.getNome())).append(",");
        sb.append("\"descrizione\":").append(quote(p.getDescrizione())).append(",");
        sb.append("\"prezzo_base\":").append(p.getPrezzoBase()).append(",");
        sb.append("\"tempo_preparazione\":").append(p.getTempoPreparazione()).append(",");
        sb.append("\"immagine\":").append(p.getImmagine() != null && !p.getImmagine().trim().isEmpty()
                ? quote("img/" + p.getImmagine()) : "null").append(",");
        sb.append("\"procedura\":").append(quote(p.getProcedura())).append(",");

        sb.append("\"ingredienti\":[");
        List<Prodotto.IngredienteConQuantita> ingredienti = p.getIngredienti();
        for (int i = 0; i < ingredienti.size(); i++) {
            if (i > 0) sb.append(",");
            Prodotto.IngredienteConQuantita ing = ingredienti.get(i);
            sb.append("{\"nome\":").append(quote(ing.getNome()))
              .append(",\"quantita\":").append(quote(ing.getQuantita())).append("}");
        }
        sb.append("],");

        sb.append("\"gruppi\":[");
        List<GruppoCaratteristica> gruppi = p.getGruppi();
        for (int i = 0; i < gruppi.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(gruppoToJson(gruppi.get(i)));
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }

    private String gruppoToJson(GruppoCaratteristica g) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(g.getId()).append(",");
        sb.append("\"nome\":").append(quote(g.getNome())).append(",");
        sb.append("\"mutua_esclusione\":").append(g.isMutuaEsclusione()).append(",");
        sb.append("\"opzioni\":[");
        List<Caratteristica> opzioni = g.getOpzioni();
        for (int i = 0; i < opzioni.size(); i++) {
            if (i > 0) sb.append(",");
            Caratteristica o = opzioni.get(i);
            sb.append("{");
            sb.append("\"id\":").append(quote(String.valueOf(o.getId()))).append(",");
            sb.append("\"nome\":").append(quote(o.getNome())).append(",");
            sb.append("\"differenza_prezzo\":").append(o.getDifferenzaPrezzo()).append(",");
            sb.append("\"is_default\":").append(o.isDefault());
            sb.append("}");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }

    // Racchiude una stringa tra virgolette JSON, escapando i caratteri speciali
    private String quote(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append("\"");
        return sb.toString();
    }
}