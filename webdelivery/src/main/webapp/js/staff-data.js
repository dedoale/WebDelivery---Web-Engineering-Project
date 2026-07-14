/*
 * dati mock per ordini correnti per testing

 * una volta implementate le servlet java + freemarker i dati arriveranno direttamente dal server 
 */

const ORDINI_CORRENTI = [
  {
    id: "WD-482913",
    cliente: "Mario Rossi",
    orario_richiesto: "2026-07-14T20:15:00",
    stato_attuale: "Inserito",
    righe: [
      { prodotto_id: 3, qty: 1, opzioni: ["Impasto integrale"] },
      { prodotto_id: 4, qty: 1, opzioni: ["Extra piccante"] },
      { prodotto_id: 5, qty: 2, opzioni: [] }
    ],
    storico: []
  },
  {
    id: "WD-482920",
    cliente: "Giulia Bianchi",
    orario_richiesto: "2026-07-14T20:00:00",
    stato_attuale: "In preparazione",
    righe: [
      { prodotto_id: 2, qty: 1, opzioni: ["Abbondante"] },
      { prodotto_id: 1, qty: 1, opzioni: [] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-07-14T19:20:00" },
      { stato: "In preparazione", personale: "Luca B.", timestamp: "2026-07-14T19:30:00" }
    ]
  },
  {
    id: "WD-482905",
    cliente: "Andrea Verdi",
    orario_richiesto: "2026-07-14T19:45:00",
    stato_attuale: "Pronto",
    righe: [
      { prodotto_id: 3, qty: 2, opzioni: [] },
      { prodotto_id: 6, qty: 2, opzioni: ["Zuccherato"] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-07-14T19:05:00" },
      { stato: "In preparazione", personale: "Sara M.", timestamp: "2026-07-14T19:10:00" },
      { stato: "Pronto", personale: "Sara M.", timestamp: "2026-07-14T19:24:00" }
    ]
  },
  {
    id: "WD-482899",
    cliente: "Elena Conti",
    orario_richiesto: "2026-07-14T19:30:00",
    stato_attuale: "In consegna",
    righe: [
      { prodotto_id: 4, qty: 1, opzioni: [] },
      { prodotto_id: 5, qty: 1, opzioni: [] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-07-14T18:50:00" },
      { stato: "In preparazione", personale: "Luca B.", timestamp: "2026-07-14T18:55:00" },
      { stato: "Pronto", personale: "Luca B.", timestamp: "2026-07-14T19:08:00" },
      { stato: "In consegna", personale: "Marco T.", timestamp: "2026-07-14T19:12:00" }
    ]
  }
];

// membro del personale loggato
const PERSONALE_CORRENTE = "Luca B.";
