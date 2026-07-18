<#import "base.ftl" as layout>
<#assign scriptTag = "<script>const STATI_ORDINE = ['Inserito', 'In preparazione', 'Pronto', 'In consegna', 'Consegnato'];</script><script src='js/orders.js'></script>">
<@layout.page title="I tuoi ordini — Univaq Food Delivery" customCss="<link rel=\"stylesheet\" href=\"css/orders.css\">" customScripts=scriptTag>

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

</@layout.page>