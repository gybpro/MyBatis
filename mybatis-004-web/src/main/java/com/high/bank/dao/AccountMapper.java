package com.high.bank.dao;

import com.high.bank.pojo.Account;

/**
 * 账户DAO层接口
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface AccountMapper {
    Account selectActByActNo(String actNo);

    int updateActByActNo(Account account);
}
