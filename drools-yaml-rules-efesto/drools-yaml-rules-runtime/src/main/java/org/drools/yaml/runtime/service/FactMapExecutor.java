package org.drools.yaml.runtime.service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.evaluate;

public class FactMapExecutor<T> implements KieRuntimeService<Map<String, Object>, T, EfestoInputMap,
        EfestoOutput<T>, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputMap.class, Collections.singletonList(Map.class));
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (context instanceof HasRulesExecutorContainer) &&
                ((HasRulesExecutorContainer) context).hasRulesExecutor(((EfestoInputMap) toEvaluate).getId());
    }

    @Override
    public Optional<EfestoOutput<T>> evaluateInput(EfestoInputMap toEvaluate, EfestoRuntimeContext context) {
        HasRulesExecutorContainer hasRuleExecutor = (HasRulesExecutorContainer) context;
        return Optional.ofNullable(evaluate(toEvaluate, hasRuleExecutor.getRulesExecutor((toEvaluate).getId())));
    }

    @Override
    public String getModelType() {
        return "drl";
    }

}
