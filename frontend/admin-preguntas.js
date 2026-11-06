(() => {
  const STORAGE_KEY = "jobready-us02-question-prototype-v1";
  const initialQuestions = [
    {
      id: "demo-1",
      text: "¿Cómo diseñarías una API REST para que sea segura y fácil de mantener?",
      specialty: "Ingeniería de Software",
      category: "Arquitectura y diseño",
      seniority: "Intermedio",
      videoUrl: "https://videos.jobready.example/guias/api-rest",
      active: true,
      demo: true
    },
    {
      id: "demo-2",
      text: "Cuéntame cómo identificarías y optimizarías una consulta lenta en una base de datos.",
      specialty: "Sistemas y Cómputo",
      category: "Bases de datos",
      seniority: "Senior",
      videoUrl: "https://videos.jobready.example/guias/consultas-sql",
      active: true,
      demo: true
    },
    {
      id: "demo-3",
      text: "¿Qué pasos seguirías para diagnosticar un error que solo ocurre en producción?",
      specialty: "Ciencias de la Computación",
      category: "Resolución de problemas",
      seniority: "Junior",
      videoUrl: "https://videos.jobready.example/guias/diagnostico",
      active: true,
      demo: true
    }
  ];

  const form = document.querySelector("#question-form");
  const formDialog = document.querySelector("#question-dialog");
  const specialtyInput = document.querySelector("#specialty");
  const categoryInput = document.querySelector("#category");
  const questionInput = document.querySelector("#question-text");
  const seniorityInput = document.querySelector("#seniority");
  const videoInput = document.querySelector("#video-url");
  const list = document.querySelector("#question-list");
  const searchInput = document.querySelector("#search-input");
  const specialtyFilter = document.querySelector("#filter-specialty");
  const emptyState = document.querySelector("#empty-state");
  const toast = document.querySelector("#toast");
  let toastTimer;
  let questions = loadQuestions();

  function loadQuestions() {
    try {
      const saved = JSON.parse(localStorage.getItem(STORAGE_KEY));
      return Array.isArray(saved) ? saved : initialQuestions.map((item) => ({ ...item }));
    } catch {
      return initialQuestions.map((item) => ({ ...item }));
    }
  }

  function saveQuestions() {
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(questions));
      return true;
    } catch {
      return false;
    }
  }

  function escapeHtml(value) {
    return String(value).replace(/[&<>"']/g, (character) => ({
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;"
    })[character]);
  }

  function safeUrl(value) {
    try {
      const parsed = new URL(value);
      return parsed.protocol === "https:" ? parsed.href : "";
    } catch {
      return "";
    }
  }

  function updatePreview() {
    document.querySelector("#preview-specialty").textContent = specialtyInput.value || "Especialidad";
    document.querySelector("#preview-category").textContent = categoryInput.value || "Categoría";
    document.querySelector("#preview-question").textContent = questionInput.value.trim() || "Tu pregunta aparecerá aquí.";
    document.querySelector("#preview-level").textContent = seniorityInput.value || "Nivel";
    document.querySelector("#char-count").textContent = questionInput.value.length;

    const hasUrl = Boolean(safeUrl(videoInput.value.trim()));
    const videoPreview = document.querySelector("#video-preview");
    videoPreview.classList.toggle("has-video", hasUrl);
    document.querySelector("#video-preview-status").textContent = hasUrl ? "Enlace listo" : "Requiere enlace";
    document.querySelector("#url-check").parentElement.classList.toggle("valid", hasUrl);
  }

  function refreshSpecialtyFilter() {
    const selected = specialtyFilter.value;
    const specialties = [...new Set(questions.map((item) => item.specialty))]
      .sort((a, b) => a.localeCompare(b, "es"));
    specialtyFilter.innerHTML = '<option value="">Todas las especialidades</option>' + specialties
      .map((name) => '<option value="' + escapeHtml(name) + '">' + escapeHtml(name) + "</option>")
      .join("");
    if (specialties.includes(selected)) specialtyFilter.value = selected;
  }

  function renderQuestions() {
    refreshSpecialtyFilter();
    const query = searchInput.value.trim().toLocaleLowerCase("es");
    const specialty = specialtyFilter.value;
    const visible = questions.filter((item) => {
      const searchable = (item.text + " " + item.category + " " + item.specialty + " " + item.seniority).toLocaleLowerCase("es");
      return searchable.includes(query) && (!specialty || item.specialty === specialty);
    });

    list.innerHTML = visible.map((item, index) => (
      '<article class="question-card" role="listitem">' +
        '<span class="question-number" aria-hidden="true">' + String(index + 1).padStart(2, "0") + "</span>" +
        '<div class="question-main">' +
          '<div class="question-meta">' +
            '<span class="specialty-tag"><i class="specialty-dot"></i>' + escapeHtml(item.specialty) + "</span>" +
            '<span class="category-tag">' + escapeHtml(item.category) + "</span>" +
          "</div>" +
          "<h3>" + escapeHtml(item.text) + "</h3>" +
          '<div class="question-footer">' +
            '<span class="seniority-tag">' + escapeHtml(item.seniority) + "</span>" +
            (item.active ? '<span class="status-active"><i></i>Activa</span>' : "") +
          "</div>" +
        "</div>" +
        '<div class="question-actions"><button class="video-link" type="button" data-video-id="' + escapeHtml(item.id) +
          '" aria-label="Ver video guía de ' + escapeHtml(item.specialty) + '">' +
          '<svg viewBox="0 0 24 24" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="3"/><path d="m10 9 5 3-5 3z"/></svg>' +
          "Video guía</button></div>" +
      "</article>"
    )).join("");

    const activeCount = questions.filter((item) => item.active).length;
    const videoCount = questions.filter((item) => Boolean(safeUrl(item.videoUrl))).length;
    const specialtyCount = new Set(questions.map((item) => item.specialty)).size;
    document.querySelector("#stat-questions").textContent = activeCount;
    document.querySelector("#stat-specialties").textContent = specialtyCount;
    document.querySelector("#stat-videos").textContent = videoCount;
    document.querySelector("#sidebar-count").textContent = activeCount;
    document.querySelector("#catalog-count").textContent = questions.length;
    document.querySelector("#results-label").textContent = visible.length + (visible.length === 1 ? " pregunta" : " preguntas");
    emptyState.hidden = visible.length > 0;
    list.hidden = visible.length === 0;
  }

  const errorMap = [
    [specialtyInput, "specialty-error", "Selecciona una especialidad."],
    [categoryInput, "category-error", "Selecciona una categoría técnica."],
    [questionInput, "question-error", "Escribe el enunciado de la pregunta."],
    [seniorityInput, "seniority-error", "Selecciona el nivel de seniority."],
    [videoInput, "video-error", "El video de la pregunta es obligatorio."]
  ];

  function clearValidation() {
    errorMap.forEach(([input, errorId]) => {
      input.removeAttribute("aria-invalid");
      document.getElementById(errorId).textContent = "";
    });
    videoInput.setCustomValidity("");
  }

  function validateForm() {
    clearValidation();
    let firstInvalid = null;
    errorMap.forEach(([input, errorId, message]) => {
      let error = "";
      const value = input.value.trim();
      if (!value) {
        error = input === videoInput ? "El video de la pregunta es obligatorio." : message;
      } else if (input === questionInput && value.length < 12) {
        error = "Escribe una pregunta de al menos 12 caracteres.";
      } else if (input === videoInput && !safeUrl(value)) {
        error = "Ingresa un enlace válido que comience con https://.";
      }

      if (error) {
        input.setAttribute("aria-invalid", "true");
        document.getElementById(errorId).textContent = error;
        if (!firstInvalid) firstInvalid = input;
      }
    });
    if (firstInvalid) firstInvalid.focus();
    return !firstInvalid;
  }

  function announce(title, message, isError) {
    toast.classList.toggle("toast-error", Boolean(isError));
    document.querySelector(".toast-copy strong").textContent = title;
    document.querySelector(".toast-copy small").textContent = message;
    toast.classList.add("show");
    window.clearTimeout(toastTimer);
    toastTimer = window.setTimeout(() => toast.classList.remove("show"), 4200);
  }

  document.querySelector("#open-question-dialog").addEventListener("click", () => {
    formDialog.showModal();
    window.setTimeout(() => specialtyInput.focus(), 40);
  });

  document.querySelector("#close-question-dialog").addEventListener("click", () => formDialog.close());
  formDialog.addEventListener("click", (event) => {
    if (event.target === formDialog) formDialog.close();
  });

  form.addEventListener("submit", (event) => {
    event.preventDefault();
    if (!validateForm()) return;

    const item = {
      id: "question-" + Date.now(),
      text: questionInput.value.trim(),
      specialty: specialtyInput.value,
      category: categoryInput.value,
      seniority: seniorityInput.value,
      videoUrl: safeUrl(videoInput.value.trim()),
      active: true,
      demo: false
    };
    questions = [item, ...questions];
    const saved = saveQuestions();
    form.reset();
    clearValidation();
    updatePreview();
    formDialog.close();
    renderQuestions();
    announce(
      saved ? "Pregunta guardada" : "Pregunta agregada",
      saved ? "Ya está en tu banco." : "Se conservará mientras esta página esté abierta.",
      !saved
    );
    document.querySelector("#catalogo").scrollIntoView({ behavior: "smooth", block: "start" });
  });

  form.addEventListener("reset", () => {
    window.setTimeout(() => {
      clearValidation();
      updatePreview();
    }, 0);
  });

  [specialtyInput, categoryInput, questionInput, seniorityInput, videoInput].forEach((input) => {
    input.addEventListener("input", () => {
      input.removeAttribute("aria-invalid");
      const pair = errorMap.find(([candidate]) => candidate === input);
      if (pair) document.getElementById(pair[1]).textContent = "";
      updatePreview();
    });
    input.addEventListener("change", updatePreview);
  });

  searchInput.addEventListener("input", renderQuestions);
  specialtyFilter.addEventListener("change", renderQuestions);
  document.querySelector("#clear-search").addEventListener("click", () => {
    searchInput.value = "";
    specialtyFilter.value = "";
    renderQuestions();
    searchInput.focus();
  });

  list.addEventListener("click", (event) => {
    const videoButton = event.target.closest("[data-video-id]");
    if (!videoButton) return;
    const item = questions.find((question) => question.id === videoButton.dataset.videoId);
    if (!item) return;
    const url = safeUrl(item.videoUrl);
    if (!url) return;
    const demoLink = new URL(url).hostname.endsWith(".example");
    const openLink = document.querySelector("#dialog-open-link");
    openLink.href = url;
    openLink.hidden = demoLink;
    document.querySelector("#video-dialog-title").textContent = demoLink ? "Video de muestra" : "Video asociado";
    document.querySelector("#dialog-url").textContent = demoLink
      ? "Este registro usa un enlace de ejemplo."
      : url;
    document.querySelector("#video-dialog").showModal();
  });

  document.querySelector("#reset-demo").addEventListener("click", () => {
    questions = initialQuestions.map((item) => ({ ...item }));
    const saved = saveQuestions();
    searchInput.value = "";
    specialtyFilter.value = "";
    form.reset();
    renderQuestions();
    announce(
      saved ? "Ejemplo restablecido" : "Ejemplo reiniciado",
      saved ? "Volvieron las preguntas de muestra." : "El cambio es temporal en esta sesión.",
      !saved
    );
  });

  document.querySelector("#toast-close").addEventListener("click", () => toast.classList.remove("show"));
  document.querySelector("#video-dialog").addEventListener("click", (event) => {
    if (event.target === event.currentTarget) event.currentTarget.close();
  });

  const menuToggle = document.querySelector("#menu-toggle");
  const sidebar = document.querySelector("#sidebar");
  menuToggle.addEventListener("click", () => {
    const isOpen = menuToggle.getAttribute("aria-expanded") === "true";
    menuToggle.setAttribute("aria-expanded", String(!isOpen));
    menuToggle.setAttribute("aria-label", isOpen ? "Abrir navegación" : "Cerrar navegación");
    sidebar.classList.toggle("open", !isOpen);
  });
  sidebar.addEventListener("click", (event) => {
    if (event.target.closest(".side-link") && window.innerWidth <= 850) {
      sidebar.classList.remove("open");
      menuToggle.setAttribute("aria-expanded", "false");
      menuToggle.setAttribute("aria-label", "Abrir navegación");
    }
  });
  document.addEventListener("click", (event) => {
    if (window.innerWidth <= 850 && sidebar.classList.contains("open") &&
        !sidebar.contains(event.target) && !menuToggle.contains(event.target)) {
      sidebar.classList.remove("open");
      menuToggle.setAttribute("aria-expanded", "false");
      menuToggle.setAttribute("aria-label", "Abrir navegación");
    }
  });

  updatePreview();
  renderQuestions();
})();
