const $ = (id) => document.getElementById(id);


function applyTheme(theme){
  const mode = theme === "light" ? "light" : "dark";
  const isLight = mode === "light";
  document.body.setAttribute("data-theme", mode);

  const icon = $("themeIcon");
  if(icon) icon.textContent = isLight ? "☀️" : "🌙";

  const toggle = $("themeToggle");
  if(toggle){
    toggle.setAttribute("aria-pressed", isLight ? "true" : "false");
    toggle.setAttribute("aria-label", isLight ? "Przełącz na ciemny motyw" : "Przełącz na jasny motyw");
    toggle.setAttribute("title", isLight ? "Przełącz na ciemny motyw" : "Przełącz na jasny motyw");
  }

  window.localStorage.setItem("cv-theme", mode);
}

function initTheme(){
  const saved = window.localStorage.getItem("cv-theme");
  if(saved === "light" || saved === "dark"){
    applyTheme(saved);
    return;
  }
  const prefersLight = window.matchMedia && window.matchMedia("(prefers-color-scheme: light)").matches;
  applyTheme(prefersLight ? "light" : "dark");
}

function toggleTheme(){
  const current = document.body.getAttribute("data-theme") || "dark";
  applyTheme(current === "dark" ? "light" : "dark");
}

function escapeHtml(str){
  return (str || "").replace(/[&<>"']/g, (m) => ({
    "&":"&amp;","<":"&lt;",">":"&gt;",'"':"&quot;","'":"&#039;"
  }[m]));
}

function initials(first, last){
  const a = (first || "").trim().slice(0,1).toUpperCase();
  const b = (last  || "").trim().slice(0,1).toUpperCase();
  return (a + b) || "CV";
}

function setName(){
  const first = ($("firstName").value || "").trim();
  const last  = ($("lastName").value || "").trim();
  const full = [first, last].filter(Boolean).join("  "); // dwa spacje
  $("vName").innerHTML = escapeHtml(full || "Imię  Nazwisko").replace(/  /g, "&nbsp;&nbsp;");
  $("avatarFallback").textContent = initials(first, last);
}

function parseBlocks(text){
  const raw = (text || "").trim();
  if(!raw) return [];
  return raw.split(/\n\s*\n/g).map(b => b.trim()).filter(Boolean);
}

function renderExperience(){
  const blocks = parseBlocks($("experience").value);
  const out = blocks.map(block => {
    const lines = block.split("\n").map(l => l.trim()).filter(Boolean);
    const head = lines.shift() || "";
    const bullets = lines
      .filter(l => l.startsWith("-") || l.startsWith("•"))
      .map(l => l.replace(/^[-•]\s*/, ""));

    let left = head, right = "";
    const parts = head.split("|").map(s => s.trim());
    if(parts.length >= 2){ left = parts[0]; right = parts.slice(1).join(" | "); }

    return `
      <div class="job">
        <div class="top">
          <strong>${escapeHtml(left)}</strong>
          <span>${escapeHtml(right)}</span>
        </div>
        ${bullets.length ? `<ul>${bullets.map(b => `<li>${escapeHtml(b)}</li>`).join("")}</ul>` : ""}
      </div>
    `;
  }).join("");
  $("vExperience").innerHTML = out || `<p style="color:#6b7280">Wpisz doświadczenie po lewej.</p>`;
}

function renderListTextarea(textareaId, targetId){
  const lines = ($(textareaId).value || "").split("\n").map(l => l.trim()).filter(Boolean);
  $(targetId).innerHTML = lines.length
    ? `<ul>${lines.map(l => `<li>${escapeHtml(l)}</li>`).join("")}</ul>`
    : `<p style="color:#6b7280">Brak danych.</p>`;
  return lines.length;
}

function renderTags(inputId, targetId){
  const list = ($(inputId).value || "").split(",").map(s => s.trim()).filter(Boolean);
  $(targetId).innerHTML = list.length
    ? list.map(s => `<span class="tag">${escapeHtml(s)}</span>`).join("")
    : `<p style="color:#6b7280;margin:0">Brak danych.</p>`;
  return list.length;
}

function setPhoto(dataUrl){
  const img = $("avatarImg");
  const fallback = $("avatarFallback");
  if(dataUrl){
    img.src = dataUrl;
    img.style.display = "block";
    fallback.style.display = "none";
    // store in-memory for JSON export
    window.__photoDataUrl = dataUrl;
  }else{
    img.removeAttribute("src");
    img.style.display = "none";
    fallback.style.display = "block";
    window.__photoDataUrl = null;
  }
}

function arrangeTemplateLayout(template){
  const mainCol = $("mainCol");
  const sideCol = $("sideCol");
  const leftHead = $("leftHead");
  const avatar = $("avatar");
  const educationCard = $("educationCard");
  const certsCard = $("certsCard");

  if(template === "classic"){
    if(avatar && avatar.parentElement !== sideCol){
      sideCol.prepend(avatar);
    }
    if(educationCard && educationCard.parentElement !== sideCol){
      sideCol.appendChild(educationCard);
    }
    return;
  }

  if(avatar && avatar.parentElement !== leftHead){
    leftHead.insertBefore(avatar, leftHead.firstChild);
  }
  if(educationCard && educationCard.parentElement !== mainCol){
    if(certsCard && certsCard.parentElement === mainCol){
      mainCol.insertBefore(educationCard, certsCard.nextSibling);
    }else{
      mainCol.appendChild(educationCard);
    }
  }
}

function applyTemplate(){
  const cv = $("cvRoot");
  const v = $("template").value || "linkedin";
  cv.classList.remove("linkedin","minimal","manager","classic");
  cv.classList.add(v);
  arrangeTemplateLayout(v);
}

/**
 * Fit content to one printable page.
 * Strategy: if CV overflows, reduce root font-size a little (within safe min).
 */
function fitToPage(){
  const cv = $("cvRoot");
  const note = $("fitNote");
  // reset to base
  cv.style.fontSize = "";
  note.textContent = "";

  // Keep content within exact A4 container height.
  const MAX = cv.clientHeight;

  let size = parseFloat(getComputedStyle(cv).fontSize);
  let loops = 0;

  while(cv.scrollHeight > MAX && size > 12 && loops < 30){
    size -= 0.3;
    cv.style.fontSize = size + "px";
    loops++;
  }

  if(loops > 0){
    note.textContent = "Dopasowano rozmiar tekstu, aby zmieścić CV na 1 stronie A4.";
  }
}

function sync(){
  setName();
  applyTemplate();

  $("vRole").textContent = ($("role").value || "Stanowisko").trim();

  const city = ($("city").value || "").trim();
  const phone = ($("phone").value || "").trim();
  $("vCity").textContent = city || "Miasto";
  $("vPhone").textContent = phone || "Telefon";

  $("vContactLine").textContent = [city, phone].filter(Boolean).join(" • ") || "Miasto • Telefon";
  $("vEmail").textContent = ($("email").value || "email@domena.pl").trim();

  const li = ($("linkedin").value || "").trim();
  $("vLinkedIn").style.display = li ? "inline-block" : "none";
  $("vLinkedIn").textContent = li;

  $("vSummary").textContent =
    ($("summary").value || "").trim() ||
    "Uzupełnij krótki profil: specjalizacja, zakres, mocne strony, 1 wynik liczbowy.";

  renderExperience();

  const skillsCount = renderTags("skills", "vSkills");
  const toolsCount  = renderTags("tools", "vTools");
  const hobbyCount  = renderTags("hobbies", "vHobbies");
  const languagesCount = renderTags("languages", "vLanguages");

  $("toolsCard").style.display = toolsCount ? "block" : "none";
  $("hobbiesCard").style.display = hobbyCount ? "block" : "none";
  $("languagesCard").style.display = languagesCount ? "block" : "none";

  const certCount = renderListTextarea("certs", "vCerts");
  $("certsCard").style.display = certCount ? "block" : "none";

  renderListTextarea("education", "vEducation");

  const consent = ($("consent").value || "").trim();
  $("vConsent").textContent = consent || "Wyrażam zgodę na przetwarzanie moich danych osobowych dla potrzeb rekrutacji zgodnie z obowiązującymi przepisami prawa.";

  // Fit after rendering
  requestAnimationFrame(fitToPage);
}

// Print
$("printBtn").addEventListener("click", () => window.print());
$("themeToggle").addEventListener("click", toggleTheme);

// Upload photo
$("photoInput").addEventListener("change", (e) => {
  const file = e.target.files && e.target.files[0];
  if(!file) return;

  if(!file.type.startsWith("image/")){
    alert("Wybierz plik graficzny (JPG/PNG/WebP).");
    e.target.value = "";
    return;
  }
  const reader = new FileReader();
  reader.onload = () => setPhoto(reader.result);
  reader.readAsDataURL(file);
});

// Drag & drop photo on preview
const dropZone = $("dropZone");
["dragenter","dragover"].forEach(evt => dropZone.addEventListener(evt, (e) => {
  e.preventDefault();
  e.stopPropagation();
  dropZone.classList.add("dragover");
}));
["dragleave","drop"].forEach(evt => dropZone.addEventListener(evt, (e) => {
  e.preventDefault();
  e.stopPropagation();
  dropZone.classList.remove("dragover");
}));
dropZone.addEventListener("drop", (e) => {
  const file = e.dataTransfer?.files?.[0];
  if(!file) return;
  if(!file.type.startsWith("image/")){
    alert("Upuść plik graficzny (JPG/PNG/WebP).");
    return;
  }
  const reader = new FileReader();
  reader.onload = () => setPhoto(reader.result);
  reader.readAsDataURL(file);
});

// Remove photo
$("removePhoto").addEventListener("click", () => {
  $("photoInput").value = "";
  setPhoto(null);
});

// Template change
$("template").addEventListener("change", sync);

// JSON export/import
function collectData(){
  return {
    template: $("template").value,
    firstName: $("firstName").value,
    lastName: $("lastName").value,
    role: $("role").value,
    city: $("city").value,
    phone: $("phone").value,
    email: $("email").value,
    linkedin: $("linkedin").value,
    summary: $("summary").value,
    experience: $("experience").value,
    skills: $("skills").value,
    tools: $("tools").value,
    certs: $("certs").value,
    education: $("education").value,
    hobbies: $("hobbies").value,
    languages: $("languages").value,
    theme: document.body.getAttribute("data-theme") || "dark",
    consent: $("consent").value,
    photoDataUrl: window.__photoDataUrl || null,
    version: 1
  };
}

function applyData(d){
  if(!d) return;
  if(d.template) $("template").value = d.template;
  if(typeof d.theme === "string") applyTheme(d.theme);

  const fields = ["firstName","lastName","role","city","phone","email","linkedin","summary","experience","skills","tools","certs","education","hobbies","languages","consent"];
  for(const k of fields){
    if(typeof d[k] === "string") $(k).value = d[k];
  }
  if(d.photoDataUrl){
    setPhoto(d.photoDataUrl);
  }else{
    setPhoto(null);
  }
  sync();
}

$("saveJson").addEventListener("click", () => {
  const data = collectData();
  const blob = new Blob([JSON.stringify(data, null, 2)], {type:"application/json"});
  const a = document.createElement("a");
  a.href = URL.createObjectURL(blob);
  a.download = "cv-data.json";
  a.click();
});

$("loadJson").addEventListener("change", (e) => {
  const file = e.target.files && e.target.files[0];
  if(!file) return;

  const reader = new FileReader();
  reader.onload = () => {
    try{
      const data = JSON.parse(reader.result);
      applyData(data);
    }catch(err){
      alert("Nie udało się wczytać pliku JSON.");
    }
  };
  reader.readAsText(file);
});

// Example
$("loadExample").addEventListener("click", () => {
  applyData({
    template: "linkedin",
    firstName: "Aleksander",
    lastName: "Nowak",
    role: "Spedytor Międzynarodowy",
    city: "Kraków",
    phone: "+48 602 781 456",
    email: "aleksander.nowak@email.com",
    linkedin: "linkedin.com/in/aleksandernowak",
    summary:
      "Spedytor międzynarodowy z 6-letnim doświadczeniem w organizacji transportów drogowych w UE. " +
      "Specjalizuję się w optymalizacji kosztów, negocjacjach stawek oraz pracy na giełdach transportowych. " +
      "W ostatnim roku obniżyłem średni koszt przewozu o ~12% przy zachowaniu terminowości dostaw.",
    experience:
`Senior Spedytor – EuroTrans Logistics | 2022–obecnie
- organizacja transportów drogowych UE (FTL/LTL)
- negocjacje stawek z przewoźnikami (redukcja kosztów ~12%)
- monitoring transportów, rozwiązywanie reklamacji
- Trans.eu / Timocom, raportowanie KPI

Spedytor – Global Freight Solutions | 2019–2022
- planowanie tras i okien załadunków
- koordynacja pracy kierowców, kontakt z klientami
- kompletowanie i weryfikacja dokumentów przewozowych`,
    skills: "Negocjacje, Planowanie tras, Obsługa klienta, Excel, Analiza kosztów",
    tools: "Trans.eu, Timocom, TMS, GPS, MS Office",
    certs: "Incoterms 2020 – szkolenie, 2024\nKurs: Excel w logistyce (online), 2023",
    education: "Uniwersytet Ekonomiczny w Krakowie – Logistyka (licencjat), 2019\nTechnikum logistyczne – Technik logistyk, 2016",
    hobbies: "motoryzacja, geografia, analiza danych",
    languages: "Polski (ojczysty), Angielski (B2)",
    consent: "Wyrażam zgodę na przetwarzanie moich danych osobowych dla potrzeb rekrutacji zgodnie z obowiązującymi przepisami prawa.",
    photoDataUrl: null
  });
});

// Live binding
[
  "firstName","lastName","role","city","phone","email","linkedin",
  "summary","experience","skills","tools","certs","education","hobbies","languages","consent"
].forEach(id => $(id).addEventListener("input", sync));

// init
window.__photoDataUrl = null;
initTheme();
sync();
