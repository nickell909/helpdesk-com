// API communication helpers

const API_BASE_URL = '/api';

function getAuthHeaders() {
    const token = getToken();
    return {
        'Content-Type': 'application/json',
        'Authorization': token ? `Bearer ${token}` : ''
    };
}

async function apiRequest(url, options = {}) {
    const defaultOptions = {
        headers: getAuthHeaders(),
        ...options
    };

    try {
        const response = await fetch(API_BASE_URL + url, defaultOptions);

        if (response.status === 401) {
            // Token expired or invalid
            removeToken();
            window.location.href = '/login';
            throw new Error('Unauthorized');
        }

        if (!response.ok) {
            const error = await response.json();
            throw new Error(error.error || 'Request failed');
        }

        // Handle 204 No Content
        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Auth API
async function login(credentials) {
    const response = await fetch(API_BASE_URL + '/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(credentials)
    });

    if (!response.ok) {
        const error = await response.json();
        throw new Error(error.error || 'Login failed');
    }

    return await response.json();
}

// Ticket API
async function getTickets() {
    return await apiRequest('/tickets');
}

async function getTicketById(id) {
    return await apiRequest(`/tickets/${id}`);
}

async function createTicket(ticketData) {
    return await apiRequest('/tickets', {
        method: 'POST',
        body: JSON.stringify(ticketData)
    });
}

async function updateTicketStatus(ticketId, statusId) {
    return await apiRequest(`/tickets/${ticketId}/status`, {
        method: 'PUT',
        body: JSON.stringify({ statusId })
    });
}

async function assignTicket(ticketId, operatorId) {
    return await apiRequest(`/tickets/${ticketId}/assign`, {
        method: 'PUT',
        body: JSON.stringify({ operatorId })
    });
}

async function getMyTickets() {
    return await apiRequest('/tickets/my');
}

async function getTicketHistory(ticketId) {
    return await apiRequest(`/tickets/${ticketId}/history`);
}

// Comment API
async function getComments(ticketId) {
    return await apiRequest(`/tickets/${ticketId}/comments`);
}

async function addComment(ticketId, content) {
    return await apiRequest(`/tickets/${ticketId}/comments`, {
        method: 'POST',
        body: JSON.stringify({ content })
    });
}

// Reference API
async function getCategories() {
    return await apiRequest('/references/categories');
}

async function getPriorities() {
    return await apiRequest('/references/priorities');
}

async function getStatuses() {
    return await apiRequest('/references/statuses');
}

async function getRoles() {
    return await apiRequest('/references/roles');
}

// User API (Admin only)
async function getUsers() {
    return await apiRequest('/admin/users');
}

async function getUserById(id) {
    return await apiRequest(`/admin/users/${id}`);
}

async function createUser(userData) {
    return await apiRequest('/admin/users', {
        method: 'POST',
        body: JSON.stringify(userData)
    });
}

async function updateUser(id, userData) {
    return await apiRequest(`/admin/users/${id}`, {
        method: 'PUT',
        body: JSON.stringify(userData)
    });
}

async function deleteUser(id) {
    return await apiRequest(`/admin/users/${id}`, {
        method: 'DELETE'
    });
}

async function getOperators() {
    return await apiRequest('/admin/users/operators');
}

// Utility functions
function showAlert(message, type = 'success') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    const container = document.querySelector('.container');
    if (container) {
        container.insertBefore(alertDiv, container.firstChild);
        setTimeout(() => alertDiv.remove(), 5000);
    }
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleString('ru-RU', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

function getPriorityClass(priorityName) {
    const priorities = {
        'Низкий': 'low',
        'Средний': 'medium',
        'Высокий': 'high',
        'Критический': 'critical'
    };
    return priorities[priorityName] || 'medium';
}

function getStatusClass(statusName) {
    const statuses = {
        'Новая': 'new',
        'В работе': 'in-progress',
        'Решена': 'resolved',
        'Закрыта': 'closed'
    };
    return statuses[statusName] || 'new';
}
