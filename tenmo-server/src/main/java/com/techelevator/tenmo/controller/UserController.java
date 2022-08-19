package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/user")
public class UserController {

    private AccountDao accountDao;
    private UserDao userDao;
    private TransactionDao transactionDao;

    public UserController(AccountDao accountDao, UserDao userDao, TransactionDao transactionDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
        this.transactionDao = transactionDao;
    }

    @GetMapping(path = "")
    public List<User> findAllSafe() {
        return userDao.findAllSafe();
    }

    @GetMapping(path = "/id/{username}")
    public int findIdByUserName(@PathVariable("username") String username) {
        return userDao.findIdByUsername(username);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(path = "/details")
    public User findByUsername(@RequestParam String username) {
        return userDao.findByUsername(username);
    }


}
