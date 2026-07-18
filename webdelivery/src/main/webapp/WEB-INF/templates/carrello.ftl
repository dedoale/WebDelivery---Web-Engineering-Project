<#import "base.ftl" as layout>

<@layout.page title="Il tuo carrello — Univaq Food Delivery">

<main>
<section class="menu-section" id="carrello-page">
  <div class="section-head">
    <h1>Il tuo carrello</h1>
  </div>

  <#if carrello?size == 0>
    <p>Il carrello è vuoto. <a href="menu">Torna al menu</a>.</p>
  <#else>
    <ul class="cart-list">
      <#list carrello as riga>
        <li class="cart-item">
          <div class="cart-item-top">
            <span>${riga.qty} × ${riga.nome}</span>
            <span>€${(riga.prezzoBaseAlMomento * riga.qty)?string("0.00")}</span>
          </div>
          <#if riga.opzioni?size gt 0>
            <div class="cart-item-opts">${riga.opzioni?join(", ")}</div>
          </#if>
          <form method="post" action="carrello/rimuovi">
            <input type="hidden" name="indice" value="${riga?index}">
            <button type="submit" class="cart-item-remove">Rimuovi</button>
          </form>
        </li>
      </#list>
    </ul>

    <div class="cart-drawer-foot">
      <div class="cart-summary">
        <span>Totale</span>
        <span>€${totale?string("0.00")}</span>
      </div>
      <div class="cart-summary cart-summary-time">
        <span>Tempo di preparazione stimato</span>
        <span>~${tempoPreparazione} min</span>
      </div>
      <a href="checkout" class="btn btn-primary cart-checkout">Vai al checkout</a>
    </div>
  </#if>
</section>
</main>

</@layout.page>
