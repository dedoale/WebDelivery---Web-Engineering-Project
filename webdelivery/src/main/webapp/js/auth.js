(function () {
  "use strict";

  const messages = {
    valueMissing: "Questo campo è obbligatorio.",
    typeMismatch_email: "Inserisci un indirizzo email valido.",
    tooShort_password: "La password deve avere almeno 8 caratteri.",
    patternMismatch_telefono: "Inserisci un numero di telefono valido (solo cifre, spazi e +)."
  };

  function messageFor(input) {
    const v = input.validity;
    if (v.valueMissing) return messages.valueMissing;
    if (v.typeMismatch && input.type === "email") return messages.typeMismatch_email;
    if (v.tooShort && input.name === "password") return messages.tooShort_password;
    if (v.patternMismatch && input.name === "telefono") return messages.patternMismatch_telefono;
    return "Valore non valido.";
  }

  function wireForm(form) {
    if (!form) return;

    const inputs = form.querySelectorAll("input[required], input[pattern], input[minlength]");

    inputs.forEach(input => {
      input.addEventListener("blur", () => validateField(input));
      input.addEventListener("input", () => {
        if (input.closest(".form-field").querySelector(".field-error").textContent) {
          validateField(input);
        }
      });
    });

    form.addEventListener("submit", (e) => {
      let valid = true;
      inputs.forEach(input => {
        if (!validateField(input)) valid = false;
      });
      if (!valid) {
        e.preventDefault();
        const firstInvalid = form.querySelector(".field-error:not(:empty)");
        if (firstInvalid) firstInvalid.closest(".form-field").querySelector("input").focus();
      }
      // se valid === true, il form procede con il normale submit HTML
      // verso il servlet indicato in action
    });
  }

  function validateField(input) {
    const field = input.closest(".form-field");
    if (!field) return true;
    const errorEl = field.querySelector(".field-error");
    if (!errorEl) return true;

    if (input.checkValidity()) {
      errorEl.textContent = "";
      return true;
    }
    errorEl.textContent = messageFor(input);
    return false;
  }

  wireForm(document.getElementById("loginForm"));
  wireForm(document.getElementById("registerForm"));

  // mostra/nascondi password
  const togglePwCheckbox = document.getElementById("togglePwCheckbox");
  if (togglePwCheckbox) {
    togglePwCheckbox.addEventListener("change", (e) => {
      const pw = document.getElementById("password");
      if (pw) {
        pw.type = e.target.checked ? "text" : "password";
      }
    });
  }
})();