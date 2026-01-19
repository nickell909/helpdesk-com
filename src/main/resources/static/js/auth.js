// Authentication and token management

function getToken() {
    return localStorage.getItem('token');
}

function setToken(token) {
    localStorage.setItem('token', token);
}

function removeToken() {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
}

function getUserInfo() {
    const userInfo = localStorage.getItem('userInfo');
    return userInfo ? JSON.parse(userInfo) : null;
}

function setUserInfo(userInfo) {
    localStorage.setItem('userInfo', JSON.stringify(userInfo));
}

function isAuthenticated() {
    return !!getToken();
}

function checkAuth() {
    if (!isAuthenticated()) {
        window.location.href = '/login';
        return false;
    }
    return true;
}

function logout() {
    removeToken();
    window.location.href = '/login';
}

function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        return null;
    }
}

function updateNavbar() {
    const userInfo = getUserInfo();
    if (userInfo) {
        document.getElementById('username').textContent = userInfo.fullName || userInfo.login;
        document.getElementById('userRole').textContent = 'Роль: ' + getRoleDisplayName(userInfo.role);

        // Show admin menu only for ADMIN role
        if (userInfo.role === 'ADMIN') {
            const adminNav = document.getElementById('nav-admin');
            if (adminNav) adminNav.style.display = 'block';
        }
    }
}

function getRoleDisplayName(role) {
    const roleNames = {
        'USER': 'Пользователь',
        'OPERATOR': 'Оператор',
        'ADMIN': 'Администратор'
    };
    return roleNames[role] || role;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    // Skip auth check on login page
    if (window.location.pathname === '/login' || window.location.pathname === '/') {
        return;
    }

    if (checkAuth()) {
        updateNavbar();
    }
});
