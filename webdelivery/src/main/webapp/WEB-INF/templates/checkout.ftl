<!DOCTYPE html>
<html lang="it">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Il tuo ordine — Univaq Food Delivery</title>
<link rel="stylesheet" href="css/style.css">
<link rel="stylesheet" href="css/checkout.css">
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
  </div>
</header>

<main class="checkout-main">

  <!-- ordine confermato -->
  <#if ordineConfermato??>
    <!-- ordine confermato: Mostra i dati che arrivano dal Database -->
    <section class="confirm-state" id="confirmState">
      <div class="confirm-ticket">
        <p class="ticket-eyebrow">Ordine registrato</p>
        <h1>Comanda inviata in cucina!</h1>
        <p id="confirmSummary">Riceverai a breve una email di riepilogo.</p>
        <dl class="confirm-facts">
          <div><dt>Numero ordine</dt><dd>${confermatoOrdine.id}</dd></div>
          <div><dt>Totale</dt><dd>€${confermatoOrdine.prezzoTotale?string("0.00")}</dd></div>
        </dl>
        <a href="menu" class="btn btn-stamp">Torna alla home</a>
        <a href="ordini" class="btn btn-ghost">Segui il tuo ordine</a>
      </div>
    </section>
    <!-- Svuota il carrello dopo il salvataggio! -->
  <script>localStorage.removeItem("wd_cart");</script>

  <#else>

  <!-- carrello vuoto -->
  <section class="empty-state" id="emptyState" hidden>
    <p class="ticket-eyebrow">Comanda vuota</p>
    <h1>Il carrello è vuoto</h1>
    <p>Non hai ancora selezionato nessun piatto.</p>
    <a href="menu#menu" class="btn btn-stamp">Torna al menu</a>
  </section>

  <!-- composizione ordine -->
  <section class="checkout-layout" id="checkoutLayout" hidden>

    <div class="order-summary">
      <p class="ticket-eyebrow">Riepilogo comanda</p>
      <h1>Il tuo ordine</h1>

      <ul class="summary-list" id="summaryList"></ul>

      <div class="summary-totals">
        <div class="summary-row">
          <span>Tempo di preparazione stimato</span>
          <strong id="summaryPrepTime">—</strong>
        </div>
        <div class="summary-row muted">
          <span>+ Tempo di consegna stimato</span>
          <strong id="summaryTravelTime">—</strong>
        </div>
        <div class="summary-row">
          <span>Consegna stimata tra</span>
          <strong id="summaryTotalTime">—</strong>
        </div>
        <div class="summary-row summary-total">
          <span>Totale</span>
          <strong id="summaryTotalPrice">€0,00</strong>
        </div>
      </div>
    </div>

    <form class="delivery-form" id="deliveryForm" action="checkout" method="POST" novalidate>
      <p class="ticket-eyebrow">Consegna</p>
      <h2>Quando vuoi ricevere l'ordine?</h2>

      <div class="form-field">
        <label for="orario">Orario di consegna <span class="optional">(opzionale, default: il prima possibile)</span></label>
        <input type="datetime-local" id="orario" name="orario">
        <p class="field-hint" id="orarioHint">Calcolo la prima fascia disponibile…</p>
        <p class="field-error" id="orarioError" role="alert"></p>
      </div>

      <div class="delivery-address" id="deliveryAddress">
        <p class="ticket-eyebrow">Consegna presso</p>
        <#if utente??>
          <p id="addressLine">${utente.indirizzo}</p>
        <#else>
          <p id="addressLine">Devi effettuare l'<a href="login">accesso</a> per confermare l'ordine.</p>
        </#if>
      </div>

      <button type="submit" class="btn btn-stamp btn-full" id="confirmBtn">Conferma ordine</button>
      <p class="field-hint">Riceverai una email di riepilogo e un aggiornamento quando l'ordine sarà in consegna.</p>
    </form>
  </section>

  </#if>

</main>

<footer class="site-footer">
  <p>Progetto didattico realizzato per il corso di Web Engineering — Univaq Food Delivery™ è un'attività fittizia.</p>
</footer>

<script>
    <#if utente??>
    const LOGGED_USER = { id: ${utente.id}, nome: "${utente.nome?js_string}" };
    <#else>
    const LOGGED_USER = null;
    </#if>

    // Iniezione dinamica dei dati dal database per il calcolo dei totali
    const prodotti = [
        <#list prodotti as p>
        {
            id: ${p.id},
            nome: "${p.nome?js_string}",
            prezzo_base: ${p.prezzoBase?c},
            tempo_preparazione: ${p.tempoPreparazione},
            gruppi_caratteristiche: [
                <#list p.gruppi as g>
                {
                    id: ${g.id},
                    nome: "${g.nome?js_string}",
                    opzioni: [
                        <#list g.opzioni as c>
                        {
                            id: ${c.id},
                            nome: "${c.nome?js_string}",
                            differenza_prezzo: ${c.differenzaPrezzo?c}
                        }<#sep>,</#sep>
                        </#list>
                    ]
                }<#sep>,</#sep>
                </#list>
            ]
        }<#sep>,</#sep>
        </#list>
    ];
</script>
<script src="js/checkout.js"></script>
</body>
</html>