<#import "base.ftl" as layout>

<@layout.page title="Menu — Univaq Food Delivery" customScripts="<script src='js/menu.js'></script>">

<section class="hero">
  <div class="hero-ticket">
    <div class="ticket-hole"></div>
    <p class="ticket-eyebrow">Ordini aperti</p>
    <h1>La cucina universitaria<br>consegnata a casa tua.</h1>
    <p class="hero-sub">Piatti preparati al momento dal nostro forno a legna. Scegli, componi, ricevi una stima del tempo prima ancora di confermare.</p>
    <a href="#menu" class="btn btn-stamp">Vai al menu ↓</a>
    <div class="ticket-tear" aria-hidden="true"></div>
  </div>
</section>

<main>
<section class="menu-section" id="menu">
  <div class="section-head">
    <h2>Il Menu</h2>
    <p>Ordini aggiornati in cucina in tempo reale. Tocca un piatto per personalizzarlo.</p>
  </div>

  <nav class="category-tabs" id="categoryTabs" aria-label="Filtra per categoria">
    <button data-cat="Tutte" class="active">Tutte</button>
    <#list categorie as cat>
      <button data-cat="${cat}">${cat}</button>
    </#list>
  </nav>

  <div class="menu-grid" id="menuGrid">
    <#list prodotti as p>
    <article class="product-card" data-id="${p.id}" data-cat="${p.categoria}">
      <button type="button" class="product-open" data-id="${p.id}">
        <span class="visually-hidden">Apri dettagli di ${p.nome}, €${p.prezzoBase}</span>
      </button>
      <div class="product-img">
        <#if p.immagine?? && p.immagine?trim != "">
          <img src="img/${p.immagine}" alt="${p.nome}">
        <#else>
          Nessuna immagine
        </#if>
      </div>
      <h3 class="product-name">${p.nome}</h3>
      <p class="product-desc">${p.descrizione}</p>
      <span class="product-hint">Tocca per personalizzare →</span>
      <form class="product-foot" method="post" action="carrello/aggiungi">
        <input type="hidden" name="prodotto_id" value="${p.id}">
        <span class="product-price">€${p.prezzoBase?string("0.00")}</span>
        <button type="submit" class="btn btn-stamp btn-small">Aggiungi al carrello</button>
      </form>
    </article>
    </#list>
  </div>
</section>

<section class="about-section" id="chi-siamo">
  <div class="about-inner">
    <h2>Chi siamo</h2>
    <p>Univaq Food Delivery nasce nel 2026 come piccola realtà per far fronte alle esigenze culinarie degli studenti universitari fuori sede. Piatti semplici, gustosi ed economici consegnati direttamente alla tua porta di casa, caldi come al tavolo.</p>
    <dl class="about-facts">
      <div><dt>Orari</dt><dd>Tutti i giorni<br> 12:00–15:00 <br> 19:00–23:00</dd></div>
      <div><dt>Zona di consegna</dt><dd>Entro 6&nbsp;km dal locale</dd></div>
      <div><dt>Tempo medio di consegna</dt><dd>25–40 minuti</dd></div>
    </dl>
  </div>
</section>

<section class="location-section" id="dove-siamo">
  <div class="about-inner">
    <h2>Dove siamo</h2>
    <address>Via Vetoio — 67100 L'Aquila</address>
  </div>
</section>
</main>

</@layout.page>