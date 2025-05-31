package ru.yandex.practicum.filmorate.util;

public class IdGenerator {

    public static long generateId(long currentId) {
        return ++currentId;
    }
}