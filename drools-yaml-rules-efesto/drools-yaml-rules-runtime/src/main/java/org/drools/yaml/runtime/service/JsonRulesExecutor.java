package org.drools.yaml.runtime.service;

import java.util.Collections;
import java.util.Optional;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.executeFacts;

public class JsonRulesExecutor implements KieRuntimeService<String, Integer, EfestoInputJson, EfestoOutputInteger, EfestoRuntimeContext> {


    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputJson.class, Collections.singletonList(String.class));
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (context instanceof HasRulesExecutorContainer) &&
                ((HasRulesExecutorContainer) context).hasRulesExecutor(((EfestoInputJson) toEvaluate).getId());
    }

    @Override
    public Optional<EfestoOutputInteger> evaluateInput(EfestoInputJson toEvaluate, EfestoRuntimeContext context) {
        HasRulesExecutorContainer hasRuleExecutor = (HasRulesExecutorContainer) context;
        return Optional.ofNullable(executeFacts(toEvaluate, hasRuleExecutor.getRulesExecutor((toEvaluate).getId())));
    }

    @Override
    public String getModelType() {
        return "drl";
    }
}
