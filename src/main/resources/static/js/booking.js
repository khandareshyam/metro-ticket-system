function pay() {
  fetch("/api/bookings/initiate", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      userId: 1,
      journeyId: localStorage.getItem("journeyId")
    })
  })
  .then(res => res.json())
  .then(ticket => {
    localStorage.setItem("ticketId", ticket.id);
    window.location.href = "ticket.html";
  });
}
