/*
 * dati mock per menu per testing

 * una volta implementate le servlet java + freemarker i dati arriveranno direttamente dal server 
 */

const MENU_DATA = {
  categorie: ["Antipasti", "Primi", "Pizze", "Dolci", "Bevande"],

  prodotti: [
    {
      id: 1,
      categoria: "Antipasti",
      nome: "Bruschette miste",
      descrizione: "Pane casereccio tostato, pomodoro fresco, olio extravergine e basilico.",
      prezzo_base: 5.50,
      tempo_preparazione: 8,
      immagine: null,
      // visibili solo al personale/proprietario
      ingredienti: [
        { nome: "Pane casereccio", quantita: "4 fette" },
        { nome: "Pomodoro", quantita: "2 medi" },
        { nome: "Basilico fresco", quantita: "q.b." },
        { nome: "Olio extravergine", quantita: "2 cucchiai" }
      ],
      procedura: "Tostare il pane, strofinare con aglio, condire con pomodoro a cubetti, olio e basilico.",
      gruppi: []
    },
    {
      id: 2,
      categoria: "Primi",
      nome: "Tagliatelle al ragù",
      descrizione: "Pasta fresca all'uovo con ragù di carne cotto lentamente per 4 ore.",
      prezzo_base: 9.00,
      tempo_preparazione: 15,
      immagine: null,
      ingredienti: [
        { nome: "Tagliatelle fresche", quantita: "120 g" },
        { nome: "Ragù di carne", quantita: "150 g" },
        { nome: "Parmigiano", quantita: "20 g" }
      ],
      procedura: "Cuocere la pasta 3 minuti in acqua salata, mantecare con il ragù caldo, spolverare di parmigiano.",
      gruppi: [
        {
          nome: "Porzione",
          mutua_esclusione: true,
          opzioni: [
            { id: "p1", nome: "Normale", differenza_prezzo: 0, is_default: true },
            { id: "p2", nome: "Abbondante", differenza_prezzo: 2.00, is_default: false }
          ]
        }
      ]
    },
    {
      id: 3,
      categoria: "Pizze",
      nome: "Pizza Margherita",
      descrizione: "Pomodoro San Marzano, fiordilatte, basilico fresco, cotta nel forno a legna.",
      prezzo_base: 7.00,
      tempo_preparazione: 12,
      immagine: null,
      ingredienti: [
        { nome: "Impasto pizza", quantita: "250 g" },
        { nome: "Pomodoro San Marzano", quantita: "80 g" },
        { nome: "Fiordilatte", quantita: "100 g" },
        { nome: "Basilico fresco", quantita: "q.b." }
      ],
      procedura: "Stendere l'impasto, condire con pomodoro e mozzarella, cuocere nel forno a legna a 400°C per 90 secondi, guarnire con basilico.",
      gruppi: [
        {
          nome: "Impasto",
          mutua_esclusione: true,
          opzioni: [
            { id: "i1", nome: "Classico", differenza_prezzo: 0, is_default: true },
            { id: "i2", nome: "Integrale", differenza_prezzo: 0.50, is_default: false }
          ]
        },
        {
          nome: "Aggiunte",
          mutua_esclusione: false,
          opzioni: [
            { id: "a1", nome: "Extra mozzarella", differenza_prezzo: 1.50, is_default: false },
            { id: "a2", nome: "Origano fresco", differenza_prezzo: 0.30, is_default: false }
          ]
        }
      ]
    },
    {
      id: 4,
      categoria: "Pizze",
      nome: "Pizza Diavola",
      descrizione: "Pomodoro, fiordilatte, salame piccante, olio al peperoncino.",
      prezzo_base: 8.00,
      tempo_preparazione: 12,
      immagine: null,
      ingredienti: [
        { nome: "Impasto pizza", quantita: "250 g" },
        { nome: "Pomodoro", quantita: "80 g" },
        { nome: "Fiordilatte", quantita: "100 g" },
        { nome: "Salame piccante", quantita: "60 g" },
        { nome: "Olio al peperoncino", quantita: "q.b." }
      ],
      procedura: "Stendere l'impasto, condire con pomodoro, mozzarella e salame, cuocere nel forno a legna, rifinire con olio piccante in uscita.",
      gruppi: [
        {
          nome: "Piccantezza",
          mutua_esclusione: true,
          opzioni: [
            { id: "pc1", nome: "Normale", differenza_prezzo: 0, is_default: true },
            { id: "pc2", nome: "Extra piccante", differenza_prezzo: 0, is_default: false }
          ]
        }
      ]
    },
    {
      id: 5,
      categoria: "Dolci",
      nome: "Tiramisù della casa",
      descrizione: "Ricetta originale con savoiardi, mascarpone e caffè della torrefazione locale.",
      prezzo_base: 4.50,
      tempo_preparazione: 3,
      immagine: null,
      ingredienti: [
        { nome: "Savoiardi", quantita: "4" },
        { nome: "Crema al mascarpone", quantita: "150 g" },
        { nome: "Caffè espresso", quantita: "30 ml" },
        { nome: "Cacao amaro", quantita: "q.b." }
      ],
      procedura: "Porzionare dalla vaschetta preparata in mattinata, spolverare di cacao al momento del servizio.",
      gruppi: []
    },
    {
      id: 6,
      categoria: "Bevande",
      nome: "Caffè",
      descrizione: "Espresso della nostra torrefazione di fiducia.",
      prezzo_base: 1.00,
      tempo_preparazione: 2,
      immagine: null,
      ingredienti: [
        { nome: "Miscela espresso", quantita: "7 g" }
      ],
      procedura: "Estrazione singola da macinato fresco, 25 secondi circa.",
      gruppi: [
        {
          nome: "Zucchero",
          mutua_esclusione: true,
          opzioni: [
            { id: "z1", nome: "Senza zucchero", differenza_prezzo: -0.05, is_default: false },
            { id: "z2", nome: "Zuccherato", differenza_prezzo: 0, is_default: true },
            { id: "z3", nome: "Molto zuccherato", differenza_prezzo: 0, is_default: false }
          ]
        },
        {
          nome: "Aggiunte",
          mutua_esclusione: false,
          opzioni: [
            { id: "cp1", nome: "Con panna", differenza_prezzo: 0.50, is_default: false },
            { id: "cf1", nome: "Freddo", differenza_prezzo: 1.00, is_default: false }
          ]
        }
      ]
    }
  ]
};

const STATI_ORDINE = [
  "Inserito",
  "In preparazione",
  "Pronto",
  "In consegna",
  "Consegnato"
];