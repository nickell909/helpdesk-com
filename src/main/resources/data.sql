-- Инициализация справочников и тестовых данных

-- Роли
INSERT INTO roles (name) VALUES ('USER') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('OPERATOR') ON CONFLICT DO NOTHING;
INSERT INTO roles (name) VALUES ('ADMIN') ON CONFLICT DO NOTHING;

-- Пользователи (пароль для всех: password)
-- Bcrypt hash для "password": $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Администратор', 'admin@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'ADMIN'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'operator1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Иванов Иван', 'operator1@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'OPERATOR'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'operator2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Петрова Мария', 'operator2@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'OPERATOR'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'user1', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Сидоров Петр', 'user1@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'USER'
ON CONFLICT (login) DO NOTHING;

INSERT INTO users (login, password_hash, full_name, email, role_id)
SELECT 'user2', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Смирнова Анна', 'user2@helpdesk.local', r.role_id
FROM roles r WHERE r.name = 'USER'
ON CONFLICT (login) DO NOTHING;

-- Категории
INSERT INTO categories (name) VALUES ('Программное обеспечение') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('Оборудование') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('Сеть') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('Доступы') ON CONFLICT DO NOTHING;
INSERT INTO categories (name) VALUES ('Прочее') ON CONFLICT DO NOTHING;

-- Приоритеты
INSERT INTO priorities (name) VALUES ('Низкий') ON CONFLICT DO NOTHING;
INSERT INTO priorities (name) VALUES ('Средний') ON CONFLICT DO NOTHING;
INSERT INTO priorities (name) VALUES ('Высокий') ON CONFLICT DO NOTHING;
INSERT INTO priorities (name) VALUES ('Критический') ON CONFLICT DO NOTHING;

-- Статусы
INSERT INTO statuses (name) VALUES ('Новая') ON CONFLICT DO NOTHING;
INSERT INTO statuses (name) VALUES ('В работе') ON CONFLICT DO NOTHING;
INSERT INTO statuses (name) VALUES ('Решена') ON CONFLICT DO NOTHING;
INSERT INTO statuses (name) VALUES ('Закрыта') ON CONFLICT DO NOTHING;
