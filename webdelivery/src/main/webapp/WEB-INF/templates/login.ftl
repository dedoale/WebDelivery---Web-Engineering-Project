<#import "base.ftl" as layout>
<@layout.page title="Accedi — Univaq Food Delivery" customCss="<link rel=\"stylesheet\" href=\"css/auth.css\">" customScripts="<script src='js/auth.js'></script>">

<main class="auth-main">
  <section class="auth-ticket">
    <p class="ticket-eyebrow">Accesso cliente</p>
    <h1>Bentornato</h1>
    <p class="auth-sub">Accedi per completare un ordine o rivedere lo storico dei tuoi acquisti.</p>

    <#if error??>
      <div class="field-error" style="display:block; margin-bottom:1rem; font-weight:bold; color:var(--stamp-red);">${error}</div>
    </#if>

    <!-- form action + method POST -->
    <form class="auth-form" action="login" method="post" id="loginForm" novalidate>
      <div class="form-field">
        <label for="email">Email</label>
        <input type="email" id="email" name="email" required autocomplete="username" placeholder="mario.rossi@email.it">
        <p class="field-error" id="emailError" role="alert"></p>
      </div>

      <div class="form-field">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required minlength="8" autocomplete="current-password">
        
        <label class="checkbox-line">
          <input type="checkbox" id="togglePwCheckbox"> Mostra password
        </label>
        
        <p class="field-error" id="passwordError" role="alert"></p>
      </div>

      <div class="form-row">
        <label class="checkbox-line">
          <input type="checkbox" name="ricordami"> Ricordami su questo dispositivo
        </label>
        <a href="#" class="link-muted">Password dimenticata?</a>
      </div>

      <button type="submit" class="btn btn-stamp btn-full">Accedi</button>

      <p class="auth-switch">Non hai un account? <a href="registrazione">Registrati come nuovo cliente</a></p>
    </form>

    <div class="ticket-tear" aria-hidden="true"></div>
  </section>
</main>

</@layout.page>