package com.high.bank.service.impl;

import com.high.bank.dao.AccountMapper;
import com.high.bank.expection.MoneyNotEnoughException;
import com.high.bank.expection.TransferException;
import com.high.bank.pojo.Account;
import com.high.bank.service.AccountService;
import com.high.bank.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 账户业务相关实现类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class AccountServiceImpl implements AccountService {
    // private final AccountMapper accountMapper = new AccountMapperImpl();
    private AccountMapper accountMapper;

    @Override
    public void transfer(String fromAccount, String toAccount, double money) throws MoneyNotEnoughException, TransferException {
        // 每一次都要从连接池中获取，且用完之后要归还，保证连接对象的使用率
        accountMapper = SqlSessionUtil.openSession().getMapper(AccountMapper.class);
        Account fromAct = accountMapper.selectActByActNo(fromAccount);
        double fromActBalance = fromAct.getBalance();
        if (fromActBalance < money) {
            throw new MoneyNotEnoughException("对不起，余额不足");
        }

        Account toAct = accountMapper.selectActByActNo(toAccount);
        fromAct.setBalance(fromActBalance - money);
        toAct.setBalance(toAct.getBalance() + money);

        SqlSession sqlSession = SqlSessionUtil.openSession();
        int count = accountMapper.updateActByActNo(fromAct);

        // 模拟异常
        // int i = 1/0;

        count += accountMapper.updateActByActNo(toAct);
        if (count != 2) {
            throw new TransferException("转账异常，原因未知");
        }

        // 提交事务
        sqlSession.commit();
        // 注意：归还连接时要移除SqlSession对象和当前线程的绑定关系
        // 因为每次获取都是先从线程本地变量中获取，而连接对象已经归还，获取到的可能已经不能使用了
        SqlSessionUtil.close(sqlSession);
    }
}
