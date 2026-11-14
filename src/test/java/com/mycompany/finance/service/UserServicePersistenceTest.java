package com.mycompany.finance.service;

import com.mycompany.finance.User;
import com.mycompany.finance.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserServicePersistenceTest {

    @TempDir
    Path tempDir;

    @Test
    void shouldRestoreUserWalletAndBudgets() {
        UserService original = new UserService();
        original.register("Alex", "123456");

        User user = original.login("Alex", "123456");
        assertNotNull(user);

        Wallet wallet = user.getWallet();
        wallet.addIncome(new BigDecimal("50000"), "Зарплата");
        wallet.addExpense(new BigDecimal("3500"), "Продукты");
        wallet.setBudget("Продукты", new BigDecimal("15000"));

        Path file = tempDir.resolve("users.dat");
        original.saveToFile(file);

        UserService restored = new UserService();
        restored.loadFromFile(file);

        User savedUser = restored.login("Alex", "123456");
        assertNotNull(savedUser);

        Wallet savedWallet = savedUser.getWallet();

        assertEquals(new BigDecimal("46500"), savedWallet.getBalance());
        assertEquals(2, savedWallet.getTransactions().size());
        assertEquals(
                new BigDecimal("11500"),
                savedWallet.getRemainingBudget("Продукты")
        );
        assertNull(restored.login("Alex", "wrong"));
    }

    @Test
    void shouldHandleMissingFile() {
        UserService service = new UserService();
        Path missing = tempDir.resolve("missing.dat");

        assertDoesNotThrow(() -> service.loadFromFile(missing));
        assertNull(service.login("Alex", "123456"));
    }
}
