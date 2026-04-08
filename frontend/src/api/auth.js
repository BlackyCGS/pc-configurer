const API_URL = "http://localhost:8080/api/auth";

export async function signup(user) {
    const response = await fetch(`${API_URL}/signup`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user),
        credentials: "include"
    });

    return response.json();
}

export async function login(credentials) {
    const response = await fetch(`${API_URL}/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(credentials),
        credentials: "include"
    });

    if (!response.ok) {
        throw new Error("Ошибка авторизации");
    }

    return response.text();
}
