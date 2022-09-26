package org.drools.yaml.benchmark;

import java.util.concurrent.TimeUnit;

import org.drools.yaml.api.KieSessionHolder;
import org.drools.yaml.compilation.RulesCompiler;
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

import static org.drools.yaml.runtime.KieSessionHolderUtils.kieSessionHolder;

@State(Scope.Benchmark)
@Warmup(iterations = 5)
@Measurement(iterations = 10)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Fork(2)
public class DroolsDurableBenchmark {

    @Param({"1000", "10000", "100000"})
    private int eventsNr;

    private KieSessionHolder rulesExecutor;

    @Setup
    public void setup() {
        String jsonRule = "{ \"rules\": {\"r_0\": {\"all\": [{\"m\": {\"$ex\": {\"event.i\": 1}}}]}}}";
        RulesCompiler rulesCompiler = RulesCompiler.createFromJson(jsonRule);
        rulesExecutor = kieSessionHolder(rulesCompiler.getId());
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    public int benchmark() {
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
