package com.temirlan.pulsewatch.repository;

import org.springframework.data.jpa.domain.Specification;

import com.temirlan.pulsewatch.enums.AlertSeverity;
import com.temirlan.pulsewatch.enums.AlertType;
import com.temirlan.pulsewatch.model.AlertEntry;

public class AlertEntrySpecification {

    public static Specification<AlertEntry> withFilters (String service, AlertType type, String status, AlertSeverity severity, Long from, Long to) {
        return (root, query, cb) -> {
            var predicates = cb.conjunction();

            if (service != null && !service.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("service"), service));
            }

            if (type != null) {
                predicates = cb.and(predicates, cb.equal(root.get("type"), type));
            }

            if (status != null && !status.isBlank()) {
                predicates = cb.and(predicates, cb.equal(root.get("status"), status));
            }

            if (severity != null) {
                predicates = cb.and(predicates, cb.equal(root.get("severity"), severity));
            }

            if (from != null) {
                predicates = cb.and(predicates, cb.greaterThanOrEqualTo(root.get("timestamp"), from));
            }

            if (to != null) {
                predicates = cb.and(predicates, cb.lessThanOrEqualTo(root.get("timestamp"), to));
            }

            return predicates;
        };
    }
    
}
