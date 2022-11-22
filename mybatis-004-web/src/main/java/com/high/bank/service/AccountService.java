package com.high.bank.service;

import com.high.bank.expection.MoneyNotEnoughException;
import com.high.bank.expection.TransferException;

/**
 * 账户业务相关接口
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public interface AccountService {
    /**
     * 转账业务
     * @param fromAccount 转账账户
     * @param toAccount 到账账户
     * @param money 转账资金
     */
    void transfer(String fromAccount, String toAccount, double money) throws MoneyNotEnoughException, TransferException;
}
