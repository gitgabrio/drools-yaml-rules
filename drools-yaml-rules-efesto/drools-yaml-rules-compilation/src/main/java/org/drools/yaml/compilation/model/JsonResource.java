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
package org.drools.yaml.compilation.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;

import org.drools.yaml.api.domain.RulesSet;
import org.drools.yaml.api.notations.RuleNotation;
import org.kie.efesto.compilationmanager.api.model.EfestoResource;

/**
 * File set for "drl" files
 */
public final class JsonResource implements EfestoResource<String> {

    private static final AtomicInteger counter = new AtomicInteger(1);

    private final String json;

    private final String basePath;

    private final RuleNotation notation;

    public JsonResource(String json, RuleNotation notation) {
        this.json = json;
        this.basePath = "/drl/json/" + counter.accumulateAndGet(1, Integer::sum);
        this.notation = notation;
    }

    public JsonResource(String json) {
        this(json, RuleNotation.CoreNotation.INSTANCE);
    }

    @Override
    public String getContent() {
        return json;
    }

    public String getBasePath() {
        return basePath;
    }

    public RuleNotation getNotation() {
        return notation;
    }
}
