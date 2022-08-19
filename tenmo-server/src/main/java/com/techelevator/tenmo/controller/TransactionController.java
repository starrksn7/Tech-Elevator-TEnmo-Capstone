package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.TransactionStatus;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/transaction")
public class TransactionController {

    private AccountDao accountDao;
    private UserDao userDao;
    private TransactionDao transactionDao;

    public TransactionController(AccountDao accountDao, UserDao userDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }


    @GetMapping(path = "/{transaction_id}")
    public Transaction getTransaction(@PathVariable int transaction_id) {
        return transactionDao.getTransaction(transaction_id);
    }

    @GetMapping(path = "/user/{username}")
    public List<Transaction> transactionsByUsername(@PathVariable String username, Principal principal) {
        List<Transaction> blank = new ArrayList<>();
        if (username.equalsIgnoreCase(principal.getName())) {
            try {
                return transactionDao.listTransactions(username);
            } catch (AccessDeniedException e) {
                e.getLocalizedMessage();
            }
        }
        return blank;
    }

    @GetMapping(path = "/status/{transaction_id}")
    public TransactionStatus getStatusByTransactionId(@PathVariable int transaction_id) {
        return transactionDao.getStatusByTransactionId(transaction_id);
    }



    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "")
    public Transaction insertTransaction(@RequestBody @Valid Transaction transaction, Principal principal) {

        if (!transaction.isIs_requesting()) {
            if (accountDao.accountIdByUserName(principal.getName()) == transaction.getAccount_in() ||
                    accountDao.accountIdByUserName(principal.getName()) != transaction.getAccount_out()) {
                throw new AccessDeniedException("Please select a valid recipient");
            } else if (accountDao.getBalance(transaction.getAccount_out()).compareTo(transaction.getAmount()) == -1) {
                throw new ArithmeticException("You cannot send more money than you have.");
            } else if (accountDao.findByAccountId(transaction.getAccount_in()) == null) {
                throw new UsernameNotFoundException("Please select a valid user");
            }
            else {
                transactionDao.subtractFromBalance(transaction.getAmount(), transaction.getAccount_out());
                transactionDao.addToBalance(transaction.getAmount(), transaction.getAccount_in());
                transactionDao.insertTransaction(transaction);
            }
        }
        if (transaction.isIs_requesting()) {
            if (accountDao.accountIdByUserName(principal.getName()) == transaction.getAccount_in() &&
                    accountDao.accountIdByUserName(principal.getName()) != transaction.getAccount_out()) {
                transactionDao.insertTransaction(transaction);
            }
        }
        return transaction;
    }

    @PutMapping(path = "")
    public TransactionStatus updateTransaction(@RequestBody @Valid TransactionStatus status, Principal principal) {
        Transaction pending = transactionDao.getTransaction(status.getTransaction_id());
        if (accountDao.accountIdByUserName(principal.getName()) == pending.getAccount_in()) {
            throw new AccessDeniedException("You cannot approve a transaction you requested");
        } else if (accountDao.getBalance(pending.getAccount_out()).compareTo(pending.getAmount()) == -1) {
            throw new ArithmeticException("You cannot send more money than you have.");
        }
        if (!status.getStatus()) {
            transactionDao.approveOrDenyTransaction(false, status.getStatus_id(), status.getTransaction_id());
        } else {
            transactionDao.approveOrDenyTransaction(true, status.getStatus_id(), status.getTransaction_id());
            transactionDao.subtractFromBalance(pending.getAmount(), pending.getAccount_out());
            transactionDao.addToBalance(pending.getAmount(), pending.getAccount_in());
            return status;
        }
        return status;
    }
}




