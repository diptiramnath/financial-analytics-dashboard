import { useEffect, useState } from "react";

import Navbar from "../components/Navbar";
import StatCard from "../components/StatCard";
import TransactionList from "../components/TransactionList";

import { apiFetch } from "../services/api";

function Dashboard() {

    const [dashboard, setDashboard] =
        useState(null);

    const [loading, setLoading] =
        useState(true);

    const [error, setError] =
        useState("");

    useEffect(() => {

        const loadDashboard = async () => {

            try {

                setLoading(true);
                setError("");

                const data =
                    await apiFetch(
                        "/api/dashboard"
                    );

                setDashboard(data);

            } catch (error) {

                console.error(
                    "Dashboard error:",
                    error
                );

                setError(
                    error.message ||
                    "Failed to load dashboard"
                );

            } finally {

                setLoading(false);
            }
        };

        loadDashboard();

    }, []);

    if (loading) {

        return (
            <div className="min-h-screen bg-gray-100">

                <Navbar />

                <div className="p-6">
                    Loading dashboard...
                </div>

            </div>
        );
    }

    if (error) {

        return (
            <div className="min-h-screen bg-gray-100">

                <Navbar />

                <div className="p-6">

                    <div className="bg-red-100 text-red-700 p-4 rounded-lg">
                        {error}
                    </div>

                </div>

            </div>
        );
    }

    return (
        <div className="min-h-screen bg-gray-100">

            <Navbar />

            <main className="p-6 max-w-7xl mx-auto">

                <div className="mb-6">

                    <h1 className="text-3xl font-bold">
                        Dashboard
                    </h1>

                    <p className="text-gray-500 mt-1">
                        Here's an overview of your finances.
                    </p>

                </div>

                {/* Statistics */}

                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">

                    <StatCard
                        title="Total Balance"
                        value={`₹${dashboard.totalBalance.toLocaleString(
                            "en-IN"
                        )}`}
                    />

                    <StatCard
                        title="Income"
                        value={`₹${dashboard.totalIncome.toLocaleString(
                            "en-IN"
                        )}`}
                    />

                    <StatCard
                        title="Expenses"
                        value={`₹${dashboard.totalExpenses.toLocaleString(
                            "en-IN"
                        )}`}
                    />

                    <StatCard
                        title="Net Savings"
                        value={`₹${dashboard.netSavings.toLocaleString(
                            "en-IN"
                        )}`}
                    />

                </div>

                {/* Main content */}

                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mt-6">

                    {/* Spending by category */}

                    <div className="bg-white rounded-2xl border p-6">

                        <h2 className="text-lg font-semibold mb-4">
                            Spending by Category
                        </h2>

                        {dashboard.expensesByCategory &&
                        dashboard.expensesByCategory.length > 0 ? (

                            <div className="space-y-4">

                                {dashboard.expensesByCategory.map(
                                    (category) => (

                                        <div
                                            key={
                                                category.categoryId
                                            }
                                        >

                                            <div className="flex justify-between mb-1">

                                                <span className="text-sm">
                                                    {
                                                        category.categoryName
                                                    }
                                                </span>

                                                <span className="text-sm font-medium">
                                                    ₹
                                                    {category.amount.toLocaleString(
                                                        "en-IN"
                                                    )}
                                                </span>

                                            </div>

                                        </div>

                                    )
                                )}

                            </div>

                        ) : (

                            <p className="text-gray-500">
                                No expense data available.
                            </p>

                        )}

                    </div>

                    {/* Recent transactions */}

                    <TransactionList
                        transactions={
                            dashboard.recentTransactions
                        }
                    />

                </div>

            </main>

        </div>
    );
}

export default Dashboard;