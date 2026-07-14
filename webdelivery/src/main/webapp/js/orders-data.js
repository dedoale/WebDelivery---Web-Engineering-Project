/*
 * dati mock per storico ordini per testing

 * una volta implementate le servlet java + freemarker i dati arriveranno direttamente dal server 
 */

const STATI_ORDINE = ["Inserito", "In preparazione", "Pronto", "In consegna", "Consegnato"];

const ORDINI_CLIENTE = [
  {
    id: "WD-482913",
    orario_richiesto: "2026-07-14T20:15:00",
    stato_attuale: "In preparazione",
    prezzo_totale: 24.50,
    righe: [
      { nome: "Pizza Margherita", qty: 1, opzioni: ["Impasto integrale"] },
      { nome: "Pizza Diavola", qty: 1, opzioni: ["Extra piccante"] },
      { nome: "Tiramisù della casa", qty: 2, opzioni: [] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-07-14T19:40:00" },
      { stato: "In preparazione", personale: "Luca B.", timestamp: "2026-07-14T19:52:00" }
    ]
  },
  {
    id: "WD-482770",
    orario_richiesto: "2026-07-10T13:00:00",
    stato_attuale: "Consegnato",
    prezzo_totale: 14.50,
    righe: [
      { nome: "Tagliatelle al ragù", qty: 1, opzioni: ["Abbondante"] },
      { nome: "Bruschette miste", qty: 1, opzioni: [] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-07-10T12:20:00" },
      { stato: "In preparazione", personale: "Sara M.", timestamp: "2026-07-10T12:24:00" },
      { stato: "Pronto", personale: "Sara M.", timestamp: "2026-07-10T12:38:00" },
      { stato: "In consegna", personale: "Marco T.", timestamp: "2026-07-10T12:41:00" },
      { stato: "Consegnato", personale: "Marco T.", timestamp: "2026-07-10T13:02:00" }
    ]
  },
  {
    id: "WD-481290",
    orario_richiesto: "2026-06-28T20:30:00",
    stato_attuale: "Consegnato",
    prezzo_totale: 9.50,
    righe: [
      { nome: "Pizza Margherita", qty: 1, opzioni: [] },
      { nome: "Caffè", qty: 1, opzioni: ["Zuccherato", "Con panna"] }
    ],
    storico: [
      { stato: "Inserito", personale: null, timestamp: "2026-06-28T19:55:00" },
      { stato: "In preparazione", personale: "Luca B.", timestamp: "2026-06-28T20:01:00" },
      { stato: "Pronto", personale: "Luca B.", timestamp: "2026-06-28T20:14:00" },
      { stato: "In consegna", personale: "Marco T.", timestamp: "2026-06-28T20:17:00" },
      { stato: "Consegnato", personale: "Marco T.", timestamp: "2026-06-28T20:35:00" }
    ]
  }
];