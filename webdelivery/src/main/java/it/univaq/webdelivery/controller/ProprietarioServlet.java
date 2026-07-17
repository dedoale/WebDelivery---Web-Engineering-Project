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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;
import java.io.File;
import java.nio.file.Paths;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import it.univaq.webdelivery.data.model.Caratteristica;
import it.univaq.webdelivery.data.model.GruppoCaratteristica;

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
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize = 1024 * 1024 * 10,      // 10 MB
    maxRequestSize = 1024 * 1024 * 100   // 100 MB
)
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
            populateProdottoFromRequest(request, p);
            prodottoDAO.create(p);
        } else if ("/proprietario/menu/modifica".equals(path)) {
            String idStr = request.getParameter("prodotto_id");
            if (idStr != null) {
                Prodotto p = prodottoDAO.getById(Integer.parseInt(idStr));
                if (p != null) {
                    populateProdottoFromRequest(request, p);
                    prodottoDAO.update(p);
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/proprietario");
    }

    private void populateProdottoFromRequest(HttpServletRequest request, Prodotto p) throws IOException, ServletException {
        p.setNome(request.getParameter("nome"));
        p.setPrezzoBase(Double.parseDouble(request.getParameter("prezzo_base")));
        p.setTempoPreparazione(Integer.parseInt(request.getParameter("tempo_preparazione")));
        p.setDescrizione(request.getParameter("descrizione"));
        p.setProcedura(request.getParameter("procedura"));
        
        String cat = request.getParameter("categoria");
        if (cat == null || cat.trim().isEmpty()) {
            p.setCategoria("Altro");
        } else {
            p.setCategoria(cat.trim());
        }

        Part filePart = request.getPart("immagine");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String extension = "";
            int i = fileName.lastIndexOf('.');
            if (i > 0) {
                extension = fileName.substring(i);
            }
            String uniqueName = UUID.randomUUID().toString() + extension;
            String uploadPath = getServletContext().getRealPath("") + File.separator + "img";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdir();
            filePart.write(uploadPath + File.separator + uniqueName);
            p.setImmagine(uniqueName);
        }

        String ingrJson = request.getParameter("ingredienti_json");
        if (ingrJson != null && !ingrJson.trim().isEmpty()) {
            p.setIngredienti(parseIngredienti(ingrJson));
        }

        String carattJson = request.getParameter("caratteristiche_json");
        if (carattJson != null && !carattJson.trim().isEmpty()) {
            p.setGruppi(parseCaratteristiche(carattJson));
        }
    }

    private List<Prodotto.IngredienteConQuantita> parseIngredienti(String json) {
        List<Prodotto.IngredienteConQuantita> list = new ArrayList<>();
        Pattern p = Pattern.compile("\\{\\s*\"nome\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"quantita\"\\s*:\\s*\"(.*?)\"\\s*\\}");
        Matcher m = p.matcher(json);
        while (m.find()) {
            list.add(new Prodotto.IngredienteConQuantita(m.group(1), m.group(2)));
        }
        return list;
    }

    private List<GruppoCaratteristica> parseCaratteristiche(String json) {
        List<GruppoCaratteristica> list = new ArrayList<>();
        List<String> groups = splitJsonArray(json);
        for(String grpStr : groups) {
            GruppoCaratteristica g = new GruppoCaratteristica();
            g.setNome(extractString(grpStr, "\"nome\"\\s*:\\s*\"(.*?)\""));
            g.setMutuaEsclusione(grpStr.contains("\"mutua_esclusione\":true") || grpStr.contains("\"mutua_esclusione\" : true"));
            
            String opzioniStr = extractString(grpStr, "\"opzioni\"\\s*:\\s*\\[(.*)\\]");
            if (opzioniStr != null && !opzioniStr.trim().isEmpty()) {
                List<String> opzioni = splitJsonArray("[" + opzioniStr + "]");
                List<Caratteristica> opts = new ArrayList<>();
                for(String optStr : opzioni) {
                    Caratteristica c = new Caratteristica();
                    c.setNome(extractString(optStr, "\"nome\"\\s*:\\s*\"(.*?)\""));
                    String diffPrezzoStr = extractString(optStr, "\"diff_prezzo\"\\s*:\\s*([\\d.-]+)");
                    if (diffPrezzoStr != null) {
                        c.setDifferenzaPrezzo(Double.parseDouble(diffPrezzoStr));
                    }
                    c.setDefault(optStr.contains("\"is_default\":true") || optStr.contains("\"is_default\" : true"));
                    opts.add(c);
                }
                g.setOpzioni(opts);
            }
            list.add(g);
        }
        return list;
    }

    private List<String> splitJsonArray(String json) {
        List<String> list = new ArrayList<>();
        int depth = 0;
        StringBuilder sb = new StringBuilder();
        for (char c : json.toCharArray()) {
            if (c == '{') depth++;
            if (depth > 0) sb.append(c);
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

    private String extractString(String json, String patternStr) {
        Pattern p = Pattern.compile(patternStr);
        Matcher m = p.matcher(json);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}