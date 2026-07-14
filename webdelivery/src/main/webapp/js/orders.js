(function () {
  "use strict";

  const fmt = (n) => "€" + n.toFixed(2).replace(".", ",");
  const fmtDate = (iso) => new Date(iso).toLocaleString("it-IT", {
    weekday: "short", day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit"
  });
  const slug = (s) => s.toLowerCase().replace(/\s+/g, "-");

  let currentFilter = "in-corso";

  function isInCorso(ordine) {
    return ordine.stato_attuale !== "Consegnato";
  }

  function renderTrack(ordine) {
    const currentIndex = STATI_ORDINE.indexOf(ordine.stato_attuale);
    return `
      <div class="status-track">
        ${STATI_ORDINE.map((stato, i) => {
          const cls = i < currentIndex ? "done" : (i === currentIndex ? "current" : "");
          return `
            <div class="status-step ${cls}">
              <span class="dot"></span>
              <span class="label">${stato}</span>
            </div>
          `;
        }).join("")}
      </div>
    `;
  }

  function renderCard(ordine) {
    return `
      <article class="order-card" data-id="${ordine.id}">
        <div class="order-card-head">
          <div>
            <div class="order-id">Ordine ${ordine.id}</div>
            <div class="order-date">Consegna richiesta: ${fmtDate(ordine.orario_richiesto)}</div>
            <span class="status-badge stato-${slug(ordine.stato_attuale)}">${ordine.stato_attuale}</span>
          </div>
          <div style="text-align:right">
            <div class="order-price">${fmt(ordine.prezzo_totale)}</div>
            <span class="toggle-icon">▾</span>
          </div>
        </div>

        ${renderTrack(ordine)}

        <div class="order-details">
          <ul class="order-items">
            ${ordine.righe.map(r => `
              <li>
                ${r.qty} × ${r.nome}
                ${r.opzioni.length ? `<div class="item-opts">${r.opzioni.join(", ")}</div>` : ""}
              </li>
            `).join("")}
          </ul>

          <p class="order-log-title">Aggiornamenti</p>
          <ul class="order-log">
            ${ordine.storico.map(h => `
              <li>
                <strong>${h.stato}</strong>
                ${h.personale ? ` — ${h.personale}` : ""}
                · ${fmtDate(h.timestamp)}
              </li>
            `).join("")}
          </ul>
        </div>
      </article>
    `;
  }

  function render() {
    const listEl = document.getElementById("ordersList");
    const emptyEl = document.getElementById("ordersEmpty");

    const filtered = ORDINI_CLIENTE.filter(o =>
      currentFilter === "in-corso" ? isInCorso(o) : !isInCorso(o)
    );

    if (filtered.length === 0) {
      listEl.innerHTML = "";
      emptyEl.hidden = false;
      return;
    }
    emptyEl.hidden = true;
    listEl.innerHTML = filtered.map(renderCard).join("");

    listEl.querySelectorAll(".order-card-head").forEach(head => {
      head.addEventListener("click", () => {
        head.closest(".order-card").classList.toggle("open");
      });
    });
  }

  document.querySelectorAll(".orders-tabs button").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll(".orders-tabs button").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      currentFilter = btn.dataset.filter;
      render();
    });
  });

  render();
})();