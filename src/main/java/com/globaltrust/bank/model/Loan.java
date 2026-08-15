package com.globaltrust.bank.model;

public class Loan {
    private String loanId;
    private double amount;
    private String duration;
    private String reason;
    private String status;

    public Loan() {}

    public Loan(String loanId, double amount, String duration, String reason, String status) {
        this.loanId = loanId;
        this.amount = amount;
        this.duration = duration;
        this.reason = reason;
        this.status = status;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
