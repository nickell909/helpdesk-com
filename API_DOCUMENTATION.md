# Helpdesk API Documentation

Полная документация REST API для системы Helpdesk.

## Базовый URL

```
http://localhost:8080/api
```

## Аутентификация

Все эндпоинты (кроме `/api/auth/login`) требуют JWT токен в заголовке:

```
Authorization: Bearer <your_jwt_token>
```

---

## 1. Аутентификация

### POST /api/auth/login

Аутентификация пользователя и получение JWT токена.

**Доступ:** Публичный (без токена)

**Request Body:**
```json
{
  "login": "admin",
  "password": "password"
}
```

**Response 200 OK:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "userId": 1,
  "login": "admin",
  "fullName": "Администратор",
  "role": "ADMIN"
}
```

**Response 401 Unauthorized:**
```json
{
  "timestamp": "2026-01-19T10:00:00.000+00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials",
  "path": "/api/auth/login"
}
```

**Примеры:**

```bash
# cURL
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"password"}'

# HTTPie
http POST http://localhost:8080/api/auth/login \
  login=admin password=password
```

---

## 2. Заявки (Tickets)

### GET /api/tickets

Получить список всех заявок.

**Доступ:** USER, OPERATOR, ADMIN
- USER видит только свои заявки
- OPERATOR и ADMIN видят все заявки

**Response 200 OK:**
```json
[
  {
    "ticketId": 1,
    "ticketNumber": "TKT-2026-001",
    "title": "Не работает принтер",
    "description": "Принтер HP в офисе 301 не печатает",
    "category": {
      "categoryId": 2,
      "name": "Оборудование"
    },
    "priority": {
      "priorityId": 2,
      "name": "Средний"
    },
    "status": {
      "statusId": 1,
      "name": "Новая"
    },
    "createdBy": {
      "userId": 4,
      "login": "user1",
      "fullName": "Сидоров Петр",
      "email": "user1@helpdesk.local",
      "role": {
        "roleId": 1,
        "name": "USER"
      }
    },
    "assignedTo": null,
    "createdAt": "2026-01-19T10:30:00",
    "updatedAt": "2026-01-19T10:30:00"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/tickets \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/tickets/{id}

Получить заявку по ID.

**Доступ:** USER, OPERATOR, ADMIN
- USER может видеть только свои заявки
- OPERATOR и ADMIN видят все заявки

**Path Parameters:**
- `id` (Long) - ID заявки

**Response 200 OK:**
```json
{
  "ticketId": 1,
  "ticketNumber": "TKT-2026-001",
  "title": "Не работает принтер",
  "description": "Принтер HP в офисе 301 не печатает",
  "category": {
    "categoryId": 2,
    "name": "Оборудование"
  },
  "priority": {
    "priorityId": 2,
    "name": "Средний"
  },
  "status": {
    "statusId": 1,
    "name": "Новая"
  },
  "createdBy": {
    "userId": 4,
    "login": "user1",
    "fullName": "Сидоров Петр",
    "email": "user1@helpdesk.local",
    "role": {
      "roleId": 1,
      "name": "USER"
    }
  },
  "assignedTo": null,
  "createdAt": "2026-01-19T10:30:00",
  "updatedAt": "2026-01-19T10:30:00"
}
```

**Response 403 Forbidden:**
```json
{
  "timestamp": "2026-01-19T10:00:00.000+00:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access Denied",
  "path": "/api/tickets/1"
}
```

**Response 404 Not Found:**
```json
{
  "timestamp": "2026-01-19T10:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Заявка не найдена: 999",
  "path": "/api/tickets/999"
}
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/tickets/1 \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/tickets/1 \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### POST /api/tickets

Создать новую заявку.

**Доступ:** USER, OPERATOR, ADMIN

**Request Body:**
```json
{
  "title": "Проблема с почтой",
  "description": "Не могу отправить письмо с вложением больше 10MB",
  "categoryId": 1,
  "priorityId": 2
}
```

**Поля:**
- `title` (String, обязательно) - Заголовок заявки (макс 200 символов)
- `description` (String, обязательно) - Описание проблемы
- `categoryId` (Long, обязательно) - ID категории (см. `/api/references/categories`)
- `priorityId` (Long, обязательно) - ID приоритета (см. `/api/references/priorities`)

**Response 201 Created:**
```json
{
  "ticketId": 2,
  "ticketNumber": "TKT-2026-002",
  "title": "Проблема с почтой",
  "description": "Не могу отправить письмо с вложением больше 10MB",
  "category": {
    "categoryId": 1,
    "name": "Программное обеспечение"
  },
  "priority": {
    "priorityId": 2,
    "name": "Средний"
  },
  "status": {
    "statusId": 1,
    "name": "Новая"
  },
  "createdBy": {
    "userId": 4,
    "login": "user1",
    "fullName": "Сидоров Петр",
    "email": "user1@helpdesk.local",
    "role": {
      "roleId": 1,
      "name": "USER"
    }
  },
  "assignedTo": null,
  "createdAt": "2026-01-19T11:00:00",
  "updatedAt": "2026-01-19T11:00:00"
}
```

**Response 400 Bad Request:**
```json
{
  "timestamp": "2026-01-19T10:00:00.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/tickets"
}
```

**Примеры:**

```bash
# cURL
curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Проблема с почтой",
    "description": "Не могу отправить письмо с вложением",
    "categoryId": 1,
    "priorityId": 2
  }'

# HTTPie
http POST http://localhost:8080/api/tickets \
  "Authorization:Bearer YOUR_TOKEN" \
  title="Проблема с почтой" \
  description="Не могу отправить письмо с вложением" \
  categoryId:=1 \
  priorityId:=2
```

---

### PUT /api/tickets/{id}/status

Изменить статус заявки.

**Доступ:** OPERATOR, ADMIN

**Path Parameters:**
- `id` (Long) - ID заявки

**Request Body:**
```json
{
  "statusId": 2
}
```

**Поля:**
- `statusId` (Long, обязательно) - ID нового статуса (см. `/api/references/statuses`)
  - 1 - Новая
  - 2 - В работе
  - 3 - Решена
  - 4 - Закрыта

**Response 200 OK:**
```json
{
  "ticketId": 1,
  "ticketNumber": "TKT-2026-001",
  "title": "Не работает принтер",
  "status": {
    "statusId": 2,
    "name": "В работе"
  },
  "updatedAt": "2026-01-19T11:15:00",
  ...
}
```

**Примеры:**

```bash
# cURL
curl -X PUT http://localhost:8080/api/tickets/1/status \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"statusId": 2}'

# HTTPie
http PUT http://localhost:8080/api/tickets/1/status \
  "Authorization:Bearer YOUR_TOKEN" \
  statusId:=2
```

---

### PUT /api/tickets/{id}/assign

Назначить заявку оператору.

**Доступ:** OPERATOR, ADMIN

**Path Parameters:**
- `id` (Long) - ID заявки

**Request Body:**
```json
{
  "operatorId": 2
}
```

**Поля:**
- `operatorId` (Long, обязательно) - ID оператора (см. `/api/admin/users/operators`)

**Response 200 OK:**
```json
{
  "ticketId": 1,
  "ticketNumber": "TKT-2026-001",
  "title": "Не работает принтер",
  "status": {
    "statusId": 2,
    "name": "В работе"
  },
  "assignedTo": {
    "userId": 2,
    "login": "operator1",
    "fullName": "Иванов Иван",
    "email": "operator1@helpdesk.local"
  },
  "updatedAt": "2026-01-19T11:20:00",
  ...
}
```

**Примеры:**

```bash
# cURL
curl -X PUT http://localhost:8080/api/tickets/1/assign \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"operatorId": 2}'

# HTTPie
http PUT http://localhost:8080/api/tickets/1/assign \
  "Authorization:Bearer YOUR_TOKEN" \
  operatorId:=2
```

---

### GET /api/tickets/my

Получить заявки, назначенные мне (как оператору).

**Доступ:** OPERATOR, ADMIN

**Response 200 OK:**
```json
[
  {
    "ticketId": 1,
    "ticketNumber": "TKT-2026-001",
    "title": "Не работает принтер",
    "assignedTo": {
      "userId": 2,
      "login": "operator1",
      "fullName": "Иванов Иван"
    },
    ...
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/tickets/my \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/tickets/my \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/tickets/{id}/history

Получить историю изменений заявки.

**Доступ:** USER, OPERATOR, ADMIN
- USER может видеть историю только своих заявок

**Path Parameters:**
- `id` (Long) - ID заявки

**Response 200 OK:**
```json
[
  {
    "historyId": 3,
    "ticket": {
      "ticketId": 1
    },
    "changedBy": {
      "userId": 2,
      "login": "operator1",
      "fullName": "Иванов Иван"
    },
    "fieldName": "status",
    "oldValue": "Новая",
    "newValue": "В работе",
    "changedAt": "2026-01-19T11:15:00"
  },
  {
    "historyId": 2,
    "ticket": {
      "ticketId": 1
    },
    "changedBy": {
      "userId": 2,
      "login": "operator1",
      "fullName": "Иванов Иван"
    },
    "fieldName": "assigned_to",
    "oldValue": "Не назначен",
    "newValue": "Иванов Иван",
    "changedAt": "2026-01-19T11:10:00"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/tickets/1/history \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/tickets/1/history \
  "Authorization:Bearer YOUR_TOKEN"
```

---

## 3. Комментарии

### GET /api/tickets/{ticketId}/comments

Получить комментарии заявки.

**Доступ:** USER, OPERATOR, ADMIN
- USER может видеть комментарии только своих заявок

**Path Parameters:**
- `ticketId` (Long) - ID заявки

**Response 200 OK:**
```json
[
  {
    "commentId": 1,
    "ticket": {
      "ticketId": 1
    },
    "author": {
      "userId": 2,
      "login": "operator1",
      "fullName": "Иванов Иван"
    },
    "content": "Проверил принтер, проблема в замятии бумаги",
    "createdAt": "2026-01-19T11:30:00"
  },
  {
    "commentId": 2,
    "ticket": {
      "ticketId": 1
    },
    "author": {
      "userId": 2,
      "login": "operator1",
      "fullName": "Иванов Иван"
    },
    "content": "Замятие устранено, принтер работает",
    "createdAt": "2026-01-19T11:45:00"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/tickets/1/comments \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/tickets/1/comments \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### POST /api/tickets/{ticketId}/comments

Добавить комментарий к заявке.

**Доступ:** USER, OPERATOR, ADMIN
- USER может комментировать только свои заявки

**Path Parameters:**
- `ticketId` (Long) - ID заявки

**Request Body:**
```json
{
  "content": "Спасибо, принтер теперь работает!"
}
```

**Поля:**
- `content` (String, обязательно) - Текст комментария (не пустой)

**Response 201 Created:**
```json
{
  "commentId": 3,
  "ticket": {
    "ticketId": 1
  },
  "author": {
    "userId": 4,
    "login": "user1",
    "fullName": "Сидоров Петр"
  },
  "content": "Спасибо, принтер теперь работает!",
  "createdAt": "2026-01-19T12:00:00"
}
```

**Примеры:**

```bash
# cURL
curl -X POST http://localhost:8080/api/tickets/1/comments \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Спасибо, принтер теперь работает!"}'

# HTTPie
http POST http://localhost:8080/api/tickets/1/comments \
  "Authorization:Bearer YOUR_TOKEN" \
  content="Спасибо, принтер теперь работает!"
```

---

## 4. Пользователи

### GET /api/admin/users

Получить список всех пользователей.

**Доступ:** ADMIN

**Response 200 OK:**
```json
[
  {
    "userId": 1,
    "login": "admin",
    "fullName": "Администратор",
    "email": "admin@helpdesk.local",
    "role": {
      "roleId": 3,
      "name": "ADMIN"
    }
  },
  {
    "userId": 2,
    "login": "operator1",
    "fullName": "Иванов Иван",
    "email": "operator1@helpdesk.local",
    "role": {
      "roleId": 2,
      "name": "OPERATOR"
    }
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/admin/users \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/admin/users/{id}

Получить пользователя по ID.

**Доступ:** ADMIN

**Path Parameters:**
- `id` (Long) - ID пользователя

**Response 200 OK:**
```json
{
  "userId": 2,
  "login": "operator1",
  "fullName": "Иванов Иван",
  "email": "operator1@helpdesk.local",
  "role": {
    "roleId": 2,
    "name": "OPERATOR"
  }
}
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/admin/users/2 \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/admin/users/2 \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### POST /api/admin/users

Создать нового пользователя.

**Доступ:** ADMIN

**Request Body:**
```json
{
  "login": "newuser",
  "password": "securePassword123",
  "fullName": "Новый Пользователь",
  "email": "newuser@example.com",
  "roleId": 1
}
```

**Поля:**
- `login` (String, обязательно) - Уникальный логин
- `password` (String, обязательно) - Пароль (будет хеширован bcrypt)
- `fullName` (String, обязательно) - Полное имя
- `email` (String, обязательно) - Email (уникальный, валидный формат)
- `roleId` (Long, обязательно) - ID роли:
  - 1 - USER
  - 2 - OPERATOR
  - 3 - ADMIN

**Response 201 Created:**
```json
{
  "userId": 6,
  "login": "newuser",
  "fullName": "Новый Пользователь",
  "email": "newuser@example.com",
  "role": {
    "roleId": 1,
    "name": "USER"
  }
}
```

**Response 400/500:**
```json
{
  "timestamp": "2026-01-19T10:00:00.000+00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Логин уже занят: newuser",
  "path": "/api/admin/users"
}
```

**Примеры:**

```bash
# cURL
curl -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "newuser",
    "password": "securePassword123",
    "fullName": "Новый Пользователь",
    "email": "newuser@example.com",
    "roleId": 1
  }'

# HTTPie
http POST http://localhost:8080/api/admin/users \
  "Authorization:Bearer YOUR_TOKEN" \
  login=newuser \
  password=securePassword123 \
  fullName="Новый Пользователь" \
  email=newuser@example.com \
  roleId:=1
```

---

### PUT /api/admin/users/{id}

Обновить пользователя.

**Доступ:** ADMIN

**Path Parameters:**
- `id` (Long) - ID пользователя

**Request Body:**
```json
{
  "login": "newuser",
  "password": "",
  "fullName": "Обновленное Имя",
  "email": "newemail@example.com",
  "roleId": 2
}
```

**Поля:**
- `login` (String, обязательно) - Логин
- `password` (String, опционально) - Новый пароль (пустая строка = не менять)
- `fullName` (String, обязательно) - Полное имя
- `email` (String, обязательно) - Email
- `roleId` (Long, обязательно) - ID роли

**Response 200 OK:**
```json
{
  "userId": 6,
  "login": "newuser",
  "fullName": "Обновленное Имя",
  "email": "newemail@example.com",
  "role": {
    "roleId": 2,
    "name": "OPERATOR"
  }
}
```

**Примеры:**

```bash
# cURL
curl -X PUT http://localhost:8080/api/admin/users/6 \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "newuser",
    "password": "",
    "fullName": "Обновленное Имя",
    "email": "newemail@example.com",
    "roleId": 2
  }'

# HTTPie
http PUT http://localhost:8080/api/admin/users/6 \
  "Authorization:Bearer YOUR_TOKEN" \
  login=newuser \
  password="" \
  fullName="Обновленное Имя" \
  email=newemail@example.com \
  roleId:=2
```

---

### DELETE /api/admin/users/{id}

Удалить пользователя.

**Доступ:** ADMIN

**Path Parameters:**
- `id` (Long) - ID пользователя

**Response 204 No Content**

**Примеры:**

```bash
# cURL
curl -X DELETE http://localhost:8080/api/admin/users/6 \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http DELETE http://localhost:8080/api/admin/users/6 \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/admin/users/operators

Получить список операторов (OPERATOR и ADMIN).

**Доступ:** OPERATOR, ADMIN

**Response 200 OK:**
```json
[
  {
    "userId": 1,
    "login": "admin",
    "fullName": "Администратор",
    "email": "admin@helpdesk.local",
    "role": {
      "roleId": 3,
      "name": "ADMIN"
    }
  },
  {
    "userId": 2,
    "login": "operator1",
    "fullName": "Иванов Иван",
    "email": "operator1@helpdesk.local",
    "role": {
      "roleId": 2,
      "name": "OPERATOR"
    }
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/admin/users/operators \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/admin/users/operators \
  "Authorization:Bearer YOUR_TOKEN"
```

---

## 5. Справочники

### GET /api/references/categories

Получить список категорий заявок.

**Доступ:** USER, OPERATOR, ADMIN

**Response 200 OK:**
```json
[
  {
    "categoryId": 1,
    "name": "Программное обеспечение"
  },
  {
    "categoryId": 2,
    "name": "Оборудование"
  },
  {
    "categoryId": 3,
    "name": "Сеть"
  },
  {
    "categoryId": 4,
    "name": "Доступы"
  },
  {
    "categoryId": 5,
    "name": "Прочее"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/references/categories \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/references/categories \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/references/priorities

Получить список приоритетов заявок.

**Доступ:** USER, OPERATOR, ADMIN

**Response 200 OK:**
```json
[
  {
    "priorityId": 1,
    "name": "Низкий"
  },
  {
    "priorityId": 2,
    "name": "Средний"
  },
  {
    "priorityId": 3,
    "name": "Высокий"
  },
  {
    "priorityId": 4,
    "name": "Критический"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/references/priorities \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/references/priorities \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/references/statuses

Получить список статусов заявок.

**Доступ:** USER, OPERATOR, ADMIN

**Response 200 OK:**
```json
[
  {
    "statusId": 1,
    "name": "Новая"
  },
  {
    "statusId": 2,
    "name": "В работе"
  },
  {
    "statusId": 3,
    "name": "Решена"
  },
  {
    "statusId": 4,
    "name": "Закрыта"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/references/statuses \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/references/statuses \
  "Authorization:Bearer YOUR_TOKEN"
```

---

### GET /api/references/roles

Получить список ролей пользователей.

**Доступ:** ADMIN

**Response 200 OK:**
```json
[
  {
    "roleId": 1,
    "name": "USER"
  },
  {
    "roleId": 2,
    "name": "OPERATOR"
  },
  {
    "roleId": 3,
    "name": "ADMIN"
  }
]
```

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/references/roles \
  -H "Authorization: Bearer YOUR_TOKEN"

# HTTPie
http GET http://localhost:8080/api/references/roles \
  "Authorization:Bearer YOUR_TOKEN"
```

---

## 6. Отчёты

### GET /api/reports/pdf

Сгенерировать и скачать PDF отчёт.

**Доступ:** OPERATOR, ADMIN

**Response 200 OK:**
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="helpdesk-report-YYYYMMDD-HHMMSS.pdf"`
- Binary PDF content

**Отчёт содержит:**
- Общее количество заявок
- Статистика по статусам
- Статистика по приоритетам
- Статистика по категориям
- Среднее время решения заявок
- Загрузка операторов

**Примеры:**

```bash
# cURL
curl -X GET http://localhost:8080/api/reports/pdf \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output report.pdf

# HTTPie
http --download GET http://localhost:8080/api/reports/pdf \
  "Authorization:Bearer YOUR_TOKEN"
```

---

## Коды ошибок

| Код | Описание |
|-----|----------|
| 200 | OK - Запрос выполнен успешно |
| 201 | Created - Ресурс создан |
| 204 | No Content - Запрос выполнен, но нет содержимого для возврата |
| 400 | Bad Request - Некорректный запрос |
| 401 | Unauthorized - Требуется аутентификация |
| 403 | Forbidden - Доступ запрещён |
| 404 | Not Found - Ресурс не найден |
| 500 | Internal Server Error - Внутренняя ошибка сервера |

---

## Workflow примеры

### Пример 1: Создание и обработка заявки

```bash
# 1. Пользователь входит в систему
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"user1","password":"password"}' \
  | jq -r '.token')

# 2. Пользователь создаёт заявку
TICKET_ID=$(curl -X POST http://localhost:8080/api/tickets \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Не работает принтер",
    "description": "Принтер HP в офисе 301 не печатает",
    "categoryId": 2,
    "priorityId": 2
  }' | jq -r '.ticketId')

# 3. Оператор входит в систему
OPERATOR_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"operator1","password":"password"}' \
  | jq -r '.token')

# 4. Оператор назначает заявку себе
curl -X PUT http://localhost:8080/api/tickets/$TICKET_ID/assign \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"operatorId": 2}'

# 5. Оператор добавляет комментарий
curl -X POST http://localhost:8080/api/tickets/$TICKET_ID/comments \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Проверил принтер, проблема в замятии бумаги"}'

# 6. Оператор меняет статус на "Решена"
curl -X PUT http://localhost:8080/api/tickets/$TICKET_ID/status \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"statusId": 3}'

# 7. Пользователь добавляет комментарий
curl -X POST http://localhost:8080/api/tickets/$TICKET_ID/comments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"content": "Спасибо, всё работает!"}'

# 8. Оператор закрывает заявку
curl -X PUT http://localhost:8080/api/tickets/$TICKET_ID/status \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"statusId": 4}'

# 9. Просмотр истории изменений
curl -X GET http://localhost:8080/api/tickets/$TICKET_ID/history \
  -H "Authorization: Bearer $TOKEN"
```

---

### Пример 2: Администратор создаёт нового оператора

```bash
# 1. Админ входит в систему
ADMIN_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"admin","password":"password"}' \
  | jq -r '.token')

# 2. Получает список ролей
curl -X GET http://localhost:8080/api/references/roles \
  -H "Authorization: Bearer $ADMIN_TOKEN"

# 3. Создаёт нового оператора
curl -X POST http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "login": "operator3",
    "password": "password",
    "fullName": "Кузнецов Алексей",
    "email": "operator3@helpdesk.local",
    "roleId": 2
  }'

# 4. Получает список всех операторов
curl -X GET http://localhost:8080/api/admin/users/operators \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

---

### Пример 3: Генерация отчёта

```bash
# 1. Оператор входит в систему
OPERATOR_TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"login":"operator1","password":"password"}' \
  | jq -r '.token')

# 2. Генерирует PDF отчёт
curl -X GET http://localhost:8080/api/reports/pdf \
  -H "Authorization: Bearer $OPERATOR_TOKEN" \
  --output helpdesk-report.pdf

echo "Отчёт сохранён в helpdesk-report.pdf"
```

---

## Полный список эндпоинтов

| Метод | Путь | Доступ | Описание |
|-------|------|--------|----------|
| POST | /api/auth/login | Public | Аутентификация |
| GET | /api/tickets | USER, OPERATOR, ADMIN | Список заявок |
| GET | /api/tickets/{id} | USER, OPERATOR, ADMIN | Заявка по ID |
| POST | /api/tickets | USER, OPERATOR, ADMIN | Создать заявку |
| PUT | /api/tickets/{id}/status | OPERATOR, ADMIN | Изменить статус |
| PUT | /api/tickets/{id}/assign | OPERATOR, ADMIN | Назначить оператора |
| GET | /api/tickets/my | OPERATOR, ADMIN | Мои назначенные заявки |
| GET | /api/tickets/{id}/history | USER, OPERATOR, ADMIN | История изменений |
| GET | /api/tickets/{id}/comments | USER, OPERATOR, ADMIN | Комментарии заявки |
| POST | /api/tickets/{id}/comments | USER, OPERATOR, ADMIN | Добавить комментарий |
| GET | /api/admin/users | ADMIN | Все пользователи |
| GET | /api/admin/users/{id} | ADMIN | Пользователь по ID |
| POST | /api/admin/users | ADMIN | Создать пользователя |
| PUT | /api/admin/users/{id} | ADMIN | Обновить пользователя |
| DELETE | /api/admin/users/{id} | ADMIN | Удалить пользователя |
| GET | /api/admin/users/operators | OPERATOR, ADMIN | Список операторов |
| GET | /api/references/categories | USER, OPERATOR, ADMIN | Категории |
| GET | /api/references/priorities | USER, OPERATOR, ADMIN | Приоритеты |
| GET | /api/references/statuses | USER, OPERATOR, ADMIN | Статусы |
| GET | /api/references/roles | ADMIN | Роли |
| GET | /api/reports/pdf | OPERATOR, ADMIN | PDF отчёт |

---

## Поддержка

По всем вопросам обращайтесь к администратору системы.
