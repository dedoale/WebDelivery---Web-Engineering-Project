(function () {
  "use strict";
/**
 * Il riepilogo ordine e l'orario di default sono già renderizzati lato server 
 * qui ci sono la validazione live e le fasce orarie, ma non
 * sono necessarie al funzionamento di base del checkout.
 */

  const form = document.getElementById("deliveryForm");
  if (!form) return; // niente da fare (carrello vuoto o ordine già confermato)

  const FASCE = [
    { start: "12:00", end: "15:00" },
    { start: "19:00", end: "23:00" }
  ];

  const tempoConsegnaStimato = Number(form.dataset.tempoConsegna || 0);
  const orarioInput = document.getElementById("orario");
  const orarioError = document.getElementById("orarioError");

  function toDateOnDay(day, hhmm) {
    const [h, m] = hhmm.split(":").map(Number);
    const d = new Date(day);
    d.setHours(h, m, 0, 0);
    return d;
  }

  function isOrarioValido(data) {
    const now = new Date();
    const minimo = new Date(now.getTime() + tempoConsegnaStimato * 60000);
    if (data < minimo) return false;

    return FASCE.some(f => {
      const apertura = toDateOnDay(data, f.start);
      const chiusura = toDateOnDay(data, f.end);
      return data >= apertura && data <= chiusura;
    });
  }

  form.addEventListener("submit", (e) => {
    if (!orarioInput.value) return; // campo opzionale: il server sceglie il primo slot disponibile

    const scelto = new Date(orarioInput.value);
    if (!isOrarioValido(scelto)) {
      e.preventDefault();
      orarioError.textContent = "Scegli un orario entro gli orari di apertura e non troppo vicino ad ora.";
    } else {
      orarioError.textContent = "";
    }
  });
})();