function search() {
  fetch("/api/routes")
    .then(res => res.json())
    .then(data => {
      const box = document.getElementById("results");
      box.innerHTML = "";

      data.forEach(r => {
        box.innerHTML += `
          <div class="card route-card">
            <b>${r.source}</b> → <b>${r.destination}</b>
            <button class="primary-btn"
              onclick="book(${r.id})">Book</button>
          </div>
        `;
      });
    });
}

function book(id) {
  localStorage.setItem("journeyId", id);
  window.location.href = "booking.html";
}
