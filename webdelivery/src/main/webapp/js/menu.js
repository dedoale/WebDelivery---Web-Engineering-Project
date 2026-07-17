(function () {
  "use strict";

  // il carrello viene salvato in localStorage
  const CART_KEY = "wd_cart";
  function loadCart() {
    try {
      return JSON.parse(localStorage.getItem(CART_KEY)) || [];
    } catch (e) { return []; }
  }
  function saveCart() {
    localStorage.setItem(CART_KEY, JSON.stringify(cart));
  }

  let cart = loadCart();
  let activeCategory = "Tutte";
  let currentProduct = null;
  let currentSelection = {};
  let currentQty = 1;
  let MENU_DATA = { categorie: [], prodotti: [] };

  const fmt = (n) => "€" + n.toFixed(2).replace(".", ",");

  // render categorie
  function initTabs() {
    const tabs = document.querySelectorAll("#categoryTabs button");
    const cards = document.querySelectorAll(".product-card");

    tabs.forEach(btn => {
      btn.addEventListener("click", () => {
        // aggiorna stile bottone
        tabs.forEach(b => b.classList.remove("active"));
        btn.classList.add("active");

        // filtro prodotti nella griglia
        const selectedCat = btn.dataset.cat;
        cards.forEach(card => {
          if (selectedCat === "Tutte" || card.dataset.cat === selectedCat) {
            card.style.display = ""; // Mostra
          } else {
            card.style.display = "none"; // Nascondi
          }
        });
      });
    });
  }

  // render griglia prodotti
  function renderGrid() {
    const grid = document.getElementById("menuGrid");
    
    grid.querySelectorAll(".product-card").forEach(card => {
      const idProdotto = Number(card.dataset.id); 
      
      const open = () => openModal(idProdotto);
      
      card.addEventListener("click", open);
      card.addEventListener("keydown", (e) => { if (e.key === "Enter") open(); });
    });
  }

  // modale prodotto
  function openModal(id) {
    currentProduct = MENU_DATA.prodotti.find(p => p.id === id);
    if (!currentProduct) return;

    currentQty = 1;
    currentSelection = {};

    currentProduct.gruppi.forEach((g, gi) => {
      const defaults = g.opzioni.filter(o => o.is_default).map(o => o.id);
      currentSelection[gi] = defaults;
    });

    document.getElementById("modalTitle").textContent = currentProduct.nome;
    document.getElementById("modalDesc").textContent = currentProduct.descrizione;
    
    const imgWrap = document.getElementById("modalImg");
    if (currentProduct.immagine && currentProduct.immagine.trim() !== "") {
        imgWrap.innerHTML = `<img src="${currentProduct.immagine}" alt="${currentProduct.nome}">`;
        imgWrap.style.display = "";
    } else {
        imgWrap.innerHTML = "";
        imgWrap.style.display = "none";
    }
    
    document.getElementById("qtyValue").textContent = currentQty;

    renderGroups();
    updateModalPrice();

    document.getElementById("modalBackdrop").classList.add("open");
  }

  function renderGroups() {
    const wrap = document.getElementById("modalGroups");
    wrap.innerHTML = currentProduct.gruppi.map((g, gi) => `
      <div class="char-group" data-gi="${gi}">
        <h4>${g.nome} ${g.mutua_esclusione ? "" : `<span class="hint">selezione multipla</span>`}</h4>
        ${g.opzioni.map(o => `
          <div class="char-option">
            <label>
              <input type="${g.mutua_esclusione ? "radio" : "checkbox"}"
                     name="grp-${gi}" value="${o.id}"
                     ${currentSelection[gi].includes(o.id) ? "checked" : ""}>
              ${o.nome}
            </label>
            <span class="diff">${o.differenza_prezzo === 0 ? "" : (o.differenza_prezzo > 0 ? "+" : "") + fmt(o.differenza_prezzo)}</span>
          </div>
        `).join("")}
      </div>
    `).join("");

    wrap.querySelectorAll("input").forEach(input => {
      input.addEventListener("change", () => {
        const gi = Number(input.closest(".char-group").dataset.gi);
        const group = currentProduct.gruppi[gi];
        if (group.mutua_esclusione) {
          currentSelection[gi] = [input.value];
        } else {
          const set = new Set(currentSelection[gi]);
          input.checked ? set.add(input.value) : set.delete(input.value);
          currentSelection[gi] = [...set];
        }
        updateModalPrice();
      });
    });
  }

  function computeUnitPrice() {
    let price = currentProduct.prezzo_base;
    currentProduct.gruppi.forEach((g, gi) => {
      g.opzioni.forEach(o => {
        if (currentSelection[gi].includes(o.id)) price += o.differenza_prezzo;
      });
    });
    return price;
  }

  function updateModalPrice() {
    const unit = computeUnitPrice();
    document.getElementById("modalPrice").textContent = fmt(unit * currentQty);
  }

  function closeModal() {
    document.getElementById("modalBackdrop").classList.remove("open");
    currentProduct = null;
  }

  document.getElementById("modalClose").addEventListener("click", closeModal);
  document.getElementById("modalBackdrop").addEventListener("click", (e) => {
    if (e.target.id === "modalBackdrop") closeModal();
  });

  document.getElementById("qtyMinus").addEventListener("click", () => {
    currentQty = Math.max(1, currentQty - 1);
    document.getElementById("qtyValue").textContent = currentQty;
    updateModalPrice();
  });
  document.getElementById("qtyPlus").addEventListener("click", () => {
    currentQty += 1;
    document.getElementById("qtyValue").textContent = currentQty;
    updateModalPrice();
  });

  document.getElementById("addToCart").addEventListener("click", () => {
    const opzioniScelte = [];
    currentProduct.gruppi.forEach((g, gi) => {
      g.opzioni.forEach(o => {
        if (currentSelection[gi].includes(o.id)) opzioniScelte.push(o);
      });
    });
    cart.push({
      lineId: Date.now() + Math.random(),
      prodotto: currentProduct,
      opzioniScelte,
      qty: currentQty,
      prezzoUnitario: computeUnitPrice()
    });
    saveCart();
    renderCart();
    closeModal();
    openCart();
  });

  // carrello
  function renderCart() {
    const list = document.getElementById("cartList");
    const empty = document.getElementById("cartEmpty");
    const count = document.getElementById("cartCount");

    count.textContent = cart.reduce((s, l) => s + l.qty, 0);

    if (cart.length === 0) {
      list.innerHTML = "";
      empty.style.display = "block";
    } else {
      empty.style.display = "none";
      list.innerHTML = cart.map(line => `
        <li class="cart-item" data-line="${line.lineId}">
          <div class="cart-item-top">
            <span>${line.qty} × ${line.prodotto.nome}</span>
            <span>${fmt(line.prezzoUnitario * line.qty)}</span>
          </div>
          ${line.opzioniScelte.length ? `<div class="cart-item-opts">${line.opzioniScelte.map(o => o.nome).join(", ")}</div>` : ""}
          <button class="cart-item-remove" data-remove="${line.lineId}">Rimuovi</button>
        </li>
      `).join("");

      list.querySelectorAll("[data-remove]").forEach(btn => {
        btn.addEventListener("click", () => {
          const id = Number(btn.dataset.remove);
          cart = cart.filter(l => l.lineId !== id);
          saveCart();
          renderCart();
        });
      });
    }

    const totale = cart.reduce((s, l) => s + l.prezzoUnitario * l.qty, 0);
    const tempoStimato = cart.reduce((s, l) => s + l.prodotto.tempo_preparazione * l.qty, 0);
    document.getElementById("cartTotal").textContent = fmt(totale);
    document.getElementById("cartTime").textContent = cart.length ? `~${tempoStimato} min` : "—";
  }

  function openCart() {
    document.getElementById("cartDrawer").classList.add("open");
    document.getElementById("cartScrim").classList.add("open");
    document.getElementById("cartToggle").setAttribute("aria-expanded", "true");
  }
  function closeCart() {
    document.getElementById("cartDrawer").classList.remove("open");
    document.getElementById("cartScrim").classList.remove("open");
    document.getElementById("cartToggle").setAttribute("aria-expanded", "false");
  }

  document.getElementById("cartToggle").addEventListener("click", openCart);
  document.getElementById("cartClose").addEventListener("click", closeCart);
  document.getElementById("cartScrim").addEventListener("click", closeCart);

  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") { closeModal(); closeCart(); }
  });

  // init: la griglia e i tab sono già renderizzati server-side da Freemarker,
  // qui carichiamo solo i dati completi dei prodotti (serve per il modale) via fetch
  function init() {
    fetch("api/prodotti")
      .then(res => res.json())
      .then(data => {
        MENU_DATA = data;
        initTabs();
        renderGrid();
        renderCart();
      })
      .catch(err => {
        console.error("Impossibile caricare i dati del menu:", err);
        // fallback: la griglia server-side resta comunque visibile,
        // ma il modale di dettaglio/personalizzazione non funzionerà
        initTabs();
        renderCart();
      });
  }

  init();
})();