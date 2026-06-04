package com.metro.ticket.service;

import com.metro.ticket.model.Station;
import com.metro.ticket.model.Ticket;
import com.metro.ticket.repository.StationRepository;
import com.metro.ticket.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.time.ZoneId;

import java.time.ZonedDateTime;

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

    public Ticket issueRouteTicket(
            String fromStation,
            String toStation,
            String mobile) {

        Station from = stationRepo.findFirstByNameIgnoreCase(fromStation.trim())
                .orElseThrow(() -> new RuntimeException("Invalid FROM station"));

        Station to = stationRepo.findFirstByNameIgnoreCase(toStation.trim())
                .orElseThrow(() -> new RuntimeException("Invalid TO station"));

        Ticket t = new Ticket();
        t.setMobileNumber(mobile);
        t.setFromStation(from.getName());
        t.setToStation(to.getName());

        t.setPrimaryLine(from.getLine());

        t.setChangeRequired(
                !from.getLine().equalsIgnoreCase(to.getLine()));

        LocalDateTime now =

                ZonedDateTime.now(ZoneId.of("Asia/Kolkata"))

                        .toLocalDateTime();
        t.setIssuedAt(now);
        System.out.println("NOW = " + now);
        System.out.println("VALID UPTO = " +
                now.toLocalDate()
                        .plusDays(1)
                        .atTime(0, 30));
        // Valid till 12:30 AM next day (like Pune Metro ticket)
        t.setValidUpto(
                now.toLocalDate()
                        .plusDays(1)
                        .atTime(0, 30));

        int stationCount = Math.abs(from.getSequenceNo() - to.getSequenceNo()) + 1;

        BigDecimal fare;

        if (stationCount <= 4) {
            fare = BigDecimal.valueOf(10);
        } else if (stationCount <= 8) {
            fare = BigDecimal.valueOf(15);
        } else if (stationCount <= 12) {
            fare = BigDecimal.valueOf(20);
        } else if (stationCount <= 16) {
            fare = BigDecimal.valueOf(25);
        } else if (stationCount <= 20) {
            fare = BigDecimal.valueOf(30);
        } else {
            fare = BigDecimal.valueOf(35);
        }

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

        System.out.println("SAVING TICKET...");

        Ticket saved = ticketRepo.save(t);

        System.out.println("SAVED ID = " + saved.getId());

        return saved;
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

        System.out.println("ISSUED = " + t.getIssuedAt());
        System.out.println("VALID  = " + t.getValidUpto());

        return ticketRepo.save(t);
    }

    public java.util.List<Ticket> getTicketsByMobile(String mobile) {

        return ticketRepo.findByMobileNumber(mobile);
    }
}