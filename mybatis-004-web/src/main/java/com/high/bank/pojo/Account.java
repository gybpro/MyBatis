package com.high.bank.pojo;

/**
 * 账户类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class Account {
    private Long id;

    private String actNo;

    private Double balance;

    public Account() {
    }

    public Account(Long id, String actNo, Double balance) {
        this.id = id;
        this.actNo = actNo;
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", actNo='" + actNo + '\'' +
                ", balance=" + balance +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getActNo() {
        return actNo;
    }

    public void setActNo(String actNo) {
        this.actNo = actNo;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
