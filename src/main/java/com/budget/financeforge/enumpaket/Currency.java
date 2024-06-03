package com.budget.financeforge.enumpaket;

public enum Currency {



    PLN("PLN"),
    EUR("EUR");


    private final String displayValue;

    private Currency(String displayValue) {
        this.displayValue = displayValue;
    }

    @Override
    public String toString() {
        return displayValue;
    }






}
