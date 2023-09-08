package com.spscommerce.interview.rules;


import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
@ConfigurationProperties
@ConfigurationPropertiesScan
@Configuration
public class PersistenceRules {

    private Map<String, RuleValue> rules;
    private RuleValue defaultRuleValue;

    public PersistenceRules() {
        this.rules = new HashMap<>();
        this.defaultRuleValue = RuleValue.ALLOW;
    }

    public Optional<RuleValue> getRule(String rule) {
        if (rule != null && !rule.trim().isEmpty() && rules.containsKey(rule.toLowerCase())) {
            return Optional.of(rules.get(rule.toLowerCase()));
        }
        return Optional.empty();
    }

}
