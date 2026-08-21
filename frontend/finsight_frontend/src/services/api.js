const API_BASE_URL = "http://localhost:8080";

export async function apiFetch(endpoint, options = {}) {
    const token = localStorage.getItem("token");

    const response = await fetch(
        `${API_BASE_URL}${endpoint}`,
        {
            ...options,
            headers: {
                "Content-Type": "application/json",

                ...(token
                    ? {
                          Authorization: `Bearer ${token}`,
                      }
                    : {}),

                ...(options.headers || {}),
            },
        }
    );

    if (!response.ok) {
        const message = await response.text();

        throw new Error(
            message || "Something went wrong"
        );
    }

    return response.json();
}