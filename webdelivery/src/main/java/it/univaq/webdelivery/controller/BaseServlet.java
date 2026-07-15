package it.univaq.webdelivery.controller;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import it.univaq.webdelivery.data.DbConnectionManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

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

    protected void renderTemplate(String templateName, Map<String, Object> dataModel, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            template.process(dataModel, response.getWriter());
        } catch (TemplateException e) {
            throw new IOException("Errore durante l'elaborazione del template FreeMarker: " + templateName, e);
        }
    }
}
