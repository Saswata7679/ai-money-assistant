package com.moneyassistant.domain;

/**
 * Fixed set of spending categories. The LLM must pick one of these during
 * extraction — it cannot invent new categories.
 */
public enum Category {
    FOOD_DINING,
    GROCERIES,
    TRAVEL,
    SUBSCRIPTIONS,
    SHOPPING,
    UTILITIES,
    HEALTH,
    OTHER
}
