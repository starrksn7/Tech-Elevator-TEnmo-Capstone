package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    List<Account> findAccounts();

    Account findAccountByUserId(Long user_id);

    Account findByAccountId(int account_id);

    BigDecimal getBalance(int account_id);

    int accountIdByUserName(String username);

}
