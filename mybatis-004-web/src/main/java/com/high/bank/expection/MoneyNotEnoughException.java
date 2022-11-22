package com.high.bank.expection;

/**
 * 余额不足异常
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class MoneyNotEnoughException extends Exception{
    public MoneyNotEnoughException() {}

    public MoneyNotEnoughException(String msg) {
        super(msg);
    }
}
