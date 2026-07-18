package it.univaq.webdelivery.data.dto;

import java.util.ArrayList;
import java.util.List;

import it.univaq.webdelivery.data.model.Caratteristica;
import it.univaq.webdelivery.data.model.GruppoCaratteristica;

/**
 * Rispecchia esattamente l'oggetto "gruppo" che proprietario.js costruisce
 * in serializeForms() prima di mettere JSON.stringify(gruppi) nel campo
 * nascosto caratteristiche_json. Usato solo per il parsing in ingresso:
 * populateGruppi() lo converte poi nell'entity di dominio.
 */
public class GruppoCaratteristicaInputDTO {
    private int id;
    private String nome;
    private boolean mutua_esclusione;
    private List<OpzioneInputDTO> opzioni = new ArrayList<>();

    public static class OpzioneInputDTO {
        private int id;
        private String nome;
        private double diff_prezzo;
        private boolean is_default;

        public Caratteristica toEntity() {
            Caratteristica c = new Caratteristica();
            c.setId(id);
            c.setNome(nome);
            c.setDifferenzaPrezzo(diff_prezzo);
            c.setDefault(is_default);
            return c;
        }
    }

    public GruppoCaratteristica toEntity() {
        GruppoCaratteristica g = new GruppoCaratteristica();
        g.setId(id);
        g.setNome(nome);
        g.setMutuaEsclusione(mutua_esclusione);
        List<Caratteristica> opts = new ArrayList<>();
        for (OpzioneInputDTO opt : opzioni) {
            opts.add(opt.toEntity());
        }
        g.setOpzioni(opts);
        return g;
    }
}