package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/account")
public class AccountController {

    private AccountDao accountDao;
    private UserDao userDao;
    private TransactionDao transactionDao;

    public AccountController(AccountDao accountDao, UserDao userDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }

    @GetMapping(path = "")
    public List<Account> list() {
        return accountDao.findAccounts();
    }

    @GetMapping(path = "/user/{user_id}")
    public Account accountByUserId(@PathVariable Long user_id) {
        return accountDao.findAccountByUserId(user_id);
    }

    @GetMapping(path = "/{account_id}")
    public Account accountByUserId(@PathVariable int account_id) {
        return accountDao.findByAccountId(account_id);
    }

    @GetMapping(path = "/id/{username}")
    public int accountIdByUserName(@PathVariable String username) {
        return accountDao.accountIdByUserName(username);
    }

    @GetMapping(path = "/balance/{account_id}")
    public BigDecimal getBalance(@PathVariable int account_id) {
        return accountDao.getBalance(account_id);
    }

}
