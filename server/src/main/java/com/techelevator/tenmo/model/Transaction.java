package com.techelevator.tenmo.model;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;

public class Transaction {

    private int transId;
    private int fromUserId;
    private int toUserId;
    private BigDecimal amount;
    private Timestamp transTimestamp;
    private String status;

    public Transaction(){
        this.transTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public Transaction(int transId, int fromUserId, int toUserId, BigDecimal amount, Timestamp transTimestamp, String status) {
        this.transId = transId;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.amount = amount;
        this.transTimestamp = transTimestamp;
        this.status = status;
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(int fromUserId) {
        this.fromUserId = fromUserId;
    }

    public int getToUserId() {
        return toUserId;
    }

    public void setToUserId(int toUserId) {
        this.toUserId = toUserId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Timestamp getTransTimestamp() {
        return transTimestamp;
    }

    public void setTransTimestamp(Timestamp transTimestamp) {
        this.transTimestamp = transTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transId=" + transId +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", amount=" + amount +
                ", transTimestamp=" + transTimestamp +
                ", status='" + status + '\'' +
                '}';
    }
}
