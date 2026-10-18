// Nomes das telas e dados locais da equipe.
const names = {
  dashboard: "Painel",
  guests: "Hóspedes",
  rooms: "Quartos",
  reservations: "Reservas",
  team: "Equipe"
};
const team = [
  { name: "Marcos Silva", role: "Gerente", shift: "Manhã" },
  { name: "Ana Souza", role: "Recepcionista", shift: "Manhã" },
  { name: "Rafael Costa", role: "Recepcionista", shift: "Tarde" },
  { name: "Camila Oliveira", role: "Governança", shift: "Manhã" },
  { name: "Pedro Almeida", role: "Serviço", shift: "Tarde" },
  { name: "Juliana Ferreira", role: "Chef de rang", shift: "Noite" },
];

const typeLabels = {
  SIMPLES: "Simples",
  DUPLO: "Duplo",
  SUITE: "Suíte",
};

const rates = {
  SIMPLES: 250,
  DUPLO: 350,
  SUITE: 500,
};

let guests = [];
let rooms = [];
let reservations = [];
let activeRoomFilter = "Todos";
let toastTimer;

const $ = (selector) => document.querySelector(selector);
const guestList = $("#guest-list");
const guestSearch = $("#guest-search");
const reservationList = $("#reservation-list");
const reservationSearch = $("#reservation-search");
const roomGrid = $("#room-grid");
const roomSearch = $("#room-search");
const teamList = $("#team-list");
const teamSearch = $("#team-search");
const reservationDialog = $("#reservation-dialog");
const roomDialog = $("#room-dialog");
// Funções utilitárias e navegação.
function todayKey() {
  const date = new Date();
  return `${date.getFullYear()}-${String(date.getMonth()+1).padStart(2,"0")}-${String(date.getDate()).padStart(2,"0")}`
}
function formatDate(value) {
  if (!value) return "—";
  return new Intl.DateTimeFormat("pt-BR", {
    day: "2-digit", month: "short"
  }).format(new Date(`${value}T12:00:00`))
}
$("#today-label").textContent = new Intl.DateTimeFormat("pt-BR", {
  weekday: "long", day: "2-digit", month: "long"
}).format(new Date());
$("#dashboard-date").textContent = new Intl.DateTimeFormat("pt-BR", {
  weekday: "long", day: "2-digit", month: "long", year: "numeric"
}).format(new Date());
function showToast(message) {
  const toast = $("#toast");
  toast.textContent = message;
  toast.classList.add("show");
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => toast.classList.remove("show"), 3200)
}
async function request(path, options = {}) {
  const response = await fetch(path, {
    ...options, headers: {
      Accept: "application/json", ...(options.body ? {
        "Content-Type": "application/json"
      }
      : {}), ...options.headers
    }
  });
  if (!response.ok) {
    const text = await response.text();
    let message = text;
    try {
      const parsed = JSON.parse(text);
      if (parsed && typeof parsed === "object") message = Object.values(parsed).join(" ")
    }
    catch {
      /* A API pode retornar erro em texto simples. */
    }
    throw new Error(message || `Falha na API (${response.status}).`)
  }
  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null
}
// Controla a troca entre as páginas.
function goToPage(page) {
  if (!names[page]) return;
  document.querySelectorAll("[data-page-view]").forEach(view => view.classList.toggle("active", view.dataset.pageView === page));
  document.querySelectorAll(".nav-item[data-page]").forEach(item => {
    const active = item.dataset.page === page;
    item.classList.toggle("active", active);
    if (active) item.setAttribute("aria-current", "page");
    else item.removeAttribute("aria-current")
  });
  $("#current-section").textContent = names[page];
  history.replaceState(null, "", `#${page}`);
  if (page === "guests") loadGuests()
}
document.querySelectorAll("[data-page]").forEach(button => button.addEventListener("click", () => goToPage(button.dataset.page)));
document.querySelectorAll("[data-page-link]").forEach(link => link.addEventListener("click", event => {
  event.preventDefault();
  goToPage(link.dataset.pageLink)
}));
// Hóspedes: consulta, cadastro e tabela.
function cell(row, value, className = "") {
  const td = document.createElement("td");
  td.textContent = value ?? "—";
  if (className) td.className = className;
  row.append(td);
  return td
}
function setGuestCount(count) {
  ["#guest-count",
  "#guest-nav-count",
  "#stat-guests"].forEach(selector => $(selector).textContent = String(count))
}
function renderGuests() {
  const query = guestSearch.value.trim().toLocaleLowerCase("pt-BR"),
  found = guests.filter(g => `${g.nome??""} ${g.documento??""}`.toLocaleLowerCase("pt-BR").includes(query));
  guestList.replaceChildren();
  if (!found.length) {
    const row = document.createElement("tr"),
    td = cell(row, guests.length ? "Nenhum hóspede corresponde à busca.": "Ainda não há hóspedes cadastrados.", "empty-cell");
    td.colSpan = 3;
    guestList.append(row);
    return
  }
  found.forEach(g => {
    const row = document.createElement("tr");
    cell(row, g.nome);
    cell(row, g.documento);
    cell(row, g.id);
    guestList.append(row)
  })
}
async function loadGuests() {
  const status = $("#guest-list-message");
  status.textContent = "Carregando hóspedes…";
  try {
    guests = await request("/hospedes");
    setGuestCount(guests.length);
    status.textContent = `${guests.length} ${guests.length===1?"hóspede cadastrado":"hóspedes cadastrados"} pela API.`;
    renderGuests();
    fillReservationOptions()
  }
  catch (error) {
    status.textContent = "Não foi possível carregar hóspedes. Confira a aplicação e o banco.";
    guestList.replaceChildren();
    const row = document.createElement("tr"),
    td = cell(row, "API /hospedes indisponível.", "empty-cell");
    td.colSpan = 3;
    guestList.append(row);
    console.error(error)
  }
}
$("#guest-form").addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.currentTarget, message = $("#form-message"), button = form.querySelector("button[type='submit']"), body = {
    nome: form.elements.nome.value.trim(), documento: form.elements.documento.value.trim()
  };
  message.classList.remove("error");
  message.textContent = "Enviando cadastro…";
  button.disabled = true;
  try {
    await request("/hospedes", {
      method: "POST", body: JSON.stringify(body)
    });
    form.reset();
    message.textContent = "Hóspede cadastrado com sucesso.";
    await loadGuests()
  }
  catch (error) {
    message.classList.add("error");
    message.textContent = error.message
  }
  finally {
    button.disabled = false
  }
});
guestSearch.addEventListener("input", renderGuests);
$("#refresh-guests").addEventListener("click", loadGuests);
// Quartos: consulta, criação e disponibilidade.
function activeReservation(room) {
  const today = todayKey();
  return reservations.find(r => r.quarto?.id === room.id && r.dataCheckIn <= today && r.dataCheckOut>today)
}
function roomStatus(room) {
  return activeReservation(room) ? "Ocupado": "Disponível"
}
function updateRoomSummary() {
  const occupied = rooms.filter(room => roomStatus(room) === "Ocupado").length,
  available = Math.max(rooms.length-occupied, 0),
  rate = rooms.length ? Math.round(occupied/rooms.length*100) : 0;
  $("#room-occupied-count").textContent = String(occupied).padStart(2, "0");
  $("#room-available-count").textContent = String(available).padStart(2, "0");
  $("#room-total-count").textContent = String(rooms.length).padStart(2, "0");
  $("#room-all-count").textContent = String(rooms.length).padStart(2, "0");
  $("#dashboard-room-count-total").textContent = String(rooms.length);
  $("#dashboard-occupancy").textContent = `${rate}%`;
  $("#dashboard-occupied-rooms").textContent = `${occupied} quartos`;
  $("#dashboard-occupancy-copy").textContent = `${rate}%`;
  $("#dashboard-occupied-copy").textContent = `${occupied} quartos ocupados`;
  $("#dashboard-total-copy").textContent = String(rooms.length);
  $("#dashboard-occupied-legend").textContent = String(occupied);
  $("#dashboard-available-legend").textContent = String(available);
}
function fillReservationOptions() {
  const guestSelect = $("#reservation-guest"),
  selectedGuest = guestSelect.value;
  guestSelect.replaceChildren(new Option("Selecione um hóspede", ""));
  guests.forEach(g => guestSelect.add(new Option(`${g.nome} · ${g.documento}`, g.id)));
  if (selectedGuest) guestSelect.value = selectedGuest;
}
function fillRoomOptions() {
  const roomSelect = $("#reservation-room"),
  selectedRoom = roomSelect.value;
  roomSelect.replaceChildren(new Option("Escolha um quarto disponível", ""));
  rooms.forEach(room => roomSelect.add(new Option(`${room.numero} · ${typeLabels[room.tipo]??room.tipo} · ${roomStatus(room).toLocaleLowerCase("pt-BR")} hoje`, room.id)));
  if (selectedRoom) roomSelect.value = selectedRoom;
}
function renderRooms() {
  const query = roomSearch.value.trim().toLocaleLowerCase("pt-BR"),
  found = rooms.filter(room => {
    const state = roomStatus(room);
    return(activeRoomFilter === "Todos" || state === activeRoomFilter) && `${room.numero} ${typeLabels[room.tipo]??room.tipo}`.toLocaleLowerCase("pt-BR").includes(query)
  });
  roomGrid.replaceChildren();
  found.forEach(room => {
    const state = roomStatus(room), card = document.createElement("article");
    card.className = "room-card";
    const top = document.createElement("div");
    top.className = "room-card-top";
    const number = document.createElement("strong");
    number.className = "room-number";
    number.textContent = room.numero;
    const badge = document.createElement("span");
    badge.className = `room-status ${state==="Ocupado"?"status-occupied":"status-available"}`;
    badge.textContent = state.toLocaleUpperCase("pt-BR");
    top.append(number, badge);
    const type = document.createElement("div");
    type.className = "room-type";
    type.textContent = typeLabels[room.tipo] ?? room.tipo;
    const details = document.createElement("div");
    details.className = "room-details";
    const id = document.createElement("span");
    id.textContent = `Quarto #${room.id}`;
    const price = document.createElement("span");
    price.className = "room-rate";
    price.textContent = `R$ ${rates[room.tipo]??"—"}/noite`;
    details.append(id, price);
    card.append(top, type, details);
    roomGrid.append(card)
  });
  if (!found.length) {
    const empty = document.createElement("p");
    empty.className = "empty-cell";
    empty.textContent = rooms.length ? "Nenhum quarto corresponde ao filtro.": "Nenhum quarto cadastrado ainda.";
    roomGrid.append(empty)
  }
}
async function loadRooms() {
  try {
    rooms = await request("/quartos");
    renderRooms();
    updateRoomSummary();
    fillRoomOptions()
  }
  catch (error) {
    roomGrid.replaceChildren();
    const empty = document.createElement("p");
    empty.className = "empty-cell";
    empty.textContent = "Não foi possível carregar os quartos pela API.";
    roomGrid.append(empty);
    console.error(error)
  }
}
document.querySelectorAll("[data-room-filter]").forEach(button => button.addEventListener("click", () => {
  activeRoomFilter = button.dataset.roomFilter;
  document.querySelectorAll("[data-room-filter]").forEach(tab => tab.classList.toggle("active", tab === button));
  renderRooms()
}));
roomSearch.addEventListener("input", renderRooms);
// Reservas: consulta, criação e cancelamento.
function statusBadge(text, kind = "status-ready") {
  const badge = document.createElement("span");
  badge.className = `status ${kind}`;
  badge.textContent = text.toLocaleUpperCase("pt-BR");
  return badge
}
function reservationAmount(r) {
  const days = Math.max(0, (new Date(`${r.dataCheckOut}T12:00:00`) -new Date(`${r.dataCheckIn}T12:00:00`)) /86400000);
  const value = days*(rates[r.quarto?.tipo] ?? 0);
  return value ? new Intl.NumberFormat("pt-BR", {
    style: "currency", currency: "BRL", maximumFractionDigits: 0
  }).format(value) : "—"
}
function reservationState(r) {
  return r.dataCheckOut <= todayKey() ? "Finalizada": r.dataCheckIn <= todayKey() ? "Em andamento": "Confirmada"
}
function cancelButton(r) {
  const button = document.createElement("button");
  button.type = "button";
  button.className = "cancel-button";
  button.textContent = "Cancelar";
  button.dataset.cancelReservation = String(r.quarto?.id ?? "");
  button.dataset.cancelDate = r.dataCheckIn ?? "";
  button.title = "Cancelar reserva";
  return button
}
function renderReservations() {
  const query = reservationSearch.value.trim().toLocaleLowerCase("pt-BR"),
  found = reservations.filter(r => `${r.hospede?.nome??""} ${r.quarto?.numero??""}`.toLocaleLowerCase("pt-BR").includes(query));
  reservationList.replaceChildren();
  const recent = $("#recent-reservations");
  recent.replaceChildren();
  found.forEach((r, index) => {
    const row = document.createElement("tr");
    cell(row, r.hospede?.nome);
    cell(row, `${r.quarto?.numero??"—"} · ${typeLabels[r.quarto?.tipo]??r.quarto?.tipo??"Quarto"}`);
    cell(row, formatDate(r.dataCheckIn));
    cell(row, formatDate(r.dataCheckOut));
    const state = reservationState(r);
    const stateCell = document.createElement("td");
    stateCell.append(statusBadge(state, state === "Finalizada" ? "status-neutral": "status-ready"));
    row.append(stateCell);
    cell(row, reservationAmount(r));
    const actions = cell(row, "");
    if (state === "Finalizada") actions.textContent = "—";
    else actions.append(cancelButton(r));
    reservationList.append(row);
    if (index<3) {
      const small = document.createElement("tr");
      cell(small, r.hospede?.nome);
      cell(small, `${r.quarto?.numero??"—"} · ${typeLabels[r.quarto?.tipo]??r.quarto?.tipo??""}`);
      cell(small, `${formatDate(r.dataCheckIn)} — ${formatDate(r.dataCheckOut)}`);
      const status = document.createElement("td");
      status.append(statusBadge(state, state === "Finalizada" ? "status-neutral": "status-ready"));
      small.append(status);
      cell(small, reservationAmount(r));
      recent.append(small)
    }
  });
  if (!found.length) {
    const row = document.createElement("tr"),
    td = cell(row, reservations.length ? "Nenhuma reserva corresponde à busca.": "Nenhuma reserva cadastrada ainda.", "empty-cell");
    td.colSpan = 7;
    reservationList.append(row)
  }
  $("#reservation-count").textContent = String(reservations.length).padStart(2, "0")
}
async function loadReservations() {
  try {
    reservations = await request("/reservas");
    renderReservations();
    renderRooms();
    updateRoomSummary();
    fillRoomOptions()
  }
  catch (error) {
    reservationList.replaceChildren();
    const row = document.createElement("tr"),
    td = cell(row, "Não foi possível carregar as reservas.", "empty-cell");
    td.colSpan = 7;
    reservationList.append(row);
    console.error(error)
  }
}
reservationSearch.addEventListener("input", renderReservations);
reservationList.addEventListener("click", async event => {
  const button = event.target.closest("[data-cancel-reservation]");
  if (!button) return;
  if (!confirm("Deseja cancelar esta reserva?")) return;
  try {
    const quartoId = encodeURIComponent(button.dataset.cancelReservation), date = encodeURIComponent(button.dataset.cancelDate);
    await request(`/reservas/${quartoId}/${date}`, {
      method: "DELETE"
    });
    showToast("Reserva cancelada.");
    await Promise.all([loadReservations(), loadRooms()])
  }
  catch (error) {
    showToast(error.message)
  }
});
// Equipe: demonstração visual até existir uma API.
function renderTeam() {
  const query = teamSearch.value.trim().toLocaleLowerCase("pt-BR");
  teamList.replaceChildren();
  team.filter(p => `${p.name} ${p.role} ${p.shift}`.toLocaleLowerCase("pt-BR").includes(query)).forEach(person => {
    const row = document.createElement("tr"), personCell = cell(row, "", "person-cell"), avatar = document.createElement("span"), name = document.createElement("span");
    avatar.className = "person-avatar";
    avatar.textContent = person.name.split(" ").map(part => part[0]).slice(0, 2).join("");
    name.textContent = person.name;
    personCell.append(avatar, name);
    cell(row, person.role, "role-label");
    cell(row, person.shift, "shift-label");
    const status = document.createElement("td"), onShift = person.shift === "Manhã";
    status.append(statusBadge(onShift ? "Em serviço": "Próximo turno", onShift ? "status-ready": "status-wait"));
    row.append(status);
    teamList.append(row)
  })
}
teamSearch.addEventListener("input", renderTeam);
// Diálogos e formulários de criação.
document.querySelectorAll("[data-open-reservation]").forEach(button => button.addEventListener("click", async() => {
  await Promise.all([loadGuests(), loadRooms()]);
  fillReservationOptions();
  fillRoomOptions();
  reservationDialog.showModal()
}));
document.querySelectorAll(".close-dialog").forEach(button => button.addEventListener("click", () => reservationDialog.close()));
reservationDialog.addEventListener("click", event => {
  if (event.target === reservationDialog) reservationDialog.close()
});
$("#reservation-form").addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.currentTarget, checkin = form.elements["reservation-checkin"].value, checkout = form.elements["reservation-checkout"].value;
  if (checkout <= checkin) {
    showToast("O check-out precisa ser posterior ao check-in.");
    return
  }
  const submit = form.querySelector("button[type='submit']");
  submit.disabled = true;
  try {
    await request("/reservas", {
      method: "POST", body: JSON.stringify({
        hospedeId: Number(form.elements["reservation-guest"].value), quartoId: Number(form.elements["reservation-room"].value), dataCheckIn: checkin, dataCheckOut: checkout, observacao: form.elements.observacao.value.trim()
      })
    });
    reservationDialog.close();
    form.reset();
    showToast("Reserva criada com sucesso.");
    await Promise.all([loadReservations(), loadRooms()])
  }
  catch (error) {
    showToast(error.message)
  }
  finally {
    submit.disabled = false
  }
});
document.querySelectorAll("[data-open-room]").forEach(button => button.addEventListener("click", () => roomDialog.showModal()));
document.querySelectorAll(".close-room-dialog").forEach(button => button.addEventListener("click", () => roomDialog.close()));
roomDialog.addEventListener("click", event => {
  if (event.target === roomDialog) roomDialog.close()
});
$("#room-form").addEventListener("submit", async event => {
  event.preventDefault();
  const form = event.currentTarget, submit = form.querySelector("button[type='submit']");
  submit.disabled = true;
  try {
    await request("/quartos", {
      method: "POST", body: JSON.stringify({
        numero: Number(form.elements.numero.value), tipo: form.elements.tipo.value
      })
    });
    roomDialog.close();
    form.reset();
    showToast("Quarto cadastrado com sucesso.");
    await loadRooms()
  }
  catch (error) {
    showToast(error.message)
  }
  finally {
    submit.disabled = false
  }
});
// Inicialização da aplicação.
function openCurrentHash() {
  const initial = location.hash.slice(1);
  if (names[initial]) goToPage(initial)
}
renderTeam();
openCurrentHash();
Promise.all([loadGuests(), loadRooms(), loadReservations()]);


