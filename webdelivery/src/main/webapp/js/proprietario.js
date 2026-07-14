(function () {
  "use strict";

  const fmt = (n) => "€" + n.toFixed(2).replace(".", ",");
  const fmtDate = (iso) => new Date(iso).toLocaleString("it-IT", {
    day: "2-digit", month: "2-digit", year: "numeric", hour: "2-digit", minute: "2-digit"
  });
  const slug = (s) => s.toLowerCase().replace(/\s+/g, "-");

  // dati mock per il personale
  // da modificare
  let STAFF_DATA = [
    { id: 1, nome: "Luca B.", email: "luca@univaqfooddelivery.it" },
    { id: 2, nome: "Sara M.", email: "sara@univaqfooddelivery.it" },
    { id: 3, nome: "Marco T.", email: "marco@univaqfooddelivery.it" }
  ];

  // navigazione fra i tab
  document.querySelectorAll("#adminNav button").forEach(btn => {
    btn.addEventListener("click", () => {
      document.querySelectorAll("#adminNav button").forEach(b => b.classList.remove("active"));
      btn.classList.add("active");
      
      document.querySelectorAll(".admin-section").forEach(sec => sec.hidden = true);
      document.getElementById(btn.dataset.target).hidden = false;
    });
  });

  // statistiche
  function calcolaStatistiche() {
    const dataSelezionata = new Date(document.getElementById("statDate").value);
    if(isNaN(dataSelezionata)) return;

    const targetYear = dataSelezionata.getFullYear();
    const targetMonth = dataSelezionata.getMonth();
    const targetDay = dataSelezionata.getDate();

    // da modificare con le servlet
    const tuttiGliOrdini = [...ORDINI_CORRENTI, ...ORDINI_CLIENTE];
    
    let incassoGiorno = 0;
    let incassoMese = 0;
    let ordiniGiorno = 0;
    const frequenzaProdotti = {};

    tuttiGliOrdini.forEach(o => {
      const d = new Date(o.orario_richiesto);
      const isSameMonth = d.getFullYear() === targetYear && d.getMonth() === targetMonth;
      const isSameDay = isSameMonth && d.getDate() === targetDay;

      // ricalcolo prezzo_totale per il frontend se manca
      let prezzoOrd = o.prezzo_totale;
      if (prezzoOrd === undefined) {
        prezzoOrd = o.righe.reduce((sum, r) => {
          const p = MENU_DATA.prodotti.find(prod => prod.id === r.prodotto_id);
          return sum + (p ? p.prezzo_base * r.qty : 0);
        }, 0);
      }

      if (isSameMonth) incassoMese += prezzoOrd;
      if (isSameDay) {
        incassoGiorno += prezzoOrd;
        ordiniGiorno++;
        
        // popolo la frequenza per i piu' richiesti/meno rischiesti
        // al momento li prende da staff-data.js e orders-data.js
        // da modificare con le servlet
        o.righe.forEach(r => {
          // Gestisce sia il formato di staff-data.js (prodotto_id) che di orders-data.js (nome)
          let nome = r.nome;
          if (!nome && r.prodotto_id) {
             const p = MENU_DATA.prodotti.find(prod => prod.id === r.prodotto_id);
             if (p) nome = p.nome;
          }
          if (nome) {
            frequenzaProdotti[nome] = (frequenzaProdotti[nome] || 0) + r.qty;
          }
        });
      }
    });

    document.getElementById("statIncassoGiorno").textContent = fmt(incassoGiorno);
    document.getElementById("statIncassoMese").textContent = fmt(incassoMese);
    document.getElementById("statOrdiniOggi").textContent = ordiniGiorno;

    // calcolo i piu' e i meno richiesti
    const arrayFrequenze = Object.entries(frequenzaProdotti).sort((a, b) => b[1] - a[1]);
    
    const topList = document.getElementById("listTopProducts");
    const flopList = document.getElementById("listFlopProducts");
    
    if (arrayFrequenze.length === 0) {
      topList.innerHTML = "<li>Nessun dato per questa data</li>";
      flopList.innerHTML = "<li>Nessun dato per questa data</li>";
    } else {
      topList.innerHTML = arrayFrequenze.slice(0, 3).map(i => `<li><span>${i[0]}</span> <strong>${i[1]} pz.</strong></li>`).join("");
      // per i meno richiesti, inverto l'array e prendo gli ultimi 3
      const flopArr = [...arrayFrequenze].reverse().slice(0, 3);
      flopList.innerHTML = flopArr.map(i => `<li><span>${i[0]}</span> <strong>${i[1]} pz.</strong></li>`).join("");
    }
  }

  document.getElementById("btnCalcStats").addEventListener("click", calcolaStatistiche);

  // tutti gli ordini
  function renderAllOrders() {
    const listEl = document.getElementById("allOrdersList");
    const tutti = [...ORDINI_CORRENTI, ...ORDINI_CLIENTE];
    
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
            <button class="btn btn-primary btn-mini" onclick="alert('In prod: apre modale modifica piatto ${p.id}')">Modifica</button>
            <button class="btn btn-ghost btn-mini" style="color:var(--stamp-red)">Elimina</button>
          </div>
        </td>
      </tr>
    `).join("");
  }

  document.getElementById("btnNuovoProdotto").addEventListener("click", () => {
    alert("In prod: apre modale con form vuoto per nuovo prodotto.");
  });

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
        STAFF_DATA = STAFF_DATA.filter(staff => staff.id !== id);
        renderStaffTable();
      });
    });
  }

  document.getElementById("staffForm").addEventListener("submit", (e) => {
    e.preventDefault();
    const nome = document.getElementById("staffNome").value;
    const email = document.getElementById("staffEmail").value;
    // La password in un caso reale verrebbe inviata al backend per l'hash
    // da modificare
    
    STAFF_DATA.push({
      id: Date.now(),
      nome: nome,
      email: email
    });
    
    e.target.reset();
    renderStaffTable();
    alert(`Account per ${nome} creato con successo.`);
  });


  // init
  calcolaStatistiche();
  renderAllOrders();
  renderMenuTable();
  renderStaffTable();

})();