package it.univaq.webdelivery.util;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * controllo lato server dell'orario di consegna che gia' viene calcolato nel javascript
 */

public final class OrarioConsegna {

    private static final LocalTime[][] FASCE = {
            { LocalTime.of(12, 0), LocalTime.of(15, 0) },
            { LocalTime.of(19, 0), LocalTime.of(23, 0) }
    };

    private OrarioConsegna() {
        // solo metodi statici
    }

    // Calcola il primo orario di consegna valido a partire da adesso, per
    // precompilare il form di checkout anche quando JS non è disponibile.
    public static LocalDateTime primoSlotDisponibile(int tempoConsegnaStimatoMinuti) {
        LocalDateTime now = LocalDateTime.now();
        for (int giorno = 0; giorno < 2; giorno++) {
            java.time.LocalDate data = now.toLocalDate().plusDays(giorno);
            for (LocalTime[] fascia : FASCE) {
                LocalDateTime apertura = data.atTime(fascia[0]);
                LocalDateTime chiusura = data.atTime(fascia[1]);
                LocalDateTime inizioPrep = now.isAfter(apertura) ? now : apertura;
                LocalDateTime candidato = inizioPrep.plusMinutes(tempoConsegnaStimatoMinuti);
                if (!candidato.isAfter(chiusura)) {
                    return candidato;
                }
            }
        }
        // fallback estremo: apertura di domani + tempo stimato
        return now.toLocalDate().plusDays(1).atTime(FASCE[0][0]).plusMinutes(tempoConsegnaStimatoMinuti);
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

                // orarioRichiesto non può essere precedente a orarioMinimoConsentito
                if (!orarioRichiesto.isBefore(orarioMinimoConsentito)) {
                    return true;
                }
            }
        }
        return false;
    }
}