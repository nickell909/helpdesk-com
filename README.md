# Helpdesk System - Система внутренней технической поддержки

Веб-приложение для автоматизации обработки заявок сотрудников в IT-отдел.

## Технологический стек

- **Backend**: Java 17 + Spring Boot 3.2.1
- **Security**: Spring Security + JWT аутентификация
- **ORM**: Spring Data JPA
- **Database**: PostgreSQL 16
- **Frontend**: Thymeleaf + Bootstrap 5
- **Build**: Maven
- **PDF Reports**: iText 8
- **Контейнеризация**: Docker + Docker Compose

## Архитектура

```
Controller → Service → Repository
```

## Роли пользователей

- **USER** — создаёт и отслеживает свои заявки
- **OPERATOR** — обрабатывает заявки, назначает статусы
- **ADMIN** — управляет пользователями, справочниками и отчётами

## Основной функционал

1. **Аутентификация и авторизация**
   - JWT токены для REST API
   - Bcrypt хеширование паролей
   - Ролевая модель доступа

2. **Управление заявками**
   - Создание заявок с категориями и приоритетами
   - Автоматическая генерация номера (TKT-2026-001)
   - Жизненный цикл: Новая → В работе → Решена → Закрыта
   - Назначение оператора
   - Комментарии
   - История изменений

3. **Управление пользователями** (ADMIN)
   - CRUD операции
   - Назначение ролей

4. **Отчёты**
   - PDF отчёты с статистикой
   - Среднее время решения
   - Загрузка операторов

## 📖 Документация

**[Полная документация API →](API_DOCUMENTATION.md)**

Подробная документация содержит:
- Описание всех эндпоинтов с примерами запросов и ответов
- Коды ошибок и их описания
- Примеры использования cURL и HTTPie
- Workflow сценарии (создание заявки, назначение оператора и т.д.)
- Полный список всех доступных API методов

## Структура проекта

```
helpdesk-com/
├── src/
│   ├── main/
│   │   ├── java/com/helpdesk/
│   │   │   ├── config/          # SecurityConfig
│   │   │   ├── controller/      # REST контроллеры
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── entity/          # JPA сущности
│   │   │   ├── repository/      # Spring Data репозитории
│   │   │   ├── security/        # JWT фильтры и UserDetailsService
│   │   │   ├── service/         # Бизнес-логика
│   │   │   ├── util/            # JWT утилиты
│   │   │   └── HelpdeskApplication.java
│   │   └── resources/
│   │       ├── templates/       # Thymeleaf шаблоны
│   │       ├── static/          # CSS, JS
│   │       ├── application.yml  # Конфигурация
│   │       └── data.sql         # Начальные данные
│   └── test/
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Модель данных

### Основные таблицы

- **roles** (role_id, name)
- **users** (user_id, login, password_hash, full_name, email, role_id)
- **categories** (category_id, name)
- **priorities** (priority_id, name)
- **statuses** (status_id, name)
- **tickets** (ticket_id, ticket_number, title, description, category_id, priority_id, status_id, created_by, assigned_to, created_at, updated_at)
- **comments** (comment_id, ticket_id, author_id, content, created_at)
- **ticket_history** (history_id, ticket_id, changed_by, field_name, old_value, new_value, changed_at)

## REST API

### Аутентификация

```http
POST /api/auth/login
Content-Type: application/json

{
  "login": "admin",
  "password": "password"
}
```

Ответ:
```json
{
  "token": "eyJhbGciOiJIUzI1...",
  "type": "Bearer",
  "userId": 1,
  "login": "admin",
  "fullName": "Администратор",
  "role": "ADMIN"
}
```

### Заявки

```http
# Получить все заявки (USER видит только свои)
GET /api/tickets
Authorization: Bearer {token}

# Получить заявку по ID
GET /api/tickets/{id}
Authorization: Bearer {token}

# Создать заявку
POST /api/tickets
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "Не работает принтер",
  "description": "Принтер HP в офисе 301 не печатает",
  "categoryId": 2,
  "priorityId": 2
}

# Изменить статус (OPERATOR/ADMIN)
PUT /api/tickets/{id}/status
Authorization: Bearer {token}
Content-Type: application/json

{
  "statusId": 2
}

# Назначить оператора (OPERATOR/ADMIN)
PUT /api/tickets/{id}/assign
Authorization: Bearer {token}
Content-Type: application/json

{
  "operatorId": 2
}

# Получить мои назначенные заявки (OPERATOR/ADMIN)
GET /api/tickets/my
Authorization: Bearer {token}

# Получить историю изменений заявки
GET /api/tickets/{id}/history
Authorization: Bearer {token}
```

### Комментарии

```http
# Получить комментарии заявки
GET /api/tickets/{ticketId}/comments
Authorization: Bearer {token}

# Добавить комментарий
POST /api/tickets/{ticketId}/comments
Authorization: Bearer {token}
Content-Type: application/json

{
  "content": "Проблема решена, заменил картридж"
}
```

### Пользователи (ADMIN)

```http
# Получить всех пользователей
GET /api/admin/users
Authorization: Bearer {token}

# Создать пользователя
POST /api/admin/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "login": "newuser",
  "password": "password",
  "fullName": "Новый Пользователь",
  "email": "newuser@example.com",
  "roleId": 1
}

# Получить список операторов (OPERATOR/ADMIN)
GET /api/admin/users/operators
Authorization: Bearer {token}
```

### Справочники

```http
GET /api/references/categories
GET /api/references/priorities
GET /api/references/statuses
GET /api/references/roles   # только ADMIN
```

### Отчёты

```http
# Скачать PDF отчёт (OPERATOR/ADMIN)
GET /api/reports/pdf
Authorization: Bearer {token}
```

## Быстрый старт

### Вариант 1: Docker Compose (рекомендуется)

```bash
# Клонировать репозиторий
git clone <repository-url>
cd helpdesk-com

# Запустить приложение
docker-compose up --build

# Приложение будет доступно на http://localhost:8080
```

### Вариант 2: Локальный запуск

#### Требования
- Java 17+
- Maven 3.6+
- PostgreSQL 16+

#### Настройка БД

```sql
CREATE DATABASE helpdesk_db;
CREATE USER helpdesk_user WITH PASSWORD 'helpdesk_pass';
GRANT ALL PRIVILEGES ON DATABASE helpdesk_db TO helpdesk_user;
```

#### Запуск

```bash
# Собрать проект
mvn clean package

# Запустить
java -jar target/helpdesk-1.0.0.jar

# Или через Maven
mvn spring-boot:run
```

## Тестовые пользователи

После запуска доступны следующие пользователи (пароль для всех: `password`):

| Логин | Роль | ФИО | Email |
|-------|------|-----|-------|
| admin | ADMIN | Администратор | admin@helpdesk.local |
| operator1 | OPERATOR | Иванов Иван | operator1@helpdesk.local |
| operator2 | OPERATOR | Петрова Мария | operator2@helpdesk.local |
| user1 | USER | Сидоров Петр | user1@helpdesk.local |
| user2 | USER | Смирнова Анна | user2@helpdesk.local |

## Примеры использования

### 1. Получить JWT токен

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"password"}'
```

### 2. Создать заявку

```bash
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Проблема с почтой",
    "description": "Не могу отправить письмо с вложением",
    "categoryId": 1,
    "priorityId": 2
  }'
```

### 3. Получить отчёт в PDF

```bash
curl -X GET http://localhost:8080/api/reports/pdf \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output report.pdf
```

## Конфигурация

Основные настройки в `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/helpdesk_db
    username: helpdesk_user
    password: helpdesk_pass

server:
  port: 8080

jwt:
  secret: <ваш_секретный_ключ>
  expiration: 86400000  # 24 часа
```

## Разработка

### Запуск БД в Docker

```bash
docker run --name helpdesk-postgres \
  -e POSTGRES_DB=helpdesk_db \
  -e POSTGRES_USER=helpdesk_user \
  -e POSTGRES_PASSWORD=helpdesk_pass \
  -p 5432:5432 \
  -d postgres:16-alpine
```

### Остановка и очистка

```bash
# Остановить контейнеры
docker-compose down

# Удалить данные БД
docker-compose down -v
```

## Troubleshooting

### Ошибка подключения к БД

Убедитесь, что PostgreSQL запущен и доступен:
```bash
docker ps | grep postgres
```

### Ошибка при сборке Maven

Очистите кеш Maven:
```bash
mvn clean install -U
```

### JWT токен невалиден

Проверьте срок действия токена (24 часа). Получите новый токен через `/api/auth/login`.

## Лицензия

MIT

## Автор

Helpdesk System © 2026
