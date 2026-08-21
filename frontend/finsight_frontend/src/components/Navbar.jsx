import { useNavigate } from "react-router-dom";

function Navbar() {

    const navigate = useNavigate();

    const userName =
        localStorage.getItem("userName") ||
        "User";

    const handleLogout = () => {

        localStorage.removeItem("token");
        localStorage.removeItem("userName");
        localStorage.removeItem("userEmail");

        navigate("/");
    };

    return (
        <nav className="bg-white border-b px-6 py-4 flex items-center justify-between">

            <div>

                <h1 className="text-xl font-bold">
                    FinSight
                </h1>

            </div>

            <div className="flex items-center gap-5">

                <span className="text-gray-600">
                    Hello, {userName}
                </span>

                <button
                    onClick={handleLogout}
                    className="text-red-600 hover:text-red-700"
                >
                    Logout
                </button>

            </div>

        </nav>
    );
}

export default Navbar;