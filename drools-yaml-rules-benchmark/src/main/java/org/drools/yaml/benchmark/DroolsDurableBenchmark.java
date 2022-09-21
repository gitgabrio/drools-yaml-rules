package org.drools.yaml.benchmark;

import java.util.concurrent.TimeUnit;

import org.drools.yaml.api.context.RulesExecutor;
import org.drools.yaml.api.notations.DurableNotation;
import org.drools.yaml.compilation.RulesCompilationContext;
import org.drools.yaml.compilation.model.JsonResource;
import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputJson;
import org.drools.yaml.runtime.model.EfestoOutputMatches;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.service.CompilationManager;
import org.kie.efesto.compilationmanager.api.utils.SPIUtils;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import static org.drools.yaml.runtime.utils.RuntimeUtils.makeModelLocalUriId;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
public class DroolsDurableBenchmark {

    @Param({"1000", "10000", "100000"})
    private int eventsNr;

    @Param({"efesto", "notEfesto"})
    private String invocation;

    private RulesExecutor rulesExecutor;

    private RuntimeManager runtimeManager;

    private RulesRuntimeContext rulesRuntimeContext;

    private long executorId;

    private ModelLocalUriId modelLocalUriId;

    @Setup
    public void setup() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"event.i\": 1}}}]}}}";
        switch (invocation) {
            case "efesto":
                setupEfesto(jsonRule);
                break;
            default:
                setupNotEfesto(jsonRule);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public int benchmark() {
        switch (invocation) {
            case "efesto":
                return benchmarkEfesto();
            default:
                return benchmarkNotEfesto();
        }
    }

    private void setupEfesto(String jsonRule) {
        CompilationManager compilationManager =
                SPIUtils.getCompilationManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve " +
                                                                                                     "CompilationManager"));
        RulesCompilationContext ctx = RulesCompilationContext.create();
        JsonResource resource = new JsonResource(jsonRule, DurableNotation.INSTANCE);
        compilationManager.processResource(ctx, resource);
        executorId = ctx.ruleExecutorId();
        runtimeManager =
                org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(false).orElseThrow(() -> new RuntimeException("Failed to retrieve RuntimeManager"));
        rulesRuntimeContext = RulesRuntimeContext.create();
        modelLocalUriId = makeModelLocalUriId(executorId, "processEvents");
    }

    private void setupNotEfesto(String jsonRule) {
        rulesExecutor = RulesExecutor.createFromJson(DurableNotation.INSTANCE, jsonRule);
    }

    private int benchmarkEfesto() {
        int count = 0;
        for (int i = 0; i < eventsNr; i++) {
            // Doing inside the loop to recreate condition as of `benchmarkNotEfesto`
            String json = "{ \"event\": { \"i\": \"Done\" } }";
            EfestoInputJson efestoInputJson = new EfestoInputJson(modelLocalUriId, executorId, json);
            EfestoOutputMatches matches = (EfestoOutputMatches) runtimeManager
                    .evaluateInput(rulesRuntimeContext, efestoInputJson)
                    .iterator().next();
            count += matches.getOutputData().size();
        }
        if (count != eventsNr) {
            throw new IllegalStateException("Matched " + count + " rules, expected " + eventsNr);
        }
        return count;
    }

    private int benchmarkNotEfesto() {
        int count = 0;
        for (int i = 0; i < eventsNr; i++) {
            count += rulesExecutor.processEvents("{ \"event\": { \"i\": \"Done\" } }").size();
        }
        if (count != eventsNr) {
            throw new IllegalStateException("Matched " + count + " rules, expected " + eventsNr);
        }
        return count;
    }
}
