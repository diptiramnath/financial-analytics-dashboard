import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080";

function Register() {

    const navigate = useNavigate();

    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleRegister = async (e) => {

        e.preventDefault();

        setError("");
        setLoading(true);

        try {

            const response = await fetch(
                `${API_BASE_URL}/api/auth/register`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type": "application/json",
                    },

                    body: JSON.stringify({
                        name,
                        email,
                        password,
                    }),
                }
            );

            if (!response.ok) {

                const message =
                    await response.text();

                throw new Error(
                    message || "Registration failed"
                );
            }

            // Registration succeeded
            alert(
                "Account created successfully. Please login."
            );

            navigate("/");

        } catch (error) {

            console.error(error);

            setError(
                error.message ||
                "Registration failed"
            );

        } finally {

            setLoading(false);
        }
    };

    return (
        <div className="min-h-screen bg-gray-100 flex items-center justify-center">

            <div className="bg-white p-8 rounded-2xl shadow-md w-full max-w-md">

                <h1 className="text-3xl font-bold text-center mb-2">
                    Create your account
                </h1>

                <p className="text-gray-500 text-center mb-6">
                    Start managing your finances
                </p>

                {error && (
                    <div className="bg-red-100 text-red-700 p-3 rounded-lg mb-4">
                        {error}
                    </div>
                )}

                <form
                    onSubmit={handleRegister}
                    className="space-y-4"
                >

                    <div>

                        <label className="block text-sm font-medium mb-1">
                            Name
                        </label>

                        <input
                            type="text"
                            value={name}
                            onChange={(e) =>
                                setName(e.target.value)
                            }
                            placeholder="Enter your name"
                            required
                            className="w-full border rounded-lg px-4 py-2"
                        />

                    </div>

                    <div>

                        <label className="block text-sm font-medium mb-1">
                            Email
                        </label>

                        <input
                            type="email"
                            value={email}
                            onChange={(e) =>
                                setEmail(e.target.value)
                            }
                            placeholder="Enter your email"
                            required
                            className="w-full border rounded-lg px-4 py-2"
                        />

                    </div>

                    <div>

                        <label className="block text-sm font-medium mb-1">
                            Password
                        </label>

                        <input
                            type="password"
                            value={password}
                            onChange={(e) =>
                                setPassword(e.target.value)
                            }
                            placeholder="Create a password"
                            required
                            className="w-full border rounded-lg px-4 py-2"
                        />

                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50"
                    >
                        {loading
                            ? "Creating account..."
                            : "Register"}
                    </button>

                </form>

                <p className="text-center text-sm text-gray-500 mt-6">

                    Already have an account?{" "}

                    <button
                        onClick={() =>
                            navigate("/")
                        }
                        className="text-blue-600 hover:underline"
                    >
                        Login
                    </button>

                </p>

            </div>

        </div>
    );
}

export default Register;