package com.techelevator.tenmo.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionStatus {

    private int status_id;
    private int transaction_id;
    private boolean status;


    public TransactionStatus() {

    }

    public int getStatus_id() {
        return status_id;
    }

    public void setStatus_id(int status_id) {
        this.status_id = status_id;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}


