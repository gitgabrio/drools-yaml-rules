package org.drools.yaml.core.endpoint.durable;

import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.runtime.RulesRuntimeContext;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.drools.yaml.runtime.model.EfestoOutputInteger;
import org.drools.yaml.runtime.utils.InputMaps;
import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

@Path("/rules-durable-executors/{id}/execute")
public class ExecuteRulesDurableEndpoint {

    @Inject
    RuntimeManager runtimeManager;

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public int execute(@PathParam("id") long id, Map<String, Object> factMap) {
        RulesRuntimeContext rulesRuntimeContext = RulesRuntimeContext.create();
        EfestoInputMap efestoInputMap = InputMaps.executeFacts(id, factMap);

        var output = (EfestoOutputInteger) runtimeManager
                .evaluateInput(rulesRuntimeContext, efestoInputMap)
                .stream().findFirst().get();

        return output.getOutputData();
    }
}
