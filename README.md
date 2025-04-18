# Лабораторная работа №1
Тема: Создание простейшего REST сервиса
Создание сервиса: 
Разработан простой REST-сервис на Spring Boot, который принимает параметры сложности и длины пароля через queryParams в URL. 
Для запуска используйте путь:
http://localhost:8080/generate-password?length=10&complexity=medium
В сервисе возвращается сгенерированный пароль в формате JSON в зависимости от указанных параметров сложности и длины.

# Лабораторная работа №2
Тема: Подключение базы данных и реализация связей
1. Подключение базы данных: В проект добавлена база данных MySQL, настройка выполнена в файле application.properties.
2. Реализация связей: Для получения данных о пользователях и их паролях реализована связь один ко многим (@OneToMany).
CRUD-операции реализованы для всех сущностей: создание, чтение, обновление и удаление пользователей и их паролей.

# Лабораторная работа №3
Тема: Улучшение сервиса с кастомным запросом и кэшированием
Добавление полезного GET-эндпоинта: В проект добавлен эндпоинт для получения всех пользователей с паролями заданной сложности:
http://localhost:8080/users/by-password-complexity?complexity=high
Данные извлекаются из БД с помощью кастомного запроса (@Query) с параметром, что позволяет фильтровать пользователей по сложности пароля.
Кэширование: Реализован ин-мемори кэш в виде простого бинa CacheService. При создании нового пароля он автоматически добавляется в кэш. Чтобы получить пароль из кэша, выполните запрос:
http://localhost:8080/cache/get?passwordId=15
Замените 15 на нужный ID пароля.

# Лабораторная работа №4
Тема: Обработка ошибок, логирование и использование Swagger и CheckStyle
1. Обработка ошибок
В проекте добавлена обработка ошибок:
Ошибка 400 (Bad Request)
Ошибка 500 (Internal Server Error) 
2. Логирование
Для логирования используется Spring AOP. 
3. Swagger
Swagger подключен для автоматической генерации документации API.
4. CheckStyle
Подключен CheckStyle для проверки кода на соответствие стилю. 



