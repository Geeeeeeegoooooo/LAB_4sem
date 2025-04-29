const baseUrl = 'http://localhost:8080';

function outputMessage(message) {
    document.getElementById('output').innerText = message;
}

async function createUser() {
    const username = document.getElementById('username').value.trim();
    const length = parseInt(document.getElementById('length').value);
    const complexity = parseInt(document.getElementById('complexity').value);

    if (!username || !length || !complexity) {
        return outputMessage(' Все поля должны быть заполнены.');
    }

    const requestBody = { username, length, complexity };

    try {
        const response = await fetch(`${baseUrl}/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody)
        });
        const data = await response.json();
        outputMessage(' Пользователь добавлен:\n' + JSON.stringify(data, null, 2));
    } catch (error) {
        outputMessage(' Ошибка добавления пользователя.');
    }
}

async function fetchUsers() {
    try {
        const response = await fetch(`${baseUrl}/users`);
        const data = await response.json();
        outputMessage(' Пользователи:\n' + JSON.stringify(data, null, 2));
    } catch (error) {
        outputMessage(' Ошибка получения пользователей.');
    }
}

async function deleteUser() {
    const userId = document.getElementById('deleteUserId').value;
    if (!userId) return outputMessage(' Введите ID пользователя.');

    try {
        await fetch(`${baseUrl}/users/${userId}`, { method: 'DELETE' });
        outputMessage(' Пользователь удалён.');
    } catch (error) {
        outputMessage(' Ошибка удаления пользователя.');
    }
}

async function updatePassword() {
    const userId = document.getElementById('updateUserId').value;
    const passwordId = document.getElementById('updatePasswordId').value;
    const newLength = document.getElementById('newLength').value;
    const newComplexity = document.getElementById('newComplexity').value;

    if (!userId || !passwordId || !newLength || !newComplexity) {
        return outputMessage(' Все поля для обновления пароля должны быть заполнены.');
    }

    try {
        const response = await fetch(`${baseUrl}/users/${userId}/passwords/${passwordId}?size=${newLength}&level=${newComplexity}`, {
            method: 'PUT'
        });
        const data = await response.json();
        outputMessage(' Пароль обновлён:\n' + JSON.stringify(data, null, 2));
    } catch (error) {
        outputMessage(' Ошибка обновления пароля.');
    }
}

async function deletePassword() {
    const userId = document.getElementById('deletePasswordUserId').value;
    const passwordId = document.getElementById('deletePasswordId').value;

    if (!userId || !passwordId) {
        return outputMessage(' Укажите ID пользователя и ID пароля.');
    }

    try {
        const response = await fetch(`${baseUrl}/users/${userId}/passwords/${passwordId}`, { method: 'DELETE' });
        if (response.ok) {
            outputMessage(' Пароль удалён.');
        } else {
            const errorText = await response.text();
            outputMessage(' Ошибка удаления пароля:\n' + errorText);
        }
    } catch (error) {
        outputMessage(' Ошибка удаления пароля: ' + error.message);
    }
}
