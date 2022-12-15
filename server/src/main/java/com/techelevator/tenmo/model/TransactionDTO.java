package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class TransactionDTO {

    private int transId;
    private int UserId;
    @DecimalMin(value = "0.01", inclusive = true, message = "Must transfer more than $0.00.")
    private BigDecimal amount;
    private Timestamp transTimestamp;
    private String status;

    public TransactionDTO(int UserId, BigDecimal amount) {
        this.UserId = UserId;
        this.amount = amount;
        this.transTimestamp = new Timestamp(System.currentTimeMillis());
    }

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getUserId() {
        return UserId;
    }

    public void setUserId(int toUserId) {
        this.UserId = toUserId;
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
}
