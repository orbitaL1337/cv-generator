const $ = (id) => document.getElementById(id);

function escapeHtml(text) {
  return (text || "").replace(/[&<>"']/g, (ch) => ({
    "&": "&amp;",
    "<": "&lt;",
    ">": "&gt;",
    '"': "&quot;",
    "'": "&#39;"
  }[ch]));
}

function linesToList(text, targetId) {
  const items = (text || "").split("\n").map((x) => x.trim()).filter(Boolean);
  const el = $(targetId);
  el.innerHTML = items.map((item) => `<li>${escapeHtml(item)}</li>`).join("");
}

function renderExperience(text) {
  const blocks = (text || "").trim().split(/\n\s*\n/g).map((b) => b.trim()).filter(Boolean);
  const html = blocks.map((block) => {
    const rows = block.split("\n").map((x) => x.trim()).filter(Boolean);
    const title = rows.shift() || "";
    const points = rows.map((r) => r.replace(/^[-•]\s*/, "")).filter(Boolean);
    return `
      <article class="job">
        <h4>${escapeHtml(title)}</h4>
        <ul>${points.map((p) => `<li>${escapeHtml(p)}</li>`).join("")}</ul>
      </article>
    `;
  }).join("");

  $("vExperience").innerHTML = html;
}

function sync() {
  $("vName").textContent = `${($("firstName").value || "").trim()} ${($("lastName").value || "").trim()}`.trim() || "IMIĘ NAZWISKO";
  $("vRole").textContent = ($("role").value || "Stanowisko").trim();
  $("vSummary").textContent = ($("summary").value || "").trim();

  const contactLines = ($("contact").value || "").split("\n").map((x) => x.trim()).filter(Boolean);
  $("vContact").innerHTML = contactLines.map((line) => `<p>${escapeHtml(line)}</p>`).join("");

  linesToList($("skills").value, "vSkills");
  linesToList($("education").value, "vEducation");
  renderExperience($("experience").value);

  document.documentElement.style.setProperty("--font-size", `${$("fontSize").value}px`);
  document.documentElement.style.setProperty("--name-size", `${$("nameSize").value}px`);
  document.documentElement.style.setProperty("--sidebar", $("sidebarColor").value);
  document.documentElement.style.setProperty("--accent", $("accentColor").value);
}

function loadPhoto(file) {
  if (!file) return;
  const reader = new FileReader();
  reader.onload = (event) => {
    $("cvPhoto").src = event.target?.result || "";
  };
  reader.readAsDataURL(file);
}

function init() {
  [
    "firstName", "lastName", "role", "summary", "contact",
    "skills", "education", "experience", "fontSize", "nameSize",
    "sidebarColor", "accentColor"
  ].forEach((id) => $(id).addEventListener("input", sync));

  $("photoInput").addEventListener("change", (e) => {
    loadPhoto(e.target.files?.[0]);
  });

  $("printBtn").addEventListener("click", () => window.print());

  sync();
}

init();
