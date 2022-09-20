package org.drools.yaml.runtime.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoOutputFactMaps;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoRuntimeContext;
import org.kie.efesto.runtimemanager.api.service.KieRuntimeService;

import static org.drools.yaml.runtime.utils.DroolsYamlUtils.getAllFacts;

public class IdExecutor implements KieRuntimeService<Long, List<Map<String, Object>>, EfestoInputId,
        EfestoOutputFactMaps, EfestoRuntimeContext> {

    @Override
    public EfestoClassKey getEfestoClassKeyIdentifier() {
        return new EfestoClassKey(EfestoInputId.class, Collections.singletonList(Long.class));
    }

    @Override
    public boolean canManageInput(EfestoInput toEvaluate, EfestoRuntimeContext context) {
        return (context instanceof HasRulesExecutorContainer) &&
                ((HasRulesExecutorContainer) context).hasRulesExecutor(((EfestoInputId) toEvaluate).getId());
    }

    @Override
    public Optional<EfestoOutputFactMaps> evaluateInput(EfestoInputId toEvaluate, EfestoRuntimeContext context) {
        HasRulesExecutorContainer hasRuleExecutor = (HasRulesExecutorContainer) context;
        return Optional.ofNullable(getAllFacts(toEvaluate, hasRuleExecutor.getRulesExecutor((toEvaluate).getId())));
    }

    @Override
    public String getModelType() {
        return "drl";
    }

}
