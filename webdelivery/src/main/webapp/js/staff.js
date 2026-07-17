(function () {
  "use strict";

  const fmtDate = (iso) => new Date(iso).toLocaleString("it-IT", {
    day: "2-digit", month: "2-digit", hour: "2-digit", minute: "2-digit"
  });
  const slug = (s) => s.toLowerCase().replace(/\s+/g, "-");
  const prodottoById = (id) => MENU_DATA.prodotti.find(p => p.id === id);

  let currentFilter = "tutti";
  document.getElementById("staffName").textContent = PERSONALE_CORRENTE;

  // tabs di filtro per stato
  function renderTabs() {
    const tabs = document.getElementById("statusTabs");
    const opzioni = ["tutti", ...STATI_ORDINE.filter(s => s !== "Consegnato")];
    tabs.innerHTML = opzioni.map(s =>
      `<button data-filter="${s}" class="${s === currentFilter ? "active" : ""}">${s === "tutti" ? "Tutti" : s}</button>`
    ).join("");
    tabs.querySelectorAll("button").forEach(btn => {
      btn.addEventListener("click", () => {
        currentFilter = btn.dataset.filter;
        renderTabs();
        renderList();
      });
    });
  }

  // avanzamento di stato
  function nextStatus(stato) {
    const i = STATI_ORDINE.indexOf(stato);
    return i >= 0 && i < STATI_ORDINE.length - 1 ? STATI_ORDINE[i + 1] : null;
  }
  // lo stato puo' avanzare solo di uno stato alla volta e non puo' mai tornare indietro
  function advanceOrder(ordine) {
    const nuovo = nextStatus(ordine.stato_attuale);
    if (!nuovo) return;
    ordine.stato_attuale = nuovo;
    ordine.storico.push({
      stato: nuovo,
      personale: PERSONALE_CORRENTE,
      timestamp: new Date().toISOString()
    });
    renderList();
  }

  // track avanzamento
  function renderTrack(ordine) {
    const currentIndex = STATI_ORDINE.indexOf(ordine.stato_attuale);
    return `
      <div class="status-track">
        ${STATI_ORDINE.map((stato, i) => {
          const cls = i < currentIndex ? "done" : (i === currentIndex ? "current" : "");
          return `<div class="status-step ${cls}"><span class="dot"></span><span class="label">${stato}</span></div>`;
        }).join("")}
      </div>
    `;
  }

  function renderCard(ordine) {
    const nuovo = nextStatus(ordine.stato_attuale);
    const numProdotti = ordine.righe.reduce((s, r) => s + r.qty, 0);
    const tempoTot = ordine.righe.reduce((s, r) => s + (prodottoById(r.prodotto_id)?.tempo_preparazione || 0) * r.qty, 0);

    return `
      <article class="order-card" data-id="${ordine.id}">
        <div class="order-card-head" style="cursor:default">
          <div>
            <div class="order-id">Ordine ${ordine.id} — ${ordine.cliente}</div>
            <div class="order-date">Consegna richiesta: ${fmtDate(ordine.orario_richiesto)}</div>
            <span class="status-badge stato-${slug(ordine.stato_attuale)}">${ordine.stato_attuale}</span>
          </div>
        </div>

        ${renderTrack(ordine)}

        <ul class="staff-items">
          ${ordine.righe.map(r => {
            const p = prodottoById(r.prodotto_id);
            return `
              <li>
                <span>
                  <button class="item-name-btn" data-prodotto="${r.prodotto_id}">${r.qty} × ${p ? p.nome : "Prodotto"}</button>
                  ${r.opzioni.length ? `<span class="item-opts">${r.opzioni.join(", ")}</span>` : ""}
                </span>
              </li>
            `;
          }).join("")}
        </ul>

        <div class="order-card-actions">
          <div class="order-card-meta">
            ${numProdotti} prodotti · tempo prep. stimato <strong>${tempoTot} min</strong>
          </div>
          ${nuovo
            ? `<button class="btn btn-stamp btn-small" data-advance="${ordine.id}">Segna come "${nuovo}"</button>`
            : `<span class="order-card-meta">Ordine completato</span>`}
        </div>
      </article>
    `;
  }

  function renderList() {
    const listEl = document.getElementById("staffOrdersList");
    const emptyEl = document.getElementById("staffOrdersEmpty");

    const filtered = ORDINI_CORRENTI.filter(o =>
      currentFilter === "tutti" || o.stato_attuale === currentFilter
    );

    if (filtered.length === 0) {
      listEl.innerHTML = "";
      emptyEl.hidden = false;
      return;
    }
    emptyEl.hidden = true;
    listEl.innerHTML = filtered.map(renderCard).join("");

    listEl.querySelectorAll("[data-advance]").forEach(btn => {
      btn.addEventListener("click", () => {
        const ordine = ORDINI_CORRENTI.find(o => o.id === btn.dataset.advance);
        const nuovoStato = nextStatus(ordine.stato_attuale);
        
        // Estraiamo l'ID numerico reale del database rimuovendo "WD-"
        const realId = ordine.id.replace("WD-", "");

        // Creiamo un form invisibile che invia i dati reali alla StaffServlet
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "personale/aggiorna-stato";
        form.innerHTML = `
            <input type="hidden" name="ordine_id" value="${realId}">
            <input type="hidden" name="stato" value="${nuovoStato}">
        `;
        document.body.appendChild(form);
        form.submit(); // <-- Questo fa partire la richiesta al server!
      });
    });
    listEl.querySelectorAll("[data-prodotto]").forEach(btn => {
      btn.addEventListener("click", () => openProductModal(Number(btn.dataset.prodotto)));
    });
  }

  // scheda prodotto interna
  function openProductModal(id) {
    const p = prodottoById(id);
    if (!p) return;
    document.getElementById("staffModalCategory").textContent = p.categoria;
    document.getElementById("staffModalTitle").textContent = p.nome;
    document.getElementById("staffModalDesc").textContent = p.descrizione;
    document.getElementById("staffModalTime").textContent = `${p.tempo_preparazione} minuti`;
    document.getElementById("staffModalIngredients").innerHTML = (p.ingredienti || [])
      .map(i => `<li><span>${i.nome}</span><span>${i.quantita}</span></li>`).join("");
    document.getElementById("staffModalProcedure").textContent = p.procedura || "Nessuna procedura registrata.";
    document.getElementById("staffModalBackdrop").classList.add("open");
  }
  document.getElementById("staffModalClose").addEventListener("click", () => {
    document.getElementById("staffModalBackdrop").classList.remove("open");
  });
  document.getElementById("staffModalBackdrop").addEventListener("click", (e) => {
    if (e.target.id === "staffModalBackdrop") e.currentTarget.classList.remove("open");
  });
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") document.getElementById("staffModalBackdrop").classList.remove("open");
  });

  renderTabs();
  renderList();
})();