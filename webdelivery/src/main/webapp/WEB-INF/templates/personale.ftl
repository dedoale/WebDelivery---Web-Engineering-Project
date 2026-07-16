<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Ordini in corso — Personale — Univaq Food Delivery</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/orders.css">
<link rel="stylesheet" href="css/staff.css">
</head>
<body class="staff-body">

<header class="site-header staff-header">
  <div class="header-inner">
    <a href="personale" class="brand">
      <span class="brand-mark">UFD</span>
      <span class="brand-name">Cucina — Personale</span>
    </a>
    <div class="header-actions">
      <span class="user-chip">Turno: <strong id="staffName"><#if utente??>${utente.nome}<#else>Ospite</#if></strong></span>
      <a href="logout" class="btn btn-ghost btn-small">Esci</a>
    </div>
  </div>
</header>

<main class="staff-main">
  <p class="ticket-eyebrow">Vista personale</p>
  <h1>Ordini correnti</h1>
  <p class="orders-sub">Aggiorna lo stato man mano che gli ordini avanzano in cucina. Gli stati si susseguono in ordine: non è possibile saltarli o tornare indietro.</p>

  <nav class="orders-tabs" aria-label="Filtra per stato" id="statusTabs">
    <!-- popolato via JS -->
  </nav>

  <div class="orders-list" id="staffOrdersList"></div>
  <p class="orders-empty" id="staffOrdersEmpty" hidden>Nessun ordine in questo stato.</p>
</main>


<div class="modal-backdrop" id="staffModalBackdrop">
  <div class="modal-ticket" role="dialog" aria-modal="true" aria-labelledby="staffModalTitle">
    <button class="modal-close" id="staffModalClose" aria-label="Chiudi">✕</button>
    <div class="modal-body">
      <p class="ticket-eyebrow" id="staffModalCategory">Categoria</p>
      <h3 id="staffModalTitle">Nome prodotto</h3>
      <p class="modal-desc" id="staffModalDesc"></p>

      <div class="staff-internal">
        <p class="order-log-title">Tempo di preparazione</p>
        <p id="staffModalTime"></p>

        <p class="order-log-title">Ingredienti</p>
        <ul class="ingredient-list" id="staffModalIngredients"></ul>

        <p class="order-log-title">Procedura</p>
        <p id="staffModalProcedure"></p>
      </div>
    </div>
  </div>
</div>

<footer class="site-footer">
  <p>Progetto didattico realizzato per il corso di Web Engineering — Univaq Food Delivery™ è un'attività fittizia.</p>
</footer>

<script>
    const PERSONALE_CORRENTE = "<#if utente??>${utente.nome?js_string}<#else>Ospite</#if>";

    const MENU_DATA = {
        prodotti: [
            <#list prodotti as p>
            {
                id: ${p.id},
                nome: "${p.nome?js_string}",
                descrizione: "${p.descrizione?js_string}",
                tempo_preparazione: ${p.tempoPreparazione},
                categoria: "${(p.categoria!'')?js_string}",
                procedura: "${(p.procedura!'')?js_string}",
                ingredienti: [
                    <#list p.ingredienti as i>
                    { nome: "${i.nome?js_string}", quantita: "${i.quantita?js_string}" }<#sep>,</#sep>
                    </#list>
                ]
            }<#sep>,</#sep>
            </#list>
        ]
    };

    const ORDINI_CORRENTI = [
        <#list ordiniCorrenti as o>
        {
            id: "${o.id}",
            cliente: "${o.cliente?js_string}",
            orario_richiesto: "${o.orarioRichiesto}",
            stato_attuale: "${o.statoAttuale?js_string}",
            righe: [
                <#list o.righe as r>
                {
                    prodotto_id: ${r.prodottoId},
                    qty: ${r.qty},
                    opzioni: [
                        <#list r.opzioni as opt>
                        "${opt?js_string}"<#sep>, </#sep>
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
                    timestamp: "${st.timestamp}"
                }<#sep>,</#sep>
                </#list>
            ]
        }<#sep>,</#sep>
        </#list>
    ];
</script>
<script src="js/staff.js"></script>
</body>
</html>