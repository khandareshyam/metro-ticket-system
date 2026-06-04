package com.metro.ticket.controller;

import com.metro.ticket.model.Ticket;
import com.metro.ticket.service.TicketService;
import com.metro.ticket.service.TicketPdfService;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;
    private final TicketPdfService pdfService;

    // FIXED CONSTRUCTOR
    public TicketController(TicketService ticketService,
            TicketPdfService pdfService) {
        this.ticketService = ticketService;
        this.pdfService = pdfService;
    }

    // ISSUE TICKET
    @PostMapping("/issue-route")
    public ResponseEntity<?> issueRoute(
            @RequestParam String fromStation,
            @RequestParam String toStation,
            HttpSession session) {

        try {
            String mobile = (String) session.getAttribute("MOBILE");

            Ticket t = ticketService.issueRouteTicket(
                    fromStation,
                    toStation,
                    mobile);
            return ResponseEntity.ok(t);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // QR IMAGE
    @GetMapping("/{id}/qr")
    public ResponseEntity<byte[]> getQr(@PathVariable Long id) {

        byte[] qr = ticketService.getQr(id);

        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(qr);
    }

    // NEW: PRINT PDF (REAL METRO STYLE)
    @GetMapping("/{id}/print")
    public ResponseEntity<byte[]> print(@PathVariable Long id) {

        Ticket t = ticketService.getTicketById(id);

        byte[] pdf = pdfService.generateTicketPdf(t);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<?> myTickets(
            HttpSession session) {

        String mobile = (String) session.getAttribute("MOBILE");

        return ResponseEntity.ok(
                ticketService.getTicketsByMobile(mobile));
    }
}