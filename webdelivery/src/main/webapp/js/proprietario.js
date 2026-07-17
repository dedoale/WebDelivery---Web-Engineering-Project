(function () {
  "use strict";

  const fmt = (n) => "€" + n.toFixed(2).replace(".", ",");
  const fmtDate = (iso) => new Date(iso).toLocaleString("it-IT", {
    day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"
  });
  const slug = (s) => s.toLowerCase().replace(/\s+/g, "-");

  // navigazione fra i tab
  document.querySelectorAll("#adminNav button").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll("#adminNav button").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      
      document.querySelectorAll(".admin-section").forEach(sec => sec.hidden = true);
      document.getElementById(btn.dataset.target).hidden = false;
    });
  });



  // tutti gli ordini
  function renderAllOrders() {
    const listEl = document.getElementById("allOrdersList");
    const tutti = [...ORDINI_CORRENTI];
    
    // ordino dal più recente (basato sull'orario richiesto)
    tutti.sort((a, b) => new Date(b.orario_richiesto) - new Date(a.orario_richiesto));

    listEl.innerHTML = tutti.map(ordine => {
      // calcolo dati aggregati per la vista proprietario
      const numProdotti = ordine.righe.reduce((s, r) => s + r.qty, 0);
      let tempoTot = 0;
      ordine.righe.forEach(r => {
        const p = MENU_DATA.prodotti.find(prod => prod.id === r.prodotto_id || prod.nome === r.nome);
        if(p) tempoTot += (p.tempo_preparazione * r.qty);
      });

      return `
        <article class="order-card" style="margin-bottom:1rem;">
          <div class="order-card-head" onclick="this.parentElement.classList.toggle('open')">
            <div>
              <div class="order-id">Ordine ${ordine.id} ${ordine.cliente ? `— ${ordine.cliente}` : ''}</div>
              <div class="order-date">Consegna: ${fmtDate(ordine.orario_richiesto)}</div>
              <span class="status-badge stato-${slug(ordine.stato_attuale)}">${ordine.stato_attuale}</span>
            </div>
            <div style="text-align:right">
              <div style="font-size:.85rem; color:var(--ink-soft); margin-bottom:.3em;">
                ${numProdotti} pz. | ~${tempoTot} min
              </div>
              <span class="toggle-icon">▾</span>
            </div>
          </div>
          
          <div class="order-details">
            <p class="order-log-title">Tracciamento Operatori</p>
            <ul class="order-log">
              ${ordine.storico.map(h => `
                <li>
                  <strong>${h.stato}</strong>
                  ${h.personale ? ` impostato da <span style="color:var(--stamp-red)">${h.personale}</span>` : " (Cliente)"}
                  · ${fmtDate(h.timestamp)}
                </li>
              `).join("")}
            </ul>
          </div>
        </article>
      `;
    }).join("");
  }

// gestione menu'
  function renderMenuTable() {
    const tbody = document.getElementById("menuTableBody");
    tbody.innerHTML = MENU_DATA.prodotti.map(p => `
      <tr>
        <td>#${p.id}</td>
        <td>${p.categoria}</td>
        <td><strong>${p.nome}</strong></td>
        <td>${fmt(p.prezzo_base)}</td>
        <td>${p.tempo_preparazione}</td>
        <td>
          <div class="table-actions">
            <button class="btn btn-primary btn-mini" onclick="openEditModal(${p.id})">Modifica</button>
            <!-- Aggiunto data-del-prodotto -->
            <button class="btn btn-ghost btn-mini" style="color:var(--stamp-red)" data-del-prodotto="${p.id}">Elimina</button>
          </div>
        </td>
      </tr>
    `).join("");

    // logica eliminazione prodotto
    tbody.querySelectorAll("[data-del-prodotto]").forEach(btn => {
      btn.addEventListener("click", (e) => {
        // Un piccolo alert di conferma per evitare cancellazioni accidentali
        if (confirm("Sei sicuro di voler rimuovere questo prodotto dal menu?")) {
          const id = parseInt(e.target.dataset.delProdotto);
          
          // Invia la richiesta POST al backend
          const form = document.createElement("form");
          form.method = "POST";
          form.action = "proprietario/menu/elimina";
          form.innerHTML = `<input type="hidden" name="prodotto_id" value="${id}">`;
          document.body.appendChild(form);
          form.submit();
        }
      });
    });
  }

  // gestione personale
  function renderStaffTable() {
    const tbody = document.getElementById("staffTableBody");
    tbody.innerHTML = STAFF_DATA.map(s => `
      <tr>
        <td><strong>${s.nome}</strong></td>
        <td>${s.email}</td>
        <td>
          <button class="btn btn-ghost btn-mini" style="color:var(--stamp-red)" data-del-staff="${s.id}">Rimuovi</button>
        </td>
      </tr>
    `).join("");

    tbody.querySelectorAll("[data-del-staff]").forEach(btn => {
      btn.addEventListener("click", (e) => {
        const id = parseInt(e.target.dataset.delStaff);
        
        // Invia la richiesta al backend
        const form = document.createElement("form");
        form.method = "POST";
        form.action = "proprietario/personale/rimuovi";
        form.innerHTML = `<input type="hidden" name="staff_id" value="${id}">`;
        document.body.appendChild(form);
        form.submit();
      });
    });
  }

  // gestione modale Nuovo Prodotto
  document.getElementById("btnNuovoProdotto").addEventListener("click", () => {
    document.getElementById("modalNewProduct").classList.add("open");
  });

  // gestione modale Modifica
  window.openEditModal = function(id) {
    const p = MENU_DATA.prodotti.find(prod => prod.id === id);
    if (!p) return;
    
    // popola i campi del form
    document.getElementById("edit_id").value = p.id;
    document.getElementById("edit_nome").value = p.nome;
    document.getElementById("edit_prezzo").value = p.prezzo_base;
    document.getElementById("edit_tempo").value = p.tempo_preparazione;
    document.getElementById("edit_categoria").value = p.categoria || '';
    document.getElementById("edit_descrizione").value = p.descrizione || '';
    
    document.getElementById("modalEditProduct").classList.add("open");
  };

  // init
  renderAllOrders();
  renderMenuTable();
  renderStaffTable();

})();