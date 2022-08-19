package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.TransactionStatus;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionDao {

    List<Transaction> listTransactions(String username);

    Transaction getTransaction(int transaction_id);

    boolean subtractFromBalance(BigDecimal balance, int account_id) throws IllegalArgumentException;

    boolean addToBalance(BigDecimal balance, int account_id) throws IllegalArgumentException;

    boolean insertTransaction(Transaction transaction);

    boolean approveOrDenyTransaction(boolean status, int status_id, int transaction_id);

    TransactionStatus getStatusByTransactionId(int transaction_id);

}
