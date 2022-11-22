package com.high.bank.web.servlet;

import com.high.bank.expection.MoneyNotEnoughException;
import com.high.bank.expection.TransferException;
import com.high.bank.service.AccountService;
import com.high.bank.service.impl.AccountServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 账户相关控制
 *
 * @author high
 * @version 1.0
 * @since 1.0
 */
@WebServlet("/transfer")
public class AccountServlet extends HttpServlet {
    private final AccountService accountService = new AccountServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String fromAccount = request.getParameter("fromAccount");
        String toAccount = request.getParameter("toAccount");
        double money = Double.parseDouble(request.getParameter("money"));

        try {
            accountService.transfer(fromAccount, toAccount, money);
            response.sendRedirect(request.getContextPath() + "/success.html");
        } catch (MoneyNotEnoughException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error1.html");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/error2.html");
        }
    }
}
