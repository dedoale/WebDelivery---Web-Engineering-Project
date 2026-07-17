package it.univaq.webdelivery.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

// controllo lato server dell'orario di consegna che gia' viene calcolato nel javascript

public final class OrarioConsegna {

    private static final LocalTime[][] FASCE = {
            { LocalTime.of(12, 0), LocalTime.of(15, 0) },
            { LocalTime.of(19, 0), LocalTime.of(23, 0) }
    };

    private OrarioConsegna() {
        // solo metodi statici
    }

    public static boolean isOrarioValido(LocalDateTime orarioRichiesto, int tempoConsegnaStimatoMinuti) {
        if (orarioRichiesto == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalTime ora = orarioRichiesto.toLocalTime();

        for (LocalTime[] fascia : FASCE) {
            LocalTime apertura = fascia[0];
            LocalTime chiusura = fascia[1];

            // controlla se l'orario richiesto rientra in una delle fasce
            if (!ora.isBefore(apertura) && !ora.isAfter(chiusura)) {

                // calcolo inizio preparazione
                LocalDateTime aperturaDelGiornoRichiesto = orarioRichiesto.toLocalDate().atTime(apertura);
                LocalDateTime inizioPrep = now.isAfter(aperturaDelGiornoRichiesto) ? now : aperturaDelGiornoRichiesto;
                
                // calcolo orario minimo di consegna
                LocalDateTime orarioMinimoConsentito = inizioPrep.plusMinutes(tempoConsegnaStimatoMinuti);

                // 4. orarioRichiesto non può essere precedente a orarioMinimoConsentito
                if (!orarioRichiesto.isBefore(orarioMinimoConsentito)) {
                    return true;
                }
            }
        }
        return false;
    }
}