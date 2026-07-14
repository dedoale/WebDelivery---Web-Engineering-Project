(function () {
  "use strict";

  // fasce di apertura, al momento hard-coded
  const FASCE = [
    { start: "12:00", end: "15:00" },
    { start: "19:00", end: "23:00" }
  ];

  // tempo di percorrenza stimato in base alla distanza, al momento è un valore mock
  const TEMPO_PERCORRENZA_MIN = 10;

  const fmt = (n) => "€" + n.toFixed(2).replace(".", ",");
  const fmtTime = (d) => d.toLocaleString("it-IT", { weekday: "short", day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit" });

  function loadCart() {
    try { return JSON.parse(localStorage.getItem("wd_cart")) || []; }
    catch (e) { return []; }
  }

  const cart = loadCart();

  if (cart.length === 0) {
    document.getElementById("emptyState").hidden = false;
    return;
  }
  document.getElementById("checkoutLayout").hidden = false;

  // riepilogo
  const list = document.getElementById("summaryList");
  list.innerHTML = cart.map(line => `
    <li class="summary-item">
      <div class="summary-item-top">
        <span>${line.qty} × ${line.prodotto.nome}</span>
        <span>${fmt(line.prezzoUnitario * line.qty)}</span>
      </div>
      ${line.opzioniScelte.length ? `<div class="summary-item-opts">${line.opzioniScelte.map(o => o.nome).join(", ")}</div>` : ""}
    </li>
  `).join("");

  const totale = cart.reduce((s, l) => s + l.prezzoUnitario * l.qty, 0);
  const tempoPreparazione = cart.reduce((s, l) => s + l.prodotto.tempo_preparazione * l.qty, 0);
  const tempoTotale = tempoPreparazione + TEMPO_PERCORRENZA_MIN;

  document.getElementById("summaryPrepTime").textContent = `~${tempoPreparazione} min`;
  document.getElementById("summaryTravelTime").textContent = `~${TEMPO_PERCORRENZA_MIN} min`;
  document.getElementById("summaryTotalTime").textContent = `~${tempoTotale} min`;
  document.getElementById("summaryTotalPrice").textContent = fmt(totale);

  // calcolo fascia oraria disponibile
  // l'orario scelto non può essere maggiore di quello di chiusura 
  // né minore di (ora attuale + tempo stimato) 
  function toDateOnDay(day, hhmm) {
    const [h, m] = hhmm.split(":").map(Number);
    const d = new Date(day);
    d.setHours(h, m, 0, 0);
    return d;
  }

  function nextAvailableSlot(now, minutesToAdd) {
    const earliest = new Date(now.getTime() + minutesToAdd * 60000);
    for (let dayOffset = 0; dayOffset < 3; dayOffset++) {
      const day = new Date(now);
      day.setDate(day.getDate() + dayOffset);
      for (const fascia of FASCE) {
        const apertura = toDateOnDay(day, fascia.start);
        const chiusura = toDateOnDay(day, fascia.end);
        if (earliest <= chiusura && chiusura > now) {
          const start = earliest > apertura ? earliest : apertura;
          if (start <= chiusura) return { min: start, max: chiusura };
        }
      }
    }
    // fallback, nessuna fascia trovata nei 3 giorni successivi
    return null;
  }

  const now = new Date();
  const slot = nextAvailableSlot(now, tempoTotale);
  const orarioInput = document.getElementById("orario");
  const orarioHint = document.getElementById("orarioHint");

  function toInputValue(d) {
    const pad = (n) => String(n).padStart(2, "0");
    return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
  }

  if (slot) {
    orarioInput.min = toInputValue(slot.min);
    orarioInput.max = toInputValue(slot.max);
    orarioInput.value = toInputValue(slot.min);
    orarioHint.textContent = `Disponibile tra le ${fmtTime(slot.min)} e le ${fmtTime(slot.max)}.`;
  } else {
    orarioHint.textContent = "Nessuna fascia di consegna disponibile a breve, riprova più tardi.";
    orarioInput.disabled = true;
    document.getElementById("confirmBtn").disabled = true;
  }

  // submit
  const form = document.getElementById("deliveryForm");
  const orarioError = document.getElementById("orarioError");

  form.addEventListener("submit", (e) => {
    e.preventDefault();
    orarioError.textContent = "";

    if (!orarioInput.value) {
      orarioError.textContent = "Seleziona un orario di consegna.";
      return;
    }
    const scelto = new Date(orarioInput.value);
    if (slot && (scelto < slot.min || scelto > slot.max)) {
      orarioError.textContent = "L'orario scelto è fuori dalla fascia disponibile.";
      return;
    }

    // da modificare con le servlet reali
    const ordineId = "WD-" + Math.floor(100000 + Math.random() * 900000);

    document.getElementById("checkoutLayout").hidden = true;
    document.getElementById("confirmState").hidden = false;
    document.getElementById("confirmId").textContent = ordineId;
    document.getElementById("confirmTime").textContent = fmtTime(scelto);
    document.getElementById("confirmPrice").textContent = fmt(totale);

    localStorage.removeItem("wd_cart");
  });
})();
