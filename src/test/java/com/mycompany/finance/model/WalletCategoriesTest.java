package com.mycompany.finance.model;

import com.mycompany.finance.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class WalletCategoriesTest {

    @Test
    void shouldRecognizeExistingCategories() {
        Wallet wallet = new Wallet();
        wallet.addIncome(new BigDecimal("50000"), "Зарплата");
        wallet.addExpense(new BigDecimal("3500"), "Продукты");
        wallet.setBudget("Транспорт", new BigDecimal("10000"));

        assertTrue(wallet.getKnownCategories().contains("Зарплата"));
        assertTrue(wallet.getKnownCategories().contains("Продукты"));
        assertTrue(wallet.getKnownCategories().contains("Транспорт"));
        assertFalse(wallet.getKnownCategories().contains("Такси"));
    }

    @Test
    void shouldProtectBudgetMapFromModification() {
        Wallet wallet = new Wallet();
        wallet.setBudget("Продукты", new BigDecimal("15000"));

        var budgets = wallet.getBudgets();

        assertThrows(UnsupportedOperationException.class, () ->
                budgets.put("Такси", new BigDecimal("1000"))
        );
    }
}
