package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.TransactionStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerErrorException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao {
    private final AccountDao accountDao;
    private final UserDao userDao;
    private final JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(AccountDao accountDao, UserDao userDao, JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
        this.userDao = userDao;
    }

    @Override
    public List<Transaction> listTransactions(String username) {
        List<Transaction> transactions = new ArrayList<>();
        String sql1 = "SELECT transaction_id, account_out, account_in, amount, is_requesting " +
                " FROM transactions " +
                " JOIN account on transactions.account_in = account.account_id " +
                " JOIN tenmo_user on account.user_id = tenmo_user.user_id " +
                " WHERE " +
                " username = ?; ";
        String sql2 = "SELECT transaction_id, account_out, account_in, amount, is_requesting " +
                " FROM transactions " +
                " JOIN account on transactions.account_out = account.account_id " +
                " JOIN tenmo_user on account.user_id = tenmo_user.user_id " +
                " WHERE " +
                " username = ?; ";
        SqlRowSet results1 = jdbcTemplate.queryForRowSet(sql1, username);
        SqlRowSet results2 = jdbcTemplate.queryForRowSet(sql2, username);
        while (results1.next()) {
            transactions.add(mapRowToTransaction(results1));
        }
        while (results2.next()) {
            transactions.add(mapRowToTransaction(results2));
        }
        return transactions;
    }

    @Override
    public Transaction getTransaction(int transaction_id) {
        Transaction transaction = null;
        String sql = "SELECT transaction_id, account_out, account_in, amount, is_requesting " +
                "FROM transactions WHERE transaction_id = ?; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transaction_id);
        if (results.next()) {
            transaction = mapRowToTransaction(results);
        }
        return transaction;
    }

    @Override
    public boolean subtractFromBalance(BigDecimal money, int account_id) throws IllegalArgumentException {
        Account account = new Account();
        String sql = " UPDATE account SET balance = balance - ? " +
                " WHERE account_id = ?; ";
        if (account.getAccount_id() == account_id) {
            System.out.println("Please select a valid recipient.");
            return false;
        } else {
            try {
                jdbcTemplate.update(sql, money, account_id);
            } catch (IllegalArgumentException e) {
                System.out.println("Please choose an existing account_id");
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean addToBalance(BigDecimal money, int account_id) throws IllegalArgumentException {
        Account account = new Account();
        String sql = "UPDATE account SET balance = balance + ? " +
                " WHERE account_id = ?; ";
        if (account.getAccount_id() == account_id) {
            System.out.println("You cannot send money to yourself.");
            return false;
        } else {
            try {
                jdbcTemplate.update(sql, money, account_id);
            } catch (IllegalArgumentException e) {
                System.out.println("Please choose an existing account_id");
                return false;
            }
            return true;
        }
    }

    @Override
    public boolean insertTransaction(Transaction transaction) {
        String newTransSql = " INSERT INTO transactions(account_out, account_in, amount, is_requesting) " +
                " VALUES (?, ?, ?, ?) RETURNING transaction_id; ";
        Integer newTransactionId;
        try {
            newTransactionId = jdbcTemplate.queryForObject(newTransSql, Integer.class,
                    transaction.getAccount_out(), transaction.getAccount_in(), transaction.getAmount(), transaction.isIs_requesting());
        } catch (DataAccessException e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        String newStatusSql = "INSERT INTO transaction_status(transaction_id, status) " +
                "VALUES (?, DEFAULT) RETURNING status_id; ";
        Integer newStatusId;
        try {
            newStatusId = jdbcTemplate.queryForObject(newStatusSql, Integer.class, newTransactionId);
        } catch (DataAccessException e) {
            System.out.println(e.getLocalizedMessage());
            return false;
        }
        return false;
    }
    @Override
    public boolean approveOrDenyTransaction(boolean status, int status_id, int transaction_id) {
        Transaction transaction = new Transaction();
        String sql = "UPDATE transaction_status SET status = ? WHERE status_id = ? AND transaction_id = ?; ";
        jdbcTemplate.update(sql, status, status_id, transaction_id);
        return true;
    }

    @Override
    public TransactionStatus getStatusByTransactionId(int transaction_id) {
        TransactionStatus status = null;
        String sql = "SELECT status_id, transaction_id, status FROM transaction_status WHERE transaction_id = ?; ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transaction_id);
        while (results.next()) {
            status = mapRowToStatus(results);
        }
        return status;
    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet) {
        Transaction transaction = new Transaction();
        transaction.setTransaction_id(rowSet.getInt("transaction_id"));
        transaction.setAccount_out(rowSet.getInt("account_out"));
        transaction.setAccount_in(rowSet.getInt("account_in"));
        transaction.setAmount(rowSet.getBigDecimal("amount"));
        transaction.setIs_requesting(rowSet.getBoolean("is_requesting"));
        return transaction;
    }

    private TransactionStatus mapRowToStatus(SqlRowSet rowSet) {
        TransactionStatus status = new TransactionStatus();
        status.setStatus_id(rowSet.getInt("status_id"));
        status.setTransaction_id(rowSet.getInt("transaction_id"));
        status.setStatus(rowSet.getBoolean("status"));
        return status;
    }
}
