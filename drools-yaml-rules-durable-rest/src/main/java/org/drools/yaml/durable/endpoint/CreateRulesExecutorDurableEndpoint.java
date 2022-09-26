package org.drools.yaml.durable.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.drools.yaml.durable.domain.DurableRules;

import static org.drools.yaml.compilation.RulesCompiler.createRulesCompiler;

@Path("/create-durable-rules-executor")
public class CreateRulesExecutorDurableEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public long createRuleBase(DurableRules durableRules) {
        return createRulesCompiler(durableRules.toRulesSet()).getId();
    }
}
