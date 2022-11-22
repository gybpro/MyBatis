package com.high.bank.expection;

/**
 * 转账异常
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class TransferException extends Exception{
    public TransferException() {}

    public TransferException(String msg) {
        super(msg);
    }
}
