package com.metro.ticket.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_station", nullable = false)
    private String fromStation;

    @Column(name = "to_station", nullable = false)
    private String toStation;

    @Column(name = "primary_line")
    private String primaryLine;

    @Column(name = "change_required")
    private boolean changeRequired;

    @Column(name = "issued_at", nullable = false)
    private LocalDateTime issuedAt;

    @Column(name = "valid_upto", nullable = false)
    private LocalDateTime validUpto;

    @Column(precision = 10, scale = 2)
    private BigDecimal fare;

    @Column(name = "qr_token", unique = true)
    private String qrToken;
    @Column(name = "ticket_number", unique = true)
    private String ticketNumber;

    @Column(name = "passengers")
    private Integer passengers = 1;

    @Column(name = "platform_no")
    private Integer platformNo = 1;
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "qr_code", columnDefinition = "bytea")
    @org.hibernate.annotations.JdbcTypeCode(java.sql.Types.BINARY)
    private byte[] qrCode;
    // ===== getters & setters =====

    private String status;

    public Long getId() {
        return id;
    }

    public String getFromStation() {
        return fromStation;
    }

    public void setFromStation(String fromStation) {
        this.fromStation = fromStation;
    }

    public String getToStation() {
        return toStation;
    }

    public void setToStation(String toStation) {
        this.toStation = toStation;
    }

    public String getPrimaryLine() {
        return primaryLine;
    }

    public void setPrimaryLine(String primaryLine) {
        this.primaryLine = primaryLine;
    }

    public boolean isChangeRequired() {
        return changeRequired;
    }

    public void setChangeRequired(boolean changeRequired) {
        this.changeRequired = changeRequired;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
    }

    public LocalDateTime getValidUpto() {
        return validUpto;
    }

    public void setValidUpto(LocalDateTime validUpto) {
        this.validUpto = validUpto;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }

    public byte[] getQrCode() {
        return qrCode;
    }

    public void setQrCode(byte[] qrCode) {
        this.qrCode = qrCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public Integer getPassengers() {
        return passengers;
    }

    public void setPassengers(Integer passengers) {
        this.passengers = passengers;
    }

    public Integer getPlatformNo() {
        return platformNo;
    }

    public void setPlatformNo(Integer platformNo) {
        this.platformNo = platformNo;
    }
}