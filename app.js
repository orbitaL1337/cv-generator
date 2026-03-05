const $ = (id) => document.getElementById(id);

const fields = [
  "firstName", "lastName", "role", "phone", "email", "address", "linkedin",
  "summary", "skills", "education", "jobs", "rodo", "fontFamily", "fontSize"
];

function escapeHtml(value) {
  return value
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#39;");
}

function renderEducation(raw) {
  const lines = raw.split("\n").map((l) => l.trim()).filter(Boolean);
  return lines.map((line) => {
    const [name, ...rest] = line.split("—").map((part) => part.trim());
    const meta = rest.join(" — ");
    return `<div><div class="name">${escapeHtml(name || "")}</div>${meta ? `<div class="meta">${escapeHtml(meta)}</div>` : ""}</div>`;
  }).join("");
}

function renderSkills(raw) {
  return raw.split("\n")
    .map((l) => l.trim())
    .filter(Boolean)
    .map((l) => `<li>${escapeHtml(l)}</li>`)
    .join("");
}

function renderJobs(raw) {
  const blocks = raw.split(/\n\s*\n/g).map((b) => b.trim()).filter(Boolean);

  return blocks.map((block) => {
    const lines = block.split("\n").map((l) => l.trim()).filter(Boolean);
    const [header, ...bullets] = lines;
    const [position, ...metaParts] = (header || "").split("—").map((p) => p.trim());
    const meta = metaParts.join(" — ");

    return `
      <article class="job">
        <p class="pos">${escapeHtml(position || "Job Position")}</p>
        <div class="meta">${escapeHtml(meta || "Company | Date")}</div>
        <ul>${bullets.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul>
      </article>
    `;
  }).join("");
}

function sync() {
  $("vFirstName").textContent = $("firstName").value.trim() || "IMIĘ";
  $("vLastName").textContent = $("lastName").value.trim() || "NAZWISKO";
  $("vRole").textContent = $("role").value.trim() || "Stanowisko";

  $("vPhone").textContent = $("phone").value.trim();
  $("vEmail").textContent = $("email").value.trim();
  $("vAddress").textContent = $("address").value.trim();
  $("vLinkedin").textContent = $("linkedin").value.trim();

  $("vSummary").textContent = $("summary").value.trim();
  $("vSkills").innerHTML = renderSkills($("skills").value);
  $("vEducation").innerHTML = renderEducation($("education").value);
  $("vJobs").innerHTML = renderJobs($("jobs").value);
  $("vRodo").textContent = $("rodo").value.trim();

  const family = $("fontFamily").value;
  const size = $("fontSize").value;
  document.documentElement.style.setProperty("--cv-font-family", `'${family}', system-ui, sans-serif`);
  document.documentElement.style.setProperty("--base-font-size", `${size}px`);
  $("fontSizeValue").textContent = `${size}px`;
}

fields.forEach((id) => {
  $(id).addEventListener("input", sync);
  $(id).addEventListener("change", sync);
});

sync();
