package com.mycompany.finance;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Set;

import java.util.LinkedHashSet;

public class Wallet implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal balance;
    private List<Transaction> transactions;
    private Map<String, BigDecimal> budgets;

    public Wallet() {
        this.balance = BigDecimal.ZERO;
        this.transactions = new ArrayList<>();
        this.budgets = new HashMap<>();
    }

    public void addIncome(BigDecimal amount, String category) {
        if (amount == null) {
            throw new IllegalArgumentException("Сумма операции не может быть null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма дохода должна быть больше нуля");
        }

        balance = balance.add(amount);
        transactions.add(new Transaction(amount, category, TransactionType.INCOME));
    }

    public void addExpense(BigDecimal amount, String category) {
        if (amount == null) {
            throw new IllegalArgumentException("Сумма операции не может быть null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Сумма расхода должна быть больше нуля");
        }

        balance = balance.subtract(amount);
        transactions.add(new Transaction(amount, category, TransactionType.EXPENSE));
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return List.copyOf(transactions);
    }

    public void setBudget(String category, BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Сумма бюджета не может быть null");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Сумма бюджета не может быть отрицательной");
        }

        budgets.put(category, amount);
    }

    public BigDecimal getExpensesByCategory(String category) {
        BigDecimal sum = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE
                    && transaction.getCategory().equals(category)) {
                sum = sum.add(transaction.getAmount());
            }
        }

        return sum;
    }


    public Map<String, BigDecimal> getBudgets() {
        return Map.copyOf(budgets);
    }

    public Set<String> getKnownCategories() {
        Set<String> categories = new LinkedHashSet<>(budgets.keySet());

        for (Transaction transaction : transactions) {
            categories.add(transaction.getCategory());
        }

        return java.util.Collections.unmodifiableSet(categories);
    }

    public BigDecimal getRemainingBudget(String category) {
        if (!budgets.containsKey(category)) {
            System.out.println("Бюджет категории не установлен!");
            return null;
        }

        return budgets.get(category).subtract(getExpensesByCategory(category));
    }

    public void checkBudget(String category) {
        BigDecimal remaining = getRemainingBudget(category);

        if (remaining == null) {
            return;
        }

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            System.out.printf("Бюджет превышен на %s!%n", remaining.abs());
        } else if (remaining.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Бюджет полностью израсходован!");
        } else {
            System.out.printf("Остаток суммы бюджета %s%n", remaining);
        }
    }

    public BigDecimal getTotalIncome() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }

    public BigDecimal getTotalExpenses() {
        BigDecimal total = BigDecimal.ZERO;

        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                total = total.add(transaction.getAmount());
            }
        }

        return total;
    }
}
