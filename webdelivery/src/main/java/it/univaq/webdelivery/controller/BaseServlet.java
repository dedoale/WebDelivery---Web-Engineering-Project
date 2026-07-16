package it.univaq.webdelivery.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import it.univaq.webdelivery.data.DbConnectionManager;
import it.univaq.webdelivery.data.model.Utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import no.api.freemarker.java8.Java8ObjectWrapper;

public abstract class BaseServlet extends HttpServlet {
    protected Configuration freemarkerConfig;

    @Override
    public void init() throws ServletException {
        super.init();
        
        // Inizializza la configurazione di FreeMarker
        freemarkerConfig = new Configuration(Configuration.VERSION_2_3_32);
        freemarkerConfig.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
        freemarkerConfig.setDefaultEncoding("UTF-8");
        freemarkerConfig.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);

        // Aggiunge il supporto nativo per java.time.LocalDateTime e LocalDate in Freemarker
        Java8ObjectWrapper ow = new Java8ObjectWrapper(Configuration.VERSION_2_3_32);
        ow.setDefaultDateType(freemarker.template.TemplateDateModel.DATETIME);
        ow.setForceLegacyNonListCollections(false);
        freemarkerConfig.setObjectWrapper(ow);
        
        // Inizializza il DbConnectionManager leggendo le credenziali da web.xml
        synchronized (DbConnectionManager.class) {
            try {
                // Se la connessione funziona, il manager è già inizializzato
                DbConnectionManager.getConnection().close();
            } catch (Exception e) {
                // Altrimenti, inizializziamo con i parametri del contesto web
                String url = getServletContext().getInitParameter("db.url");
                String user = getServletContext().getInitParameter("db.username");
                String pass = getServletContext().getInitParameter("db.password");
                
                // Fallback di cortesia per facilitare i test dell'utente
                if (url == null) {
                    url = "jdbc:mysql://localhost:3306/webdelivery?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
                }
                if (user == null) {
                    user = "root";
                }
                if (pass == null) {
                    pass = "";
                }
                
                try {
                    DbConnectionManager.initialize(url, user, pass);
                } catch (ClassNotFoundException ex) {
                    throw new ServletException("Impossibile caricare il driver JDBC di MySQL", ex);
                }
            }
        }
    }

    protected void renderTemplate(String templateName, Map<String, Object> dataModel, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // inietta l'utente automaticamente se presente
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            dataModel.put("utente", session.getAttribute("utente"));
        }

        // rendering
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new IOException("Errore durante l'elaborazione del template: " + templateName, e);
        }
    }

    
    // Verifica se c'è un utente loggato. Altrimenti reindirizza al login
    protected Utente richiediAutenticazione(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("utente") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }
        return (Utente) session.getAttribute("utente");
    }

    // Verifica se l'utente possiede uno dei ruoli ammessi
    protected boolean verificaRuoli(Utente utente, HttpServletResponse response, String... ruoliAmmessi) throws IOException {
        if (utente == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato.");
            return false;
        }
        for (String ruolo : ruoliAmmessi) {
            if (ruolo.equalsIgnoreCase(utente.getRuolo())) {
                return true;
            }
        }
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato. Ruolo non autorizzato.");
        return false;
    }

    // Utility per fare il parsing sicuro dei parametri numerici (con default in caso di errore)
    protected int getIntParameter(HttpServletRequest request, String name, int defaultValue) {
        String value = request.getParameter(name);
        if (value == null || value.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}