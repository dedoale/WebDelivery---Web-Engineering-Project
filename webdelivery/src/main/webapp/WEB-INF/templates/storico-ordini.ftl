<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>I tuoi ordini — Univaq Food Delivery</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/orders.css">
</head>
<body>

<header class="site-header">
  <div class="header-inner">
    <a href="menu" class="brand">
      <span class="brand-mark">UFD</span>
      <span class="brand-name">Univaq Food Delivery</span>
    </a>
    <nav class="main-nav" aria-label="Navigazione principale">
      <a href="menu#menu">Menu</a>
      <a href="menu#chi-siamo">Chi siamo</a>
      <a href="menu#dove-siamo">Dove siamo</a>
    </nav>
    <div class="header-actions">
      <span class="user-chip">Ciao, <#if utente??>${utente.nome}<#else>Ospite</#if></span>
      <a href="logout" class="btn btn-ghost" style="margin-left: 10px; padding: 4px 12px; font-size: 0.8rem;">Esci</a>
    </div>
  </div>
</header>

<main class="orders-main">
  <p class="ticket-eyebrow">Area cliente</p>
  <h1>I tuoi ordini</h1>
  <p class="orders-sub">Segui lo stato degli ordini in corso e rivedi lo storico degli acquisti passati.</p>

  <nav class="orders-tabs" aria-label="Filtra ordini">
    <button data-filter="in-corso" class="active">In corso</button>
    <button data-filter="storico">Storico</button>
  </nav>

  <div class="orders-list" id="ordersList"></div>

  <p class="orders-empty" id="ordersEmpty" hidden>Nessun ordine in questa categoria.</p>
</main>

<footer class="site-footer">
  <p>Progetto didattico realizzato per il corso di Web Engineering — Univaq Food Delivery™ è un'attività fittizia.</p>
</footer>

<script>
    const ORDINI_CLIENTE = [
        <#list ordini as o>
        {
            id: "${o.id?js_string}",
            orario_richiesto: "${o.orarioRichiesto.format("yyyy-MM-dd'T'HH:mm:ss")}",
            prezzo_totale: ${o.prezzoTotale?c},
            stato_attuale: "${o.statoAttuale?js_string}",
            righe: [
                <#list o.righe as r>
                {
                    nome: "${r.nome?js_string}",
                    qty: ${r.qty},
                    opzioni: [
                        <#list r.opzioni as v>
                        "${v?js_string}"<#sep>, </#sep>
                        </#list>
                    ]
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

    const STATI_ORDINE = ["Inserito", "In preparazione", "Pronto", "In consegna", "Consegnato"];
</script>
<script src="js/orders.js"></script>
</body>
</html>