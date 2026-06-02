const API = "/api";

/* ========= LOGIN ========= */
async function login() {
    const u = document.getElementById("username").value;
    const p = document.getElementById("password").value;

    try {
        const res = await fetch(`${API}/admin/login`, {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: `username=${encodeURIComponent(u)}&password=${encodeURIComponent(p)}`,
            credentials: "include"
        });

        if (!res.ok) {
            document.getElementById("loginError").innerText = "Invalid credentials";
            return;
        }

        document.getElementById("loginCard").classList.add("hidden");
        document.getElementById("dashboard").classList.remove("hidden");
        document.getElementById("logoutBtn").classList.remove("hidden");

        loadStats();
        loadTickets();

    } catch (e) {
        console.error(e);
        alert("Login failed");
    }
}

/* ========= LOAD STATS ========= */
async function loadStats() {
    const res = await fetch(`${API}/admin/stats`, {
        credentials: "include"
    });

    if (!res.ok) {
        alert("Unauthorized");
        return;
    }

    const d = await res.json();

    document.getElementById("totalTickets").innerText = d.totalTickets;
    document.getElementById("todayRevenue").innerText = "₹ " + d.todayRevenue;
}

/* ========= LOAD TICKETS ========= */
async function loadTickets() {
    const res = await fetch(`${API}/admin/tickets`, {
        credentials: "include"
    });

    if (!res.ok) return;

    const list = await res.json();
    const body = document.getElementById("ticketTable");

    body.innerHTML = "";

    list.forEach(t => {
        const tr = document.createElement("tr");

        tr.innerHTML = `
            <td>${t.id}</td>
            <td>${t.fromStation}</td>
            <td>${t.toStation}</td>
            <td>₹ ${t.fare ?? "-"}</td>
            <td>${t.status}</td>
            <td>
                ${t.status === "ACTIVE"
                    ? `<button onclick="invalidate(${t.id})">Invalidate</button>`
                    : "-"}
            </td>
        `;

        body.appendChild(tr);
    });
}

/* ========= INVALIDATE ========= */
async function invalidate(id) {
    await fetch(`${API}/admin/tickets/${id}/invalidate`, {
        method: "POST",
        credentials: "include"
    });

    loadStats();
    loadTickets();
}

/* ========= EXPORT ========= */
function exportCsv() {
    window.location.href = `${API}/admin/tickets/export`;
}

/* ========= LOGOUT ========= */
function logout() {
    fetch(`${API}/admin/logout`, {
        method: "POST",
        credentials: "include"
    }).then(() => location.reload());
}