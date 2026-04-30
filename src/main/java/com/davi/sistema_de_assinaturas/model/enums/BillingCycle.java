package com.davi.sistema_de_assinaturas.model.enums;

import java.time.LocalDate;

public enum BillingCycle {
    MONTHLY {
        @Override
        public LocalDate addTo(LocalDate date) {
            return date.plusMonths(1);
        }
    },

    YEARLY {
        @Override
        public LocalDate addTo(LocalDate date) {
            return date.plusYears(1);
        }
    };

    public abstract LocalDate addTo(LocalDate date);
}
