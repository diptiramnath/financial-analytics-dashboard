package com.finsight.backend.service;

import com.finsight.backend.dto.CategoryExpenseResponse;
import com.finsight.backend.dto.InsightResponse;
import com.finsight.backend.dto.MonthlyAnalyticsResponse;
import com.finsight.backend.dto.BudgetResponse;
import com.finsight.backend.enums.InsightType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InsightService {

    private final AnalyticsService analyticsService;
    private final BudgetService budgetService;

    public InsightService(
            AnalyticsService analyticsService,
            BudgetService budgetService) {

        this.analyticsService = analyticsService;
        this.budgetService = budgetService;
    }

    public List<InsightResponse> getInsights(
            int month,
            int year) {

        List<InsightResponse> insights =
                new ArrayList<>();

        MonthlyAnalyticsResponse analytics =
                analyticsService.getMonthlyAnalytics(
                        month,
                        year
                );

        generateSpendingInsight(
                analytics,
                insights
        );

        generateSavingsInsight(
                analytics,
                insights
        );

        generateBudgetInsights(
                month,
                year,
                insights
        );

        generateTrendInsight(
                month,
                year,
                insights
        );

        return insights;
    }

    private void generateSpendingInsight(
            MonthlyAnalyticsResponse analytics,
            List<InsightResponse> insights) {

        if (analytics.getExpensesByCategory() == null
                || analytics.getExpensesByCategory().isEmpty()) {

            return;
        }

        CategoryExpenseResponse highest =
                analytics.getExpensesByCategory()
                        .stream()
                        .max(
                                (a, b) ->
                                        a.getAmount()
                                                .compareTo(
                                                        b.getAmount()
                                                )
                        )
                        .orElse(null);

        if (highest == null) {
            return;
        }

        insights.add(
                InsightResponse.builder()
                        .type(InsightType.SPENDING)
                        .title(
                                "Highest spending category"
                        )
                        .message(
                                "You spent ₹"
                                        + highest.getAmount()
                                        + " on "
                                        + highest.getCategoryName()
                                        + " this month."
                        )
                        .severity("INFO")
                        .build()
        );
    }

    private void generateSavingsInsight(
            MonthlyAnalyticsResponse analytics,
            List<InsightResponse> insights) {

        if (analytics.getNetSavings()
                .compareTo(
                        java.math.BigDecimal.ZERO
                ) > 0) {

            insights.add(
                    InsightResponse.builder()
                            .type(InsightType.SAVINGS)
                            .title(
                                    "Positive savings"
                            )
                            .message(
                                    "You saved ₹"
                                            + analytics
                                            .getNetSavings()
                                            + " this month."
                            )
                            .severity("SUCCESS")
                            .build()
            );

        } else if (
                analytics.getNetSavings()
                        .compareTo(
                                java.math.BigDecimal.ZERO
                        ) < 0) {

            insights.add(
                    InsightResponse.builder()
                            .type(InsightType.SAVINGS)
                            .title(
                                    "Spending exceeds income"
                            )
                            .message(
                                    "Your expenses are higher "
                                            + "than your income this month."
                            )
                            .severity("WARNING")
                            .build()
            );
        }
    }

    private void generateBudgetInsights(
            int month,
            int year,
            List<InsightResponse> insights) {

        List<BudgetResponse> budgets =
                budgetService.getBudgets(
                        month,
                        year
                );

        for (BudgetResponse budget : budgets) {

            if (budget.getPercentageUsed() >= 100) {

                insights.add(
                        InsightResponse.builder()
                                .type(InsightType.BUDGET)
                                .title(
                                        "Budget exceeded"
                                )
                                .message(
                                        "You've exceeded your "
                                                + budget.getCategoryName()
                                                + " budget."
                                )
                                .severity("DANGER")
                                .build()
                );

            } else if (
                    budget.getPercentageUsed() >= 80) {

                insights.add(
                        InsightResponse.builder()
                                .type(InsightType.BUDGET)
                                .title(
                                        "Budget almost reached"
                                )
                                .message(
                                        "You've used "
                                                + String.format(
                                                "%.1f",
                                                budget
                                                        .getPercentageUsed()
                                        )
                                                + "% of your "
                                                + budget.getCategoryName()
                                                + " budget."
                                )
                                .severity("WARNING")
                                .build()
                );
            }
        }
    }

    private void generateTrendInsight(
            int month,
            int year,
            List<InsightResponse> insights) {

        var comparison =
                analyticsService.getMonthlyComparison(
                        month,
                        year
                );

        if (comparison.getExpensesChangePercentage() > 10) {

            insights.add(
                    InsightResponse.builder()
                            .type(InsightType.TREND)
                            .title(
                                    "Expenses increased"
                            )
                            .message(
                                    String.format(
                                            "Your expenses increased by %.1f%% compared with last month.",
                                            comparison
                                                    .getExpensesChangePercentage()
                                    )
                            )
                            .severity("WARNING")
                            .build()
            );
        }

        if (comparison.getExpensesChangePercentage() < -10) {

            insights.add(
                    InsightResponse.builder()
                            .type(InsightType.TREND)
                            .title(
                                    "Expenses decreased"
                            )
                            .message(
                                    String.format(
                                            "Your expenses decreased by %.1f%% compared with last month.",
                                            Math.abs(
                                                    comparison
                                                            .getExpensesChangePercentage()
                                            )
                                    )
                            )
                            .severity("SUCCESS")
                            .build()
            );
        }
    }
}