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
package org.drools.yaml.compilation.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.drools.yaml.api.context.HasRulesExecutorContainer;
import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.notations.RuleNotation;
import org.drools.yaml.compilation.model.JsonResource;
import org.drools.yaml.compilation.model.RuleSetResource;
import org.kie.efesto.compilationmanager.api.exceptions.KieCompilerServiceException;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationContext;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;

public class KieCompilerServiceJson implements KieCompilerService {



    @Override
    public boolean canManageResource(EfestoResource toProcess) {
        return toProcess instanceof JsonResource;
    }

    @Override
    public List<EfestoCompilationOutput> processResource(EfestoResource toProcess, EfestoCompilationContext context) {
        if (!canManageResource(toProcess)) {
            throw new KieCompilerServiceException(String.format("%s can not process %s",
                                                                this.getClass().getName(),
                                                                toProcess.getClass().getName()));
        }


        JsonResource jsonResource = (JsonResource) toProcess;
        // TODO: issue: this works only for the on-the-fly compilation or with an hack, anyway in the same JVM;
        // in this case it works only because
        // 1: both CompilationContext and RuntimeContext share access to the same singleton RulesExecutorContainer
        // 2: at compile time, the instance of RulesExecutor is "stored" in the above singleton by ((HasRulesExecutorContainer)EfestoCompilationContext), and at runtime it is
        // retrieved from it by ((HasRulesExecutorContainer)EfestoRuntimeContext)
        // Of course, this is a very specific situation and a very specific hack: it could not possibly work with isolated compilation and runtime execution
        // We could consider the idea to write in the IndexFile" the information/parameters required to instantiate a given class at runtime:
        // e.g.: the compilation create the following "executable" resource
        // [
        //  {
        //    "step-type": "executable",
        //    "fri": {
        //      "basePath": "/(rule_name)",
        //      "model": "drl",
        //      "fri": "/drl/(rule_name)"
        //    },
        //    "fullClassNames": [
        //      "org.drools.yaml.api.context.RulesExecutor"
        //    ],
        //    "parameters": [
        //      "(RuleNotation)",
        //      "(json rule representation)"
        //    ]
        //  }
        //]
        // At runtime, the runtime service
        // 1) retrieve the name of the class whose method is to be invoked (org.drools.yaml.api.context.RulesExecutor)
        // 2) retrieve the parameters ((RuleNotation) and (json rule representation))
        // 3) invoke the method RulesExecutor.createFromJson(jsonResource.getNotation(), jsonResource.getContent());
        // This works because the runtimeservice "knows" which method to invoke - In that specific case, it is also useless to store the
        // "org.drools.yaml.api.context.RulesExecutor" name, since we already know it this to be used; but it may be useful in case of polymorphism.
        // THis solution
        // 1) would be more in line with the general design
        // 2) it would be "almost" like what is already in place (e.g. PMML method loadFactory), with the only difference of the parameters
        // 2) would always work (regardless of when compilation and runtime are executed)
        // 3) would avoid the needs of instantiate an object at compile time to "bring it around" for runtime
        // 4) would avoid the need of the "singleton" shared by both compilation and runtime
        //

        RulesExecutor rulesExecutor = RulesExecutor.createFromJson(jsonResource.getNotation(), jsonResource.getContent());

        ((HasRulesExecutorContainer) context).register(rulesExecutor);
        return Collections.emptyList();
    }

    private class RulesExecutorKey implements Serializable {

        private final RuleNotation ruleNotation;
        private final int hashCode;

        public RulesExecutorKey(RuleNotation ruleNotation, int hashCode) {
            this.ruleNotation = ruleNotation;
            this.hashCode = hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RulesExecutorKey that = (RulesExecutorKey) o;
            return hashCode == that.hashCode && Objects.equals(ruleNotation, that.ruleNotation);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ruleNotation, hashCode);
        }
    }
}
