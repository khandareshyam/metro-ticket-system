package com.metro.ticket.service;

import com.metro.ticket.model.Station;
import com.metro.ticket.model.Ticket;
import com.metro.ticket.repository.StationRepository;
import com.metro.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class TicketService {

    private final TicketRepository ticketRepo;
    private final StationRepository stationRepo;
    private final QRCodeService qrService;

    public TicketService(
            TicketRepository ticketRepo,
            StationRepository stationRepo,
            QRCodeService qrService) {

        this.ticketRepo = ticketRepo;
        this.stationRepo = stationRepo;
        this.qrService = qrService;
    }

    public Ticket issueRouteTicket(String fromStation, String toStation) {

        Station from = stationRepo.findByNameIgnoreCase(fromStation.trim())
                .orElseThrow(() -> new RuntimeException("Invalid FROM station"));

        Station to = stationRepo.findByNameIgnoreCase(toStation.trim())
                .orElseThrow(() -> new RuntimeException("Invalid TO station"));

        Ticket t = new Ticket();

        t.setFromStation(from.getName());
        t.setToStation(to.getName());

        t.setPrimaryLine(from.getLine());

        t.setChangeRequired(
                !from.getLine().equalsIgnoreCase(to.getLine()));

        t.setIssuedAt(LocalDateTime.now());
        t.setValidUpto(LocalDateTime.now().plusHours(5));

        int stationCount = Math.abs(from.getSequenceNo() - to.getSequenceNo()) + 1;

        BigDecimal fare = BigDecimal.valueOf(stationCount * 5L);

        t.setFare(fare);

        t.setStatus("ACTIVE");

        String qrText = from.getName()
                + "|"
                + to.getName()
                + "|"
                + System.currentTimeMillis();

        String token = Base64.getEncoder()
                .encodeToString(qrText.getBytes());

        t.setQrToken(token);

        try {
            t.setQrCode(
                    qrService.generateQrBytes(token));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ticketRepo.save(t);
    }

    public Ticket getTicketById(Long id) {

        return ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    public byte[] getQr(Long id) {

        Ticket t = getTicketById(id);
        return t.getQrCode();
    }

    public Ticket validateTicket(Long id) {

        Ticket t = getTicketById(id);
        t.setStatus("USED");

        return ticketRepo.save(t);
    }
}