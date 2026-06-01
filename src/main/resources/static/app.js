const API_BASE = "/api";
let currentTicketId = null;
let allStations = [];

window.onerror = function (msg) {
    console.error("JS ERROR:", msg);
};

document.addEventListener("DOMContentLoaded", () => {
    loadStations();

    document
        .getElementById("bookBtn")
        .addEventListener("click", createTicket);
});

/* LOAD STATIONS */
async function loadStations() {
    const res = await fetch(`${API}/stations`);
    allStations = await res.json();

    const from = document.getElementById("fromStation");
    const to = document.getElementById("toStation");
    const btn = document.getElementById("bookBtn");

    from.innerHTML = `<option value="">-- Select Station --</option>`;
    to.innerHTML = `<option value="">-- Select Station --</option>`;

    allStations.forEach(s => {
        from.add(new Option(s.name, s.name));
        to.add(new Option(s.name, s.name));
    });

    from.addEventListener("change", validate);
    to.addEventListener("change", validate);

    function validate() {
        const f = from.value;
        const t = to.value;

        btn.disabled = !(f && t && f !== t);
    }
}

/* SWAP */
function swapStations() {
    const from = document.getElementById("fromStation");
    const to = document.getElementById("toStation");

    const temp = from.value;
    from.value = to.value;
    to.value = temp;

    from.dispatchEvent(new Event("change"));
}

/* CREATE TICKET */
async function createTicket() {
    const from = document.getElementById("fromStation").value;
    const to = document.getElementById("toStation").value;

    console.log("FROM:", from, "TO:", to);

    if (!from || !to) {
        alert("Select both stations");
        return;
    }

    try {
        const res = await fetch(
            `${API}/tickets/issue-route?fromStation=${encodeURIComponent(from)}&toStation=${encodeURIComponent(to)}`,
            {
                method: "POST",
                credentials: "include"
            }
        );

        const text = await res.text();
        console.log("RAW RESPONSE:", text);

        if (!res.ok) {
            alert(text);
            return;
        }

        let t;
        try {
            t = JSON.parse(text);
        } catch {
            alert("Invalid JSON from backend");
            return;
        }

        if (!t || !t.id) {
            alert("Invalid ticket response");
            return;
        }

        currentTicketId = t.id;

        renderTicket(t);

    } catch (e) {
        console.error("REAL ERROR:", e);
        alert("Frontend error - check console");
    }
}
/* RENDER */
function renderTicket(t) {
    const box = document.getElementById("ticketBox");
function formatDate(d) {
    return new Date(d).toLocaleString("en-IN");
}
    box.innerHTML = `
        <div class="receipt">

            <div class="qr">
                <img src="${API}/tickets/${t.id}/qr"/>
            </div>

            <div class="logo">PUNE METRO</div>

            <div class="divider"></div>

            <div class="row">Ticket No : ${t.id}</div>

            <div class="center bold">Single Journey Ticket</div>

            <div class="row">
                <span>Date :</span>
                <span>${formatDate(t.validUpto)}</span>
            </div>

            <div class="row">
                <span>From :</span>
                <span>${t.fromStation}</span>
            </div>

            <div class="row">
                <span>To :</span>
                <span>${t.toStation}</span>
            </div>

            <div class="row">
                <span>Fare :</span>
                <span>₹ ${t.fare}</span>
            </div>

            <div class="row">
                <span>Valid Till :</span>
                <span>${formatDate(t.validUpto)}</span>
            </div>

            <div class="row">
                <span>Platform :</span>
                <span>1</span>
            </div>

            <div class="footer">
                Thank you for traveling
            </div>

        </div>
    `;

    document.getElementById("ticketSection").classList.remove("hidden");
}
/* PRINT */
function openPrint() {
    window.print();
}

/* QR */
function openQR() {
    window.open(`${API}/tickets/${currentTicketId}/qr`);
}