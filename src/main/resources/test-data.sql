-- Дополнительные тестовые данные для демонстрации функциональности
-- Этот файл можно запустить вручную для наполнения БД тестовыми данными

-- Дополнительные пользователи
INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'operator3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Козлов Дмитрий', 'operator3@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'OPERATOR'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'user3', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Новикова Елена', 'user3@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'USER'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'user4', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Морозов Алексей', 'user4@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'USER'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'user5', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Волкова Ольга', 'user5@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'USER'
ON CONFLICT (login) DO NOTHING;

-- Тестовые заявки
-- Заявка 1: Новая, критический приоритет
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0001',
    'Не работает интернет во всем офисе',
    'После обеда пропало подключение к интернету. Проверили роутер - индикаторы мигают. Срочно нужна помощь, так как остановилась вся работа.',
    (SELECT category_id FROM categories WHERE name = 'Сеть'),
    (SELECT priority_id FROM priorities WHERE name = 'Критический'),
    (SELECT status_id FROM statuses WHERE name = 'В работе'),
    (SELECT user_id FROM users WHERE login = 'user1'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '2 hours',
    NOW() - INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0001');

-- Заявка 2: Решена
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0002',
    'Не запускается MS Word',
    'При попытке открыть Word выдает ошибку "Приложение не может быть запущено". Переустановка не помогла.',
    (SELECT category_id FROM categories WHERE name = 'Программное обеспечение'),
    (SELECT priority_id FROM priorities WHERE name = 'Высокий'),
    (SELECT status_id FROM statuses WHERE name = 'Решена'),
    (SELECT user_id FROM users WHERE login = 'user2'),
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '1 day',
    NOW() - INTERVAL '6 hours'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002');

-- Заявка 3: Закрыта
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0003',
    'Запрос доступа к общей папке',
    'Прошу предоставить доступ к папке \\server\docs\finance для работы с финансовыми документами.',
    (SELECT category_id FROM categories WHERE name = 'Доступы'),
    (SELECT priority_id FROM priorities WHERE name = 'Средний'),
    (SELECT status_id FROM statuses WHERE name = 'Закрыта'),
    (SELECT user_id FROM users WHERE login = 'user3'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '3 days',
    NOW() - INTERVAL '2 days'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003');

-- Заявка 4: Новая, не назначена
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, created_at, updated_at)
SELECT
    'TKT-2026-0004',
    'Не печатает принтер HP на 3 этаже',
    'Принтер HP LaserJet в переговорной комнате не печатает. Бумага есть, тонер показывает полный.',
    (SELECT category_id FROM categories WHERE name = 'Оборудование'),
    (SELECT priority_id FROM priorities WHERE name = 'Средний'),
    (SELECT status_id FROM statuses WHERE name = 'Новая'),
    (SELECT user_id FROM users WHERE login = 'user4'),
    NOW() - INTERVAL '3 hours',
    NOW() - INTERVAL '3 hours'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0004');

-- Заявка 5: В работе
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0005',
    'Монитор мерцает и гаснет',
    'Монитор на рабочем месте периодически гаснет на несколько секунд, затем включается снова. Очень мешает работе.',
    (SELECT category_id FROM categories WHERE name = 'Оборудование'),
    (SELECT priority_id FROM priorities WHERE name = 'Высокий'),
    (SELECT status_id FROM statuses WHERE name = 'В работе'),
    (SELECT user_id FROM users WHERE login = 'user5'),
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '5 hours',
    NOW() - INTERVAL '1 hour'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0005');

-- Заявка 6: Новая, низкий приоритет
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, created_at, updated_at)
SELECT
    'TKT-2026-0006',
    'Установить дополнительное ПО',
    'Прошу установить программу Adobe Acrobat Pro для работы с PDF документами.',
    (SELECT category_id FROM categories WHERE name = 'Программное обеспечение'),
    (SELECT priority_id FROM priorities WHERE name = 'Низкий'),
    (SELECT status_id FROM statuses WHERE name = 'Новая'),
    (SELECT user_id FROM users WHERE login = 'user2'),
    NOW() - INTERVAL '4 hours',
    NOW() - INTERVAL '4 hours'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0006');

-- Заявка 7: Решена
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0007',
    'Медленно работает компьютер',
    'Компьютер стал очень медленно работать, программы открываются по несколько минут.',
    (SELECT category_id FROM categories WHERE name = 'Оборудование'),
    (SELECT priority_id FROM priorities WHERE name = 'Средний'),
    (SELECT status_id FROM statuses WHERE name = 'Решена'),
    (SELECT user_id FROM users WHERE login = 'user1'),
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '2 days',
    NOW() - INTERVAL '5 hours'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007');

-- Заявка 8: В работе, критический приоритет
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0008',
    'Не открывается база данных 1С',
    'При попытке открыть базу 1С выдает ошибку "Файл заблокирован другим пользователем". Бухгалтерия не может работать!',
    (SELECT category_id FROM categories WHERE name = 'Программное обеспечение'),
    (SELECT priority_id FROM priorities WHERE name = 'Критический'),
    (SELECT status_id FROM statuses WHERE name = 'В работе'),
    (SELECT user_id FROM users WHERE login = 'user3'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '1 hour',
    NOW() - INTERVAL '20 minutes'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008');

-- Заявка 9: Новая
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, created_at, updated_at)
SELECT
    'TKT-2026-0009',
    'Нужен новый пароль от почты',
    'Забыл пароль от рабочей почты, прошу сбросить.',
    (SELECT category_id FROM categories WHERE name = 'Доступы'),
    (SELECT priority_id FROM priorities WHERE name = 'Средний'),
    (SELECT status_id FROM statuses WHERE name = 'Новая'),
    (SELECT user_id FROM users WHERE login = 'user4'),
    NOW() - INTERVAL '30 minutes',
    NOW() - INTERVAL '30 minutes'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0009');

-- Заявка 10: Закрыта
INSERT INTO tickets (ticket_number, title, description, category_id, priority_id, status_id, created_by_id, assigned_to_id, created_at, updated_at)
SELECT
    'TKT-2026-0010',
    'Настроить VPN подключение',
    'Нужно настроить VPN для удаленной работы из дома.',
    (SELECT category_id FROM categories WHERE name = 'Сеть'),
    (SELECT priority_id FROM priorities WHERE name = 'Низкий'),
    (SELECT status_id FROM statuses WHERE name = 'Закрыта'),
    (SELECT user_id FROM users WHERE login = 'user5'),
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '5 days',
    NOW() - INTERVAL '4 days'
WHERE NOT EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010');

-- Комментарии к заявкам
-- Комментарии к заявке TKT-2026-0001
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0001'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    'Принял заявку в работу. Выезжаю к вам в офис для диагностики сетевого оборудования.',
    NOW() - INTERVAL '1 hour 45 minutes'
WHERE NOT EXISTS (
    SELECT 1 FROM comments c
    JOIN tickets t ON c.ticket_id = t.ticket_id
    WHERE t.ticket_number = 'TKT-2026-0001'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0001'),
    (SELECT user_id FROM users WHERE login = 'user1'),
    'Спасибо! Будем ждать.',
    NOW() - INTERVAL '1 hour 30 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0001'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0001'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    'Проблема в провайдере. Связался с технической поддержкой, они обещали восстановить связь в течение 30 минут.',
    NOW() - INTERVAL '45 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0001'
);

-- Комментарии к заявке TKT-2026-0002
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0002'),
    (SELECT user_id FROM users WHERE login = 'operator2'),
    'Проблема решена, заменил картридж в принтере.',
    NOW() - INTERVAL '6 hours 30 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0002'),
    (SELECT user_id FROM users WHERE login = 'user2'),
    'Отлично, все работает! Большое спасибо!',
    NOW() - INTERVAL '6 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002'
);

-- Комментарии к заявке TKT-2026-0003
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    'Доступ предоставлен. Проверьте, пожалуйста.',
    NOW() - INTERVAL '2 days 12 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    (SELECT user_id FROM users WHERE login = 'user3'),
    'Все работает, спасибо!',
    NOW() - INTERVAL '2 days 6 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003'
);

-- Комментарии к заявке TKT-2026-0005
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0005'),
    (SELECT user_id FROM users WHERE login = 'operator3'),
    'Проверил подключение кабеля - все в порядке. Скорее всего проблема в самом мониторе. Закажу замену.',
    NOW() - INTERVAL '2 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0005'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0005'),
    (SELECT user_id FROM users WHERE login = 'user5'),
    'Хорошо, буду ждать.',
    NOW() - INTERVAL '1 hour 45 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0005'
);

-- Комментарии к заявке TKT-2026-0007
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0007'),
    (SELECT user_id FROM users WHERE login = 'operator2'),
    'Выполнил очистку диска и оптимизацию системы. Добавил оперативной памяти. Проверьте работу.',
    NOW() - INTERVAL '6 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0007'),
    (SELECT user_id FROM users WHERE login = 'user1'),
    'Компьютер летает! Спасибо большое!',
    NOW() - INTERVAL '5 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007'
);

-- Комментарии к заявке TKT-2026-0008
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0008'),
    (SELECT user_id FROM users WHERE login = 'operator1'),
    'Разбираюсь с проблемой. Похоже на блокировку файла.',
    NOW() - INTERVAL '30 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0008'),
    (SELECT user_id FROM users WHERE login = 'user3'),
    'Как можно быстрее, пожалуйста! У нас отчетность!',
    NOW() - INTERVAL '25 minutes'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008'
);

-- Комментарии к заявке TKT-2026-0010
INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    (SELECT user_id FROM users WHERE login = 'operator3'),
    'VPN настроен, отправил инструкцию по подключению на вашу почту.',
    NOW() - INTERVAL '4 days 12 hours'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010'
);

INSERT INTO comments (ticket_id, author_id, content, created_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    (SELECT user_id FROM users WHERE login = 'user5'),
    'Подключилась успешно, все работает. Закрываем заявку.',
    NOW() - INTERVAL '4 days'
WHERE EXISTS (
    SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010'
);

-- История изменений заявок
-- История для TKT-2026-0001
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0001'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '1 hour 50 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0001');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0001'),
    'assigned_to',
    'Не назначена',
    'Иванов Иван',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '1 hour 50 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0001');

-- История для TKT-2026-0002
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0002'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '22 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0002'),
    'assigned_to',
    'Не назначена',
    'Петрова Мария',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '22 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0002'),
    'status',
    'В работе',
    'Решена',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '6 hours 30 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0002');

-- История для TKT-2026-0003
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '2 days 18 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    'assigned_to',
    'Не назначена',
    'Иванов Иван',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '2 days 18 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    'status',
    'В работе',
    'Решена',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '2 days 12 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0003'),
    'status',
    'Решена',
    'Закрыта',
    (SELECT user_id FROM users WHERE login = 'user3'),
    NOW() - INTERVAL '2 days'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0003');

-- История для TKT-2026-0005
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0005'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '4 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0005');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0005'),
    'assigned_to',
    'Не назначена',
    'Козлов Дмитрий',
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '4 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0005');

-- История для TKT-2026-0007
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0007'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '1 day 18 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0007'),
    'assigned_to',
    'Не назначена',
    'Петрова Мария',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '1 day 18 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0007'),
    'status',
    'В работе',
    'Решена',
    (SELECT user_id FROM users WHERE login = 'operator2'),
    NOW() - INTERVAL '6 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0007');

-- История для TKT-2026-0008
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0008'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '50 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0008'),
    'assigned_to',
    'Не назначена',
    'Иванов Иван',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '50 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0008'),
    'priority',
    'Высокий',
    'Критический',
    (SELECT user_id FROM users WHERE login = 'operator1'),
    NOW() - INTERVAL '40 minutes'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0008');

-- История для TKT-2026-0010
INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    'status',
    'Новая',
    'В работе',
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '4 days 20 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    'assigned_to',
    'Не назначена',
    'Козлов Дмитрий',
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '4 days 20 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    'status',
    'В работе',
    'Решена',
    (SELECT user_id FROM users WHERE login = 'operator3'),
    NOW() - INTERVAL '4 days 12 hours'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010');

INSERT INTO ticket_history (ticket_id, field_name, old_value, new_value, changed_by_id, changed_at)
SELECT
    (SELECT ticket_id FROM tickets WHERE ticket_number = 'TKT-2026-0010'),
    'status',
    'Решена',
    'Закрыта',
    (SELECT user_id FROM users WHERE login = 'user5'),
    NOW() - INTERVAL '4 days'
WHERE EXISTS (SELECT 1 FROM tickets WHERE ticket_number = 'TKT-2026-0010');
