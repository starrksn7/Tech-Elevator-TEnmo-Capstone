package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transaction {

    private int transaction_id;
    private int account_out;
    private int account_in;
    private BigDecimal amount;
    private boolean is_requesting;


    public Transaction() {

    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public int getAccount_out() {
        return account_out;
    }

    public void setAccount_out(int account_out) {
        this.account_out = account_out;
    }

    public int getAccount_in() {
        return account_in;
    }

    public void setAccount_in(int account_in) {
        this.account_in = account_in;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isIs_requesting() {
        return is_requesting;
    }
    public void setIs_requesting(boolean isRequesting) {
        this.is_requesting = isRequesting;
    }


    @Override
    public String toString() {
        return "Transaction{" +
                "account_receiving_id=" + account_in +
                ", account_sending_id='" + account_out +
                ", transfer_amount = " + amount +
                '}';
    }
}

