package it.univaq.webdelivery.util;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.univaq.webdelivery.data.model.Ordine;
import it.univaq.webdelivery.data.model.Utente;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailUtil {

    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class.getName());
    // Esecutore per inviare email in un thread separato in modo asincrono, così non
    // blocca l'utente
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    private static Session getSession() {
        Properties prop = new Properties();
        // Usiamo un server SMTP locale di test sulla porta 2525 (es. FakeSMTP)
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "2525");
        prop.put("mail.smtp.auth", "false");

        return Session.getInstance(prop);
    }

    public static void sendOrderConfirmation(Utente cliente, Ordine ordine) {
        executor.submit(() -> {
            try {
                Session session = getSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("ordini@univaqfooddelivery.it", "Univaq Food Delivery"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cliente.getEmail()));
                message.setSubject("Riepilogo Ordine #" + ordine.getId());

                String text = "Ciao " + cliente.getNome() + ",\n\n" +
                        "Grazie per aver ordinato da Univaq Food Delivery!\n\n" +
                        "Dettagli Ordine:\n" +
                        "- Numero Ordine: " + ordine.getId() + "\n" +
                        "- Totale: €" + String.format("%.2f", ordine.getPrezzoTotale()) + "\n" +
                        "- Orario Consegna Richiesto: " + ordine.getOrarioRichiesto().toString() + "\n\n" +
                        "Ti manderemo un'altra email non appena l'ordine sarà in consegna.\n\n" +
                        "Il Team di UFD";

                message.setText(text);
                Transport.send(message);
                LOGGER.info(() -> "Email di conferma inviata con successo a " + cliente.getEmail());

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore nell'invio dell'email di conferma a " + cliente.getEmail(), e);
            }
        });
    }

    public static void sendOrderInDelivery(Utente cliente, Ordine ordine) {
        executor.submit(() -> {
            try {
                Session session = getSession();
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("ordini@univaqfooddelivery.it", "Univaq Food Delivery"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cliente.getEmail()));
                message.setSubject("Il tuo ordine #" + ordine.getId() + " è in consegna!");

                String text = "Ciao " + cliente.getNome() + ",\n\n" +
                        "Il tuo ordine #" + ordine.getId()
                        + " è appena uscito dalla nostra cucina ed è in consegna verso il tuo indirizzo!\n\n" +
                        "Preparati ad aprire la porta!\n\n" +
                        "Buon appetito,\n" +
                        "Il Team di UFD";

                message.setText(text);
                Transport.send(message);
                LOGGER.info(() -> "Email di notifica in consegna inviata con successo a " + cliente.getEmail());

            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Errore nell'invio dell'email 'in consegna' a " + cliente.getEmail(), e);
            }
        });
    }
}