const $ = (id) => document.getElementById(id);

const fields = [
  "firstName", "lastName", "role", "phone", "email", "address", "portfolio",
  "summary", "skills", "education", "jobs", "fontFamily", "fontSize", "lineHeight", "leftOffset", "rightOffset",
  "sidebarColor",
  "photoPosX", "photoPosY",
  "optPhoto", "optIcons", "optPortfolio", "optSkills", "optEducation", "optSummary"
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

    return `<article class="job"><p class="pos">${escapeHtml(position || "Stanowisko")}</p><div class="meta">${escapeHtml(meta || "Firma | Daty")}</div><ul>${bullets.map((item) => `<li>${escapeHtml(item)}</li>`).join("")}</ul></article>`;
  }).join("");
}

function toggleElement(id, shouldShow) {
  $(id).classList.toggle("hidden", !shouldShow);
}

function setPhoto(dataUrl) {
  const img = $("photoImg");
  const placeholder = $("photoPlaceholder");
  if (dataUrl) {
    img.src = dataUrl;
    img.style.display = "block";
    placeholder.style.display = "none";
  } else {
    img.removeAttribute("src");
    img.style.display = "none";
    placeholder.style.display = "block";
  }
}

function sync() {
  $("vFirstName").textContent = $("firstName").value.trim() || "IMIĘ";
  $("vLastName").textContent = $("lastName").value.trim() || "NAZWISKO";
  $("vRole").textContent = $("role").value.trim() || "Stanowisko";

  $("vPhone").textContent = $("phone").value.trim();
  $("vEmail").textContent = $("email").value.trim();
  $("vAddress").textContent = $("address").value.trim();
  $("vPortfolio").textContent = $("portfolio").value.trim();

  $("vSummary").textContent = $("summary").value.trim();
  $("vSkills").innerHTML = renderSkills($("skills").value);
  $("vEducation").innerHTML = renderEducation($("education").value);
  $("vJobs").innerHTML = renderJobs($("jobs").value);
  const family = $("fontFamily").value;
  const size = $("fontSize").value;
  const lineHeight = $("lineHeight").value;
  const leftOffset = $("leftOffset").value;
  const rightOffset = $("rightOffset").value;
  document.documentElement.style.setProperty("--cv-font-family", `'${family}', system-ui, sans-serif`);
  document.documentElement.style.setProperty("--base-font-size", `${size}px`);
  document.documentElement.style.setProperty("--base-line-height", lineHeight);
  document.documentElement.style.setProperty("--left-offset", `${leftOffset}px`);
  document.documentElement.style.setProperty("--right-offset", `${rightOffset}px`);
  $("fontSizeValue").textContent = `${size}px`;
  $("lineHeightValue").textContent = lineHeight;
  $("leftOffsetValue").textContent = `${leftOffset}px`;
  $("rightOffsetValue").textContent = `${rightOffset}px`;

  document.documentElement.style.setProperty("--sidebar-solid", $("sidebarColor").value);
  document.documentElement.style.setProperty("--right-bg", "#ffffff");
  document.documentElement.style.setProperty("--right-text", "#111827");
  document.documentElement.style.setProperty("--main-ink", "#111827");
  document.documentElement.style.setProperty("--right-muted", "#4b5563");
  document.documentElement.style.setProperty("--main-muted", "#4b5563");

  const posX = $("photoPosX").value;
  const posY = $("photoPosY").value;
  $("photoImg").style.objectPosition = `${posX}% ${posY}%`;
  $("photoPosXValue").textContent = `${posX}%`;
  $("photoPosYValue").textContent = `${posY}%`;

  document.querySelectorAll("[data-icon]").forEach((icon) => {
    icon.classList.toggle("hidden", !$("optIcons").checked);
  });

  toggleElement("photoBox", $("optPhoto").checked);
  toggleElement("portfolioRow", $("optPortfolio").checked);
  toggleElement("skillsSection", $("optSkills").checked);
  toggleElement("educationSection", $("optEducation").checked);
  toggleElement("summarySection", $("optSummary").checked);
}

fields.forEach((id) => {
  $(id).addEventListener("input", sync);
  $(id).addEventListener("change", sync);
});

$("photoInput").addEventListener("change", (event) => {
  const file = event.target.files?.[0];
  if (!file) return;
  if (!file.type.startsWith("image/")) {
    alert("Wybierz poprawny plik graficzny.");
    event.target.value = "";
    return;
  }
  const reader = new FileReader();
  reader.onload = () => setPhoto(reader.result);
  reader.readAsDataURL(file);
});

$("removePhoto").addEventListener("click", () => {
  $("photoInput").value = "";
  setPhoto(null);
});

function printCv() {
  sync();
  requestAnimationFrame(() => window.print());
}

const themePresets = {
  1: { sidebar: "#111827", accent: "#111827" },
  2: { sidebar: "#0f766e", accent: "#0f766e" },
  3: { sidebar: "#7c2d12", accent: "#7c2d12" }
};

function applyPreset(presetId) {
  const preset = themePresets[presetId] || themePresets[1];
  $("sidebarColor").value = preset.sidebar;
  document.documentElement.style.setProperty("--accent", preset.accent);

  ["themePreset1", "themePreset2", "themePreset3"].forEach((id, idx) => {
    $(id).classList.toggle("active", idx + 1 === presetId);
  });
  sync();
}

function applyTheme(themeName) {
  const isLight = themeName === "light";
  const page = $("cvPage");
  page.setAttribute("data-theme", isLight ? "light" : "dark");
  document.body.setAttribute("data-ui-theme", isLight ? "light" : "dark");

  if (isLight) {
    $("sidebarColor").value = "#3b4256";
  } else {
    $("sidebarColor").value = "#111827";
  }

  $("themeDarkBtn").classList.toggle("active", !isLight);
  $("themeLightBtn").classList.toggle("active", isLight);
  applyPreset(1);
  sync();
}

$("printBtn").addEventListener("click", printCv);
$("pdfBtn").addEventListener("click", printCv);
$("themeDarkBtn").addEventListener("click", () => applyTheme("dark"));
$("themeLightBtn").addEventListener("click", () => applyTheme("light"));
$("themePreset1").addEventListener("click", () => applyPreset(1));
$("themePreset2").addEventListener("click", () => applyPreset(2));
$("themePreset3").addEventListener("click", () => applyPreset(3));

applyTheme("dark");
