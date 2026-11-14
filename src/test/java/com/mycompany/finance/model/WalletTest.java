package com.mycompany.finance.model;

import com.mycompany.finance.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class WalletTest {
    @Test
    void incomeAdditionCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(10000);
        Wallet wallet = new Wallet();
        wallet.addIncome(BigDecimal.valueOf(10000), "Продукты");
        assertEquals(expectedAmount, wallet.getBalance());
    }

    @Test
    void expenseSubtractionCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(7000);
        Wallet wallet = new Wallet();
        wallet.addIncome(BigDecimal.valueOf(10000), "Продукты");
        wallet.addExpense(BigDecimal.valueOf(3000), "Продукты");
        assertEquals(expectedAmount, wallet.getBalance());
    }

    @Test
    void remainingBudgetCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(11500);
        Wallet wallet = new Wallet();
        wallet.setBudget("Продукты", BigDecimal.valueOf(15000));
        wallet.addExpense(BigDecimal.valueOf(3500), "Продукты");
        assertEquals(expectedAmount, wallet.getRemainingBudget("Продукты"));
    }

    @Test
    void budgetExceededCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(-3500);
        Wallet wallet = new Wallet();
        wallet.setBudget("Продукты", BigDecimal.valueOf(15000));
        wallet.addExpense(BigDecimal.valueOf(18500), "Продукты");
        assertEquals(expectedAmount, wallet.getRemainingBudget("Продукты"));
    }

    @Test
    void negativeIncomeCheck() {
        BigDecimal expectedAmount = BigDecimal.ZERO;
        Wallet wallet = new Wallet();
        assertThrows(IllegalArgumentException.class, () -> wallet.addIncome(BigDecimal.valueOf(-5000), "Продукты"));
        assertEquals(expectedAmount, wallet.getBalance());
        assertTrue(wallet.getTransactions().isEmpty());
    }

    @Test
    void negativeExpenseCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(10000);
        int expectedInt = 1;
        Wallet wallet = new Wallet();
        wallet.addIncome(BigDecimal.valueOf(10000), "Продукты");
        assertThrows(IllegalArgumentException.class, () -> wallet.addExpense(BigDecimal.valueOf(-3000), "Продукты"));
        assertEquals(expectedAmount, wallet.getBalance());
        assertEquals(expectedInt, wallet.getTransactions().size());
    }

    @Test
    void nullIncomeCheck() {
        BigDecimal expectedAmount = BigDecimal.ZERO;
        Wallet wallet = new Wallet();
        assertThrows(IllegalArgumentException.class, () -> wallet.addIncome(null, "Продукты"));
        assertEquals(expectedAmount, wallet.getBalance());
        assertEquals(0, wallet.getTransactions().size());
    }

    @Test
    void nullExpenseCheck() {
        BigDecimal expectedAmount = BigDecimal.valueOf(10000);
        Wallet wallet = new Wallet();
        wallet.addIncome(BigDecimal.valueOf(10000), "Продукты");
        assertThrows(IllegalArgumentException.class, () -> wallet.addExpense(null, "Продукты"));
        assertEquals(expectedAmount, wallet.getBalance());
        assertEquals(1, wallet.getTransactions().size());
    }

    @Test
    void nullBudgetCheck() {
        Wallet wallet = new Wallet();
        assertThrows(IllegalArgumentException.class, () -> wallet.setBudget("Продукты", null));
    }

    @Test
    void negativeBudgetCheck() {
        Wallet wallet = new Wallet();
        wallet.setBudget("Продукты", BigDecimal.valueOf(15000));
        assertThrows(IllegalArgumentException.class, () -> wallet.setBudget("Продукты", BigDecimal.valueOf(-5000)));
        assertEquals(BigDecimal.valueOf(15000), wallet.getRemainingBudget("Продукты"));
    }

}
