![Схема базы данных](/db-scheme.png)
## Основные запросы
### Получение всех фильмов
```sql
SELECT film.id,
    film.name,
    film.description,
    film.release_date,
    film.duration,
    rating.name
FROM film AS film
LEFT OUTER JOIN mpa_rating AS rating ON film.mpa_rating_id = rating.id;
```
### Получение всех пользователей
```sql
SELECT email,
    login,
    name,
    birthday
FROM user;
```
### Получение топ N наиболее популярных фильмов
```sql
SELECT film.id,
    film.name,
    film.description,
    film.release_date,
    film.duration,
    rating.name,
    COUNT(likes.film_id) AS popularity
FROM user_film_like AS likes
LEFT OUTER JOIN film AS film ON likes.film_id = film.id
LEFT OUTER JOIN mpa_rating AS rating ON film.mpa_rating_id = rating.id
GROUP BY film.id, 
    film.name, 
    film.description, 
    film.release_date, 
    film.duration, 
    rating.name
ORDER BY popularity DESC
LIMIT N; -- нужно подставить конкретное число
```
### Получение общих друзей с другим пользователем
```sql
SELECT email, 
    login, 
    name, 
    birthday
FROM user
WHERE id IN (
    SELECT friend_id
    FROM friendship
    WHERE user_id IN (id_первого_пользователя, id_второго_пользователя) -- нужно подставить конкретные id
        AND friendship_status_id = id_статуса_подтверждённая -- нужно подставить конкретное id
    GROUP BY friend_id
    HAVING COUNT(DISTINCT user_id) = 2
);
```