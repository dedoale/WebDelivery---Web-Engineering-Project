<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Dashboard Proprietario — Univaq Food Delivery</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/orders.css">
<link rel="stylesheet" href="css/proprietario.css">
</head>
<body>

<header class="site-header">
  <div class="header-inner">
    <a href="proprietario" class="brand">
      <span class="brand-mark">UFD</span>
      <span class="brand-name">Direzione — Proprietario</span>
    </a>
    <div class="header-actions">
      <span class="user-chip"><#if utente??>${utente.nome}<#else>Admin</#if></span>
      <a href="logout" class="btn btn-ghost btn-small">Esci</a>
    </div>
  </div>
</header>

<main class="admin-main">
  <nav class="admin-nav" id="adminNav" aria-label="Menu amministrazione">
    <button data-target="sec-statistiche" class="active">Statistiche</button>
    <button data-target="sec-ordini">Tutti gli Ordini</button>
    <button data-target="sec-menu">Gestione Menu</button>
    <button data-target="sec-personale">Staff</button>
  </nav>

  <!-- statistiche -->
  <section id="sec-statistiche" class="admin-section">
    <form class="stats-controls" action="proprietario" method="GET">
      <div class="form-field">
        <label for="statDate">Data di riferimento</label>
        <input type="date" id="statDate" name="statDate" value="${statDate}">
      </div>
      <button type="submit" class="btn btn-primary" id="btnCalcStats">Calcola</button>
    </form>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-label">Incasso del Giorno</div>
        <div class="stat-value">€${incassoGiorno?string("0.00")}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Incasso del Mese</div>
        <div class="stat-value">€${incassoMese?string("0.00")}</div>
      </div>
      <div class="stat-card">
        <div class="stat-label">Ordini Completati (Oggi)</div>
        <div class="stat-value">${ordiniOggi}</div>
      </div>
    </div>

    <div class="ranking-grid">
      <div class="ranking-card">
        <div class="stat-label">I 3 Prodotti più venduti</div>
        <ul class="ranking-list">
          <#if topProdotti?size == 0>
            <li>Nessun dato per questa data</li>
          <#else>
            <#list topProdotti as p>
              <li><span>${p.key}</span> <strong>${p.value} pz.</strong></li>
            </#list>
          </#if>
        </ul>
      </div>
      <div class="ranking-card">
        <div class="stat-label">I 3 Prodotti meno venduti</div>
        <ul class="ranking-list">
          <#if flopProdotti?size == 0>
            <li>Nessun dato per questa data</li>
          <#else>
            <#list flopProdotti as p>
              <li><span>${p.key}</span> <strong>${p.value} pz.</strong></li>
            </#list>
          </#if>
        </ul>
      </div>
    </div>
  </section>

  <!-- ordini -->
  <section id="sec-ordini" class="admin-section" hidden>
    <div class="section-head-admin">
      <div>
        <p class="ticket-eyebrow">Monitoraggio globale</p>
        <h2>Tutti gli ordini</h2>
      </div>
    </div>
    <div class="orders-list" id="allOrdersList"></div>
  </section>

  <!-- menu' -->
  <section id="sec-menu" class="admin-section" hidden>
    <div class="section-head-admin">
      <div>
        <p class="ticket-eyebrow">Catalogo Prodotti</p>
        <h2>Gestione Menu</h2>
      </div>
      <button class="btn btn-stamp" id="btnNuovoProdotto">+ Nuovo Prodotto</button>
    </div>
    <div class="admin-table-wrap">
      <table class="admin-table">
        <thead>
          <tr>
            <th>ID</th><th>Categoria</th><th>Nome</th><th>Prezzo</th><th>Tempo (min)</th><th>Azioni</th>
          </tr>
        </thead>
        <tbody id="menuTableBody"></tbody>
      </table>
    </div>
  </section>

  <!-- personale -->
  <section id="sec-personale" class="admin-section" hidden>
    <div class="section-head-admin">
      <div>
        <p class="ticket-eyebrow">Gestione Utenze</p>
        <h2>Membri del Personale</h2>
      </div>
    </div>
    
    <div style="display: grid; grid-template-columns: 1fr 2fr; gap: 2rem; align-items: start;">
      <form class="auth-form" id="staffForm" action="proprietario/personale/aggiungi" method="POST" style="background: #fff; padding: 1.5rem; border: 1.5px solid var(--ink);">
        <h3>Nuovo membro</h3>
        <div class="form-field">
          <label>Nome e Cognome</label>
          <input type="text" id="staffNome" name="nome" required>
        </div>
        <div class="form-field">
          <label>Email</label>
          <input type="email" id="staffEmail" name="email" required>
        </div>
        <div class="form-field">
          <label>Password provvisoria</label>
          <input type="text" id="staffPassword" name="password" required>
        </div>
        <button type="submit" class="btn btn-primary btn-full">Registra</button>
      </form>
      
      <div class="admin-table-wrap">
        <table class="admin-table">
          <thead>
            <tr><th>Nome</th><th>Email</th><th>Azioni</th></tr>
          </thead>
          <tbody id="staffTableBody"></tbody>
        </table>
      </div>
    </div>
  </section>

</main>

<!-- modale aggiungi / ripristina prodotto -->
  <div class="modal-backdrop" id="modalNewProduct">
    <div class="modal-ticket" style="max-width: 600px; padding: 2rem;">
      <button class="modal-close" onclick="document.getElementById('modalNewProduct').classList.remove('open')">&times;</button>
      
      <h3>Aggiungi Nuovo Prodotto</h3>
      <form action="proprietario/menu/aggiungi" method="POST" class="auth-form" style="margin-bottom: 2rem;">
        <div class="form-row-split">
          <div class="form-field"><label>Nome</label><input type="text" name="nome" required></div>
          <div class="form-field"><label>Prezzo Base (€)</label><input type="number" step="0.01" name="prezzo_base" required></div>
        </div>
        <div class="form-row-split" style="margin-top: 1rem;">
          <div class="form-field"><label>Tempo Prep. (min)</label><input type="number" name="tempo_preparazione" required></div>
          <div class="form-field"><label>Categoria</label><input type="text" name="categoria"></div>
        </div>
        <div class="form-field" style="margin-top: 1rem;">
          <label>Descrizione</label>
          <textarea name="descrizione" rows="2" required style="width:100%; border:1.5px solid var(--line); border-radius:2px; padding:0.5em; font-family:var(--font-body)"></textarea>
        </div>
        <button type="submit" class="btn btn-stamp btn-full">Salva Nuovo Prodotto</button>
      </form>

      <hr style="border: 0; border-top: 1px dashed var(--line); margin: 2rem 0;">
      
      <h3>Cestino (Eliminati di Recente)</h3>
      <ul class="ranking-list">
        <#if prodottiInattivi?? && prodottiInattivi?size gt 0>
          <#list prodottiInattivi as pi>
            <li style="display:flex; justify-content:space-between; align-items:center; padding: 0.8rem 0;">
              <span><strong>${pi.nome}</strong> <span style="color:var(--ink-soft); font-size:0.8rem;">(€${pi.prezzoBase?string("0.00")})</span></span>
              <form action="proprietario/menu/ripristina" method="POST" style="margin:0;">
                <input type="hidden" name="prodotto_id" value="${pi.id}">
                <button type="submit" class="btn btn-ghost btn-mini" style="color:var(--kitchen-green)">Ripristina</button>
              </form>
            </li>
          </#list>
        <#else>
          <li><span style="color:var(--ink-soft);">Nessun prodotto nel cestino.</span></li>
        </#if>
      </ul>
    </div>
  </div>

  <!-- modale modifica prodotto -->
  <div class="modal-backdrop" id="modalEditProduct">
    <div class="modal-ticket" style="max-width: 600px; padding: 2rem;">
      <button class="modal-close" onclick="document.getElementById('modalEditProduct').classList.remove('open')">&times;</button>
      <h3>Modifica Prodotto</h3>
      <form action="proprietario/menu/modifica" method="POST" class="auth-form">
        <input type="hidden" name="prodotto_id" id="edit_id">
        <div class="form-row-split">
          <div class="form-field"><label>Nome</label><input type="text" name="nome" id="edit_nome" required></div>
          <div class="form-field"><label>Prezzo Base (€)</label><input type="number" step="0.01" name="prezzo_base" id="edit_prezzo" required></div>
        </div>
        <div class="form-row-split" style="margin-top: 1rem;">
          <div class="form-field"><label>Tempo Prep. (min)</label><input type="number" name="tempo_preparazione" id="edit_tempo" required></div>
          <div class="form-field"><label>Categoria</label><input type="text" name="categoria" id="edit_categoria"></div>
        </div>
        <div class="form-field" style="margin-top: 1rem;">
          <label>Descrizione</label>
          <textarea name="descrizione" id="edit_descrizione" rows="2" required style="width:100%; border:1.5px solid var(--line); border-radius:2px; padding:0.5em; font-family:var(--font-body)"></textarea>
        </div>
        <button type="submit" class="btn btn-primary btn-full">Aggiorna Prodotto</button>
      </form>
    </div>
  </div>

<footer class="site-footer">
  <p>Progetto didattico realizzato per il corso di Web Engineering — Univaq Food Delivery™ è un'attività fittizia.</p>
</footer>

<script>
    const MENU_DATA = {
        prodotti: [
            <#list prodotti as p>
            {
                id: ${p.id},
                nome: "${p.nome?js_string}",
                categoria: "${(p.categoria!'')?js_string}",
                prezzo_base: ${p.prezzoBase?c},
                tempo_preparazione: ${p.tempoPreparazione},
                descrizione: "${p.descrizione?js_string}"
            }<#sep>,</#sep>
            </#list>
        ]
    };

    let STAFF_DATA = [
        <#list staff as s>
        {
            id: ${s.id},
            nome: "${s.nome?js_string}",
            email: "${s.email?js_string}"
        }<#sep>,</#sep>
        </#list>
    ];

    const ORDINI_CORRENTI = [
        <#list ordini as o>
        {
            id: "${o.id?js_string}",
            cliente: "${o.cliente?js_string}",
            orario_richiesto: "${o.orarioRichiesto.format("yyyy-MM-dd'T'HH:mm:ss")}",
            prezzo_totale: ${o.prezzoTotale?c},
            stato_attuale: "${o.statoAttuale?js_string}",
            righe: [
                <#list o.righe as r>
                {
                    prodotto_id: ${r.prodottoId},
                    qty: ${r.qty}
                }<#sep>,</#sep>
                </#list>
            ],
            storico: [
                <#list o.storico as st>
                {
                    stato: "${st.stato?js_string}",
                    personale: "${(st.personale!'')?js_string}",
                    timestamp: "${st.timestamp.format("yyyy-MM-dd'T'HH:mm:ss")}"
                }<#sep>,</#sep>
                </#list>
            ]
        }<#sep>,</#sep>
        </#list>
    ];

    const ORDINI_CLIENTE = [];
</script>
<script src="js/proprietario.js"></script>
</body>
</html>