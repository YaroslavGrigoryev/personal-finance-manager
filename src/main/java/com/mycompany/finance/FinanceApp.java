package com.mycompany.finance;

import com.mycompany.finance.service.UserService;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

public class FinanceApp {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        Path dataFile = Path.of("users.dat");
        userService.loadFromFile(dataFile);

        User currentUser = null;

        while (true) {
            System.out.println("""
                    
                    ===== ЛИЧНЫЕ ФИНАНСЫ =====

                    1. Регистрация
                    2. Авторизация
                    3. Посмотреть баланс
                    4. Добавить доход
                    5. Добавить расход
                    6. История операций
                    7. Установить бюджет
                    8. Проверить бюджет
                    9. Финансовая статистика
                    10. Расходы по категориям
                    11. Выйти из аккаунта
                    12. Посмотреть все бюджеты
                    0. Завершить программу
                    """);

            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {

                    case "1" -> {
                        String login = readText(scanner, "Введите логин: ");
                        String password = readText(scanner, "Введите пароль: ");
                        userService.register(login, password);
                    }

                    case "2" -> {
                        String login = readText(scanner, "Введите логин: ");
                        String password = readText(scanner, "Введите пароль: ");

                        User user = userService.login(login, password);

                        if (user == null) {
                            System.out.println("Неверный логин или пароль!");
                        } else {
                            currentUser = user;
                            System.out.println("Добро пожаловать, "
                                    + currentUser.getLogin() + "!");
                        }
                    }

                    case "0" -> {
                        userService.saveToFile(dataFile);
                        System.out.println("Завершение программы.");
                        return;
                    }

                    default -> {
                        if (currentUser == null) {
                            System.out.println("Сначала авторизуйтесь!");
                            continue;
                        }

                        Wallet wallet = currentUser.getWallet();

                        switch (choice) {

                            case "3" -> printBalance(currentUser);

                            case "4" -> {
                                BigDecimal amount = readAmount(scanner);
                                String category = readText(scanner, "Категория дохода: ");

                                wallet.addIncome(amount, category);
                                System.out.println("Доход добавлен!");
                                printBalance(currentUser);
                            }

                            case "5" -> {
                                BigDecimal amount = readAmount(scanner);
                                String category = readText(scanner, "Категория расхода: ");

                                wallet.addExpense(amount, category);
                                System.out.println("Расход добавлен!");
                                printBalance(currentUser);

                                if (wallet.getTotalExpenses().compareTo(
                                        wallet.getTotalIncome()) > 0) {
                                    System.out.println(
                                            "Внимание: расходы превышают доходы!");
                                }

                                wallet.checkBudget(category);
                            }

                            case "6" -> {
                                if (wallet.getTransactions().isEmpty()) {
                                    System.out.println("Операций пока нет.");
                                } else {
                                    for (Transaction transaction : wallet.getTransactions()) {
                                        System.out.printf(
                                                "%-8s | %-15s | %10s ₽ | %s%n",
                                                transaction.getType(),
                                                transaction.getCategory(),
                                                transaction.getAmount(),
                                                transaction.getCreatedAt().format(DATE_FORMAT)
                                        );
                                    }
                                }
                            }

                            case "7" -> {
                                String category = readText(scanner, "Категория: ");
                                BigDecimal amount = readAmount(scanner);

                                wallet.setBudget(category, amount);
                                System.out.println("Бюджет установлен!");
                            }

                            case "8" -> {
                                String category = readText(scanner, "Категория: ");
                                wallet.checkBudget(category);
                            }

                            case "9" -> {
                                System.out.printf(
                                        "Всего доходов: %s ₽%n", wallet.getTotalIncome());
                                System.out.printf(
                                        "Всего расходов: %s ₽%n", wallet.getTotalExpenses());
                                System.out.printf(
                                        "Текущий баланс: %s ₽%n", wallet.getBalance());

                                if (wallet.getTotalExpenses().compareTo(
                                        wallet.getTotalIncome()) > 0) {
                                    System.out.println(
                                            "Внимание: расходы превышают доходы!");
                                }
                            }

                            case "10" -> {
                                String input = readText(scanner, "Категории через запятую: ");
                                Set<String> categories = new LinkedHashSet<>();

                                for (String category : input.split(",")) {
                                    if (!category.isBlank()) {
                                        categories.add(category.trim());
                                    }
                                }

                                if (categories.isEmpty()) {
                                    System.out.println("Не указано ни одной категории.");
                                } else {
                                    BigDecimal total = BigDecimal.ZERO;
                                    Set<String> known = currentUser.getWallet().getKnownCategories();

                                    for (String category : categories) {
                                        if (!known.contains(category)) {
                                            System.out.println("Категория не найдена: " + category);
                                            continue;
                                        }

                                        BigDecimal expense =
                                                currentUser.getWallet().getExpensesByCategory(category);

                                        total = total.add(expense);
                                        System.out.printf("%s: %s ₽%n", category, expense);
                                    }

                                    System.out.printf(
                                            "Итого по выбранным категориям: %s ₽%n", total);
                                }
                            }

                            case "12" -> {
                                var budgets = wallet.getBudgets();

                                if (budgets.isEmpty()) {
                                    System.out.println("Бюджеты пока не установлены.");
                                } else {
                                    System.out.println("=== ВСЕ БЮДЖЕТЫ ===");

                                    for (var entry : budgets.entrySet()) {
                                        BigDecimal spent =
                                                wallet.getExpensesByCategory(entry.getKey());
                                        BigDecimal remaining = entry.getValue().subtract(spent);

                                        System.out.printf(
                                                "%s | Лимит: %s ₽ | Расходы: %s ₽ | Остаток: %s ₽%n",
                                                entry.getKey(),
                                                entry.getValue(),
                                                spent,
                                                remaining
                                        );
                                    }
                                }
                            }

                            case "11" -> {
                                System.out.println("Выход из аккаунта: "
                                        + currentUser.getLogin());
                                currentUser = null;
                            }

                            default -> System.out.println("Неизвестная команда!");
                        }
                    }
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    private static BigDecimal readAmount(Scanner scanner) {
        String input = readText(scanner, "Введите сумму: ");

        try {
            return new BigDecimal(input.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Введите корректную числовую сумму");
        }
    }

    private static String readText(Scanner scanner, String message) {
        System.out.print(message);

        String value = scanner.nextLine().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException(
                    "Поле не может быть пустым");
        }

        return value;
    }

    private static void printBalance(User user) {
        System.out.printf(
                "Текущий баланс: %s ₽%n",
                user.getWallet().getBalance());
    }
}

