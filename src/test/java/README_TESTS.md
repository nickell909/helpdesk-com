# Автотесты Helpdesk API

## Описание

Комплексные интеграционные тесты для REST API системы Helpdesk.

## Структура тестов

```
src/test/java/com/helpdesk/
├── BaseIntegrationTest.java          # Базовый класс для всех тестов
└── controller/
    ├── AuthControllerTest.java       # Тесты аутентификации (7 тестов)
    ├── TicketControllerTest.java     # Тесты заявок (11 тестов)
    ├── CommentControllerTest.java    # Тесты комментариев (5 тестов)
    ├── UserControllerTest.java       # Тесты пользователей (9 тестов)
    ├── ReferenceControllerTest.java  # Тесты справочников (7 тестов)
    └── ReportControllerTest.java     # Тесты отчетов (5 тестов)
```

**Всего: 44 теста**

## Технологии

- **JUnit 5** - фреймворк для тестирования
- **Spring Boot Test** - интеграционное тестирование Spring приложений
- **MockMvc** - тестирование REST API
- **H2 Database** - in-memory БД для тестов
- **Jackson** - JSON сериализация/десериализация

## Запуск тестов

### Запуск всех тестов

```bash
mvn test
```

### Запуск конкретного теста

```bash
mvn test -Dtest=AuthControllerTest
mvn test -Dtest=TicketControllerTest
```

### Запуск с отчетом покрытия

```bash
mvn test jacoco:report
```

## Описание тестовых классов

### BaseIntegrationTest

Базовый абстрактный класс для всех интеграционных тестов.

**Возможности:**
- Автоматическая настройка тестовой БД (H2) перед каждым тестом
- Создание тестовых пользователей (admin, operator, user)
- Создание справочников (категории, приоритеты, статусы)
- Helper методы для получения JWT токенов

**Тестовые пользователи:**
- `admin` / `password` - роль ADMIN
- `operator` / `password` - роль OPERATOR
- `user` / `password` - роль USER

### AuthControllerTest (7 тестов)

Тесты аутентификации и авторизации.

**Покрытие:**
- ✅ Успешная аутентификация для всех ролей
- ✅ Неверный пароль
- ✅ Несуществующий пользователь
- ✅ Валидация пустых полей
- ✅ Проверка структуры JWT ответа

### TicketControllerTest (11 тестов)

Тесты CRUD операций с заявками.

**Покрытие:**
- ✅ Создание заявки с автогенерацией номера
- ✅ Получение списка заявок с учетом ролей
- ✅ Получение заявки по ID
- ✅ Изменение статуса (только OPERATOR/ADMIN)
- ✅ Назначение оператора
- ✅ Получение назначенных заявок
- ✅ История изменений
- ✅ Проверка прав доступа (пользователь видит только свои заявки)
- ✅ Обработка несуществующих заявок

### CommentControllerTest (5 тестов)

Тесты системы комментариев.

**Покрытие:**
- ✅ Добавление комментария к своей заявке
- ✅ Получение списка комментариев
- ✅ Валидация пустого комментария
- ✅ Запрет комментирования чужих заявок
- ✅ Требование авторизации

### UserControllerTest (9 тестов)

Тесты управления пользователями (ADMIN).

**Покрытие:**
- ✅ Получение списка всех пользователей
- ✅ Получение пользователя по ID
- ✅ Создание нового пользователя
- ✅ Обновление пользователя
- ✅ Удаление пользователя
- ✅ Получение списка операторов
- ✅ Проверка прав доступа (только ADMIN)
- ✅ Валидация дублирования логина/email

### ReferenceControllerTest (7 тестов)

Тесты получения справочных данных.

**Покрытие:**
- ✅ Получение категорий
- ✅ Получение приоритетов
- ✅ Получение статусов
- ✅ Получение ролей (только ADMIN)
- ✅ Проверка доступа для разных ролей
- ✅ Требование авторизации

### ReportControllerTest (5 тестов)

Тесты генерации PDF отчетов.

**Покрытие:**
- ✅ Генерация PDF отчета
- ✅ Проверка Content-Type и заголовков
- ✅ Доступ только для OPERATOR/ADMIN
- ✅ Генерация отчета с пустой БД
- ✅ Требование авторизации

## Примеры тестов

### Пример 1: Тест успешной аутентификации

```java
@Test
@DisplayName("POST /api/auth/login - успешная аутентификация администратора")
public void testLoginSuccess_Admin() throws Exception {
    LoginRequest request = new LoginRequest();
    request.setLogin("admin");
    request.setPassword("password");

    mockMvc.perform(post("/api/auth/login")
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.role").value("ADMIN"));
}
```

### Пример 2: Тест создания заявки

```java
@Test
@DisplayName("POST /api/tickets - создание заявки пользователем")
public void testCreateTicket_Success() throws Exception {
    String token = getUserToken();

    TicketRequest request = new TicketRequest();
    request.setTitle("Не работает принтер");
    request.setDescription("Принтер HP в офисе 301 не печатает");
    request.setCategoryId(hardwareCategory.getCategoryId());
    request.setPriorityId(mediumPriority.getPriorityId());

    mockMvc.perform(post("/api/tickets")
                    .header("Authorization", "Bearer " + token)
                    .contentType("application/json")
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.ticketNumber").exists())
            .andExpect(jsonPath("$.status.name").value("Новая"));
}
```

### Пример 3: Тест проверки прав доступа

```java
@Test
@DisplayName("GET /api/admin/users - доступ запрещен для обычного пользователя")
public void testGetAllUsers_ForbiddenForUser() throws Exception {
    String token = getUserToken();

    mockMvc.perform(get("/api/admin/users")
                    .header("Authorization", "Bearer " + token))
            .andExpect(status().isForbidden());
}
```

## Тестовая БД

Тесты используют in-memory H2 database, которая:
- Создается перед каждым тестом
- Очищается после каждого теста
- Использует тот же JPA маппинг, что и production

**Конфигурация:** `src/test/resources/application-test.yml`

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

## CI/CD Integration

Тесты можно легко интегрировать в CI/CD pipeline:

### GitHub Actions

```yaml
- name: Run tests
  run: mvn test

- name: Publish test results
  uses: EnricoMi/publish-unit-test-result-action@v2
  if: always()
  with:
    files: target/surefire-reports/*.xml
```

### GitLab CI

```yaml
test:
  stage: test
  script:
    - mvn test
  artifacts:
    reports:
      junit: target/surefire-reports/*.xml
```

## Лучшие практики

1. **Изоляция тестов** - каждый тест независим и очищает БД
2. **Понятные названия** - используется `@DisplayName` на русском
3. **Тестирование граничных случаев** - пустые значения, несуществующие ID
4. **Проверка безопасности** - тесты прав доступа для каждой роли
5. **Полное покрытие** - тестируются все HTTP методы и статус коды

## Покрытие кода

Для измерения покрытия кода используйте JaCoCo:

```bash
mvn clean test jacoco:report
```

Отчет будет доступен в `target/site/jacoco/index.html`

## Troubleshooting

### Ошибка: "Failed to load ApplicationContext"

**Причина:** Проблемы с конфигурацией Spring Boot

**Решение:** Проверьте `application-test.yml` и убедитесь, что H2 в dependencies

### Ошибка: "401 Unauthorized" в тестах

**Причина:** JWT токен не передан или невалиден

**Решение:** Используйте helper методы `getAdminToken()`, `getOperatorToken()`, `getUserToken()`

### Тесты падают случайно

**Причина:** Недостаточная изоляция или порядок выполнения

**Решение:** Убедитесь, что `@BeforeEach` очищает БД правильно

## Расширение тестов

Для добавления новых тестов:

1. Создайте класс-наследник `BaseIntegrationTest`
2. Используйте `@DisplayName` для описания теста
3. Используйте helper методы для получения токенов
4. Проверяйте все граничные случаи

## Метрики

- **Всего тестов:** 44
- **Время выполнения:** ~10-15 секунд
- **Покрытие кода:** >80% (при использовании JaCoCo)

## Контакты

По вопросам тестирования обращайтесь к команде разработки.
