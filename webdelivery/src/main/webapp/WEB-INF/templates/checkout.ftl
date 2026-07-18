<#import "base.ftl" as layout>
<#assign scriptTag = "">
<#if !ordineConfermato?? && carrello?? && carrello?size gt 0>
  <#assign scriptTag = "<script src='js/checkout.js'></script>">
</#if>
<@layout.page title="Il tuo ordine — Univaq Food Delivery" customCss="<link rel=\"stylesheet\" href=\"css/checkout.css\">" customScripts=scriptTag>

<main class="checkout-main">

  <#if ordineConfermato??>
    <!-- ordine confermato: mostra i dati salvati nel database -->
    <section class="confirm-state" id="confirmState">
      <div class="confirm-ticket">
        <p class="ticket-eyebrow">Ordine registrato</p>
        <h1>Comanda inviata in cucina!</h1>
        <p id="confirmSummary">Riceverai a breve una email di riepilogo.</p>
        <dl class="confirm-facts">
          <div><dt>Numero ordine</dt><dd>${confermatoOrdine.id}</dd></div>
          <div><dt>Totale</dt><dd>€${confermatoOrdine.prezzoTotale?string("0.00")}</dd></div>
          <div><dt>Orario consegna</dt><dd>${confermatoOrdine.orarioRichiesto.format('HH:mm')}</dd></div>
        </dl>
        <a href="menu" class="btn btn-stamp">Torna alla home</a>
        <a href="ordini" class="btn btn-ghost">Segui il tuo ordine</a>
      </div>
    </section>

  <#elseif !carrello?? || carrello?size == 0>
    <!-- carrello vuoto -->
    <section class="empty-state" id="emptyState">
      <p class="ticket-eyebrow">Comanda vuota</p>
      <h1>Il carrello è vuoto</h1>
      <p>Non hai ancora selezionato nessun piatto.</p>
      <a href="menu#menu" class="btn btn-stamp">Torna al menu</a>
    </section>

  <#else>

  <#if error??>
    <p class="field-error" role="alert" style="margin: 1rem;">${error}</p>
  </#if>

  <!-- composizione ordine: renderizzata interamente lato server -->
  <section class="checkout-layout" id="checkoutLayout">

    <div class="order-summary">
      <p class="ticket-eyebrow">Riepilogo comanda</p>
      <h1>Il tuo ordine</h1>

      <ul class="summary-list" id="summaryList">
        <#list carrello as riga>
          <li class="summary-item">
            <div class="summary-item-top">
              <span>${riga.qty} × ${riga.nome}</span>
              <span>€${(riga.prezzoBaseAlMomento * riga.qty)?string("0.00")}</span>
            </div>
            <#if riga.opzioni?size gt 0>
              <div class="summary-item-opts">${riga.opzioni?join(", ")}</div>
            </#if>
          </li>
        </#list>
      </ul>

      <div class="summary-totals">
        <div class="summary-row">
          <span>Tempo di preparazione stimato</span>
          <strong id="summaryPrepTime">~${tempoPreparazione} min</strong>
        </div>
        <div class="summary-row muted">
          <span>+ Tempo di consegna stimato</span>
          <strong id="summaryTravelTime">~${tempoPercorrenza} min</strong>
        </div>
        <div class="summary-row">
          <span>Consegna stimata tra</span>
          <strong id="summaryTotalTime">~${tempoConsegnaStimato} min</strong>
        </div>
        <div class="summary-row summary-total">
          <span>Totale</span>
          <strong id="summaryTotalPrice">€${totale?string("0.00")}</strong>
        </div>
      </div>
    </div>

    <form class="delivery-form" id="deliveryForm" action="checkout" method="POST" novalidate
          data-tempo-consegna="${tempoConsegnaStimato}">
      <p class="ticket-eyebrow">Consegna</p>
      <h2>Quando vuoi ricevere l'ordine?</h2>

      <div class="form-field">
        <label for="orario">Orario di consegna <span class="optional">(opzionale, default: il prima possibile)</span></label>
        <input type="datetime-local" id="orario" name="orario" value="${orarioDefault.format('yyyy-MM-dd\'T\'HH:mm')}">
        <p class="field-hint" id="orarioHint">Proposto: il prima possibile in base ai tempi di preparazione.</p>
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

</@layout.page>
