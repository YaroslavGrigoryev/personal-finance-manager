package com.mycompany.finance.service;

import com.mycompany.finance.User;
import com.mycompany.finance.Wallet;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserService {

    private final Map<String, User> users = new HashMap<>();

    public void register(String login, String password) {
        if (users.containsKey(login)) {
            System.out.println("Пользователь с таким логином уже существует!");
            return;
        }

        User user = new User(login, password);
        user.setWallet(new Wallet());
        users.put(login, user);

        System.out.println("Регистрация успешна!");
    }

    public User login(String login, String password) {
        User user = users.get(login);

        if (user != null && user.checkPassword(password)) {
            return user;
        }

        return null;
    }

    public void saveToFile(Path file) {
        try (ObjectOutputStream output =
                     new ObjectOutputStream(Files.newOutputStream(file))) {

            output.writeObject(users);
            System.out.println("Данные сохранены.");

        } catch (IOException e) {
            throw new IllegalStateException("Не удалось сохранить данные", e);
        }
    }

    public void loadFromFile(Path file) {
        if (Files.notExists(file)) {
            return;
        }

        try (ObjectInputStream input =
                     new ObjectInputStream(Files.newInputStream(file))) {

            Object data = input.readObject();

            if (!(data instanceof Map<?, ?> saved)) {
                throw new IOException("Неверный формат файла");
            }

            Map<String, User> restored = new HashMap<>();

            for (Map.Entry<?, ?> entry : saved.entrySet()) {
                if (!(entry.getKey() instanceof String login)
                        || !(entry.getValue() instanceof User user)) {
                    throw new IOException("Повреждённые данные пользователей");
                }

                restored.put(login, user);
            }

            users.clear();
            users.putAll(restored);

            System.out.println("Данные загружены.");

        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Не удалось загрузить данные", e);
        }
    }
}
