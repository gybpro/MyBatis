package com.high.bank.dao.impl;

import com.high.bank.dao.AccountMapper;
import com.high.bank.pojo.Account;
import com.high.bank.util.SqlSessionUtil;
import org.apache.ibatis.session.SqlSession;

/**
 * 账户DAO层实现类
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
public class AccountMapperImpl implements AccountMapper {
    @Override
    public Account selectActByActNo(String actNo) {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return sqlSession.selectOne("account.selectActByActNo", actNo);
    }

    @Override
    public int updateActByActNo(Account account) {
        SqlSession sqlSession = SqlSessionUtil.openSession();
        return sqlSession.update("account.updateActByActNo", account);
    }
}
