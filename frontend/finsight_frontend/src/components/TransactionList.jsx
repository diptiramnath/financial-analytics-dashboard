function TransactionList({ transactions }) {

    if (!transactions ||
        transactions.length === 0) {

        return (
            <div className="bg-white rounded-2xl border p-6">

                <h2 className="text-lg font-semibold mb-4">
                    Recent Transactions
                </h2>

                <p className="text-gray-500">
                    No transactions yet.
                </p>

            </div>
        );
    }

    return (
        <div className="bg-white rounded-2xl border p-6">

            <div className="flex justify-between items-center mb-4">

                <h2 className="text-lg font-semibold">
                    Recent Transactions
                </h2>

            </div>

            <div className="space-y-4">

                {transactions.map(
                    (transaction) => {

                        const isExpense =
                            transaction.type ===
                            "EXPENSE";

                        const isIncome =
                            transaction.type ===
                            "INCOME";

                        const sign =
                            isExpense
                                ? "-"
                                : isIncome
                                ? "+"
                                : "";

                        return (
                            <div
                                key={transaction.id}
                                className="flex items-center justify-between border-b pb-3 last:border-b-0"
                            >

                                <div>

                                    <p className="font-medium">
                                        {
                                            transaction.merchant ||
                                            transaction.description ||
                                            "Transaction"
                                        }
                                    </p>

                                    <p className="text-sm text-gray-500">
                                        {new Date(
                                            transaction.date
                                        ).toLocaleDateString(
                                            "en-IN"
                                        )}
                                    </p>

                                </div>

                                <div className="text-right">

                                    <p
                                        className={`font-semibold ${
                                            isExpense
                                                ? "text-red-600"
                                                : isIncome
                                                ? "text-green-600"
                                                : "text-gray-700"
                                        }`}
                                    >
                                        {sign}₹
                                        {transaction.amount.toLocaleString(
                                            "en-IN"
                                        )}
                                    </p>

                                    <p className="text-xs text-gray-400">
                                        {transaction.type}
                                    </p>

                                </div>

                            </div>
                        );
                    }
                )}

            </div>

        </div>
    );
}

export default TransactionList;