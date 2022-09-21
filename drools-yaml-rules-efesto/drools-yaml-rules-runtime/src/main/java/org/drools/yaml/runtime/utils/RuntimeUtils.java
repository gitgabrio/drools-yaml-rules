/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.yaml.runtime.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.drools.yaml.api.domain.RuleMatch;
import org.drools.yaml.api.domain.durable.DurableRuleMatch;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.DrlRulesetIdFactory;
import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputBoolean;
import org.drools.yaml.runtime.model.EfestoOutputFactMaps;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.drools.yaml.runtime.model.EfestoOutputMatches;
import org.drools.yaml.runtime.model.LocalComponentIdDrlRuleset;
import org.kie.api.runtime.rule.Match;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.common.api.identifiers.ReflectiveAppRoot;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;
import org.kie.efesto.runtimemanager.api.model.EfestoOutput;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.kie.memorycompiler.KieMemoryCompiler;

public class RuntimeUtils {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    private RuntimeUtils() {
    }

    public static int executeFacts(long id, Map<String, Object> factMap) {
        EfestoOutputInteger output = (EfestoOutputInteger) common(id, factMap, "execute-facts");
        return output.getOutputData();
    }

    public static List<RuleMatch> processFacts(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process-facts");
        List<RuleMatch> toReturn = new ArrayList<>();
        for (Match match : output.getOutputData()) {
            toReturn.add(RuleMatch.from(match));
        }
        return toReturn;
    }

    public static List<RuleMatch> processEvents(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process-events");
        List<RuleMatch> toReturn = new ArrayList<>();
        for (Match match : output.getOutputData()) {
            toReturn.add(RuleMatch.from(match));
        }
        return toReturn;
    }

    public static boolean retract(long id, Map<String, Object> factMap) {
        EfestoOutputBoolean output = (EfestoOutputBoolean) common(id, factMap, "retract");
        return output.getOutputData();
    }

    public static List<Map<String, Object>> getAllFacts(long id) {
        ModelLocalUriId modelLocalUriId = makeModelLocalUriId(id);
        EfestoInputId efestoInputId = new EfestoInputId(modelLocalUriId, id);
        EfestoOutputFactMaps output = (EfestoOutputFactMaps) common(efestoInputId);
        return output.getOutputData();
    }

    public static List<Map<String, Map>> processFactsDurableRules(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process-facts");
        List<Map<String, Map>> toReturn = new ArrayList<>();
        for (Match match : output.getOutputData()) {
            toReturn.add(DurableRuleMatch.from(match));
        }
        return toReturn;
    }

    public static List<Map<String, Map>> processEventsDurableRules(long id, Map<String, Object> factMap) {
        EfestoOutputMatches output = (EfestoOutputMatches) common(id, factMap, "process-events");
        List<Map<String, Map>> toReturn = new ArrayList<>();
        for (Match match : output.getOutputData()) {
            toReturn.add(DurableRuleMatch.from(match));
        }
        return toReturn;
    }

    private static EfestoOutput common(long id, Map<String, Object> factMap, String operation) {
        ModelLocalUriId modelLocalUriId = makeModelLocalUriId(id, operation);
        EfestoInputMap efestoInputMap = new EfestoInputMap(modelLocalUriId, id, factMap, operation);
        return common(efestoInputMap);
    }

    private static EfestoOutput common(EfestoInput efestoInput) {
        RulesRuntimeContext rulesRuntimeContext =
                new RulesRuntimeContext(
                        new KieMemoryCompiler.MemoryCompilerClassLoader(
                                Thread.currentThread().getContextClassLoader()));

        Collection<EfestoOutput> outputs = runtimeManager.evaluateInput(rulesRuntimeContext, efestoInput);
        return outputs.iterator().next();
    }

    public static ModelLocalUriId makeModelLocalUriId(Object... suffix) {
        StringBuilder builder = new StringBuilder();
        int counter = 0;
        int limit = suffix.length - 1;
        for (Object object : suffix) {
            builder.append(object.toString());
            if (counter < limit) {
                builder.append("/");
            }
            counter++;
        }
        String suffixString = builder.toString();
        LocalComponentIdDrlRuleset modelLocalUriId = new ReflectiveAppRoot("")
                .get(DrlRulesetIdFactory.class)
                .get(suffixString);

        return modelLocalUriId;
    }

}
