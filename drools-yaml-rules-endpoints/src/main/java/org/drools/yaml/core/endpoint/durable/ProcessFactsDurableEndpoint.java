package org.drools.yaml.core.endpoint.durable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/rules-durable-executors/{id}/process")
public class ProcessFactsDurableEndpoint {

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Map>> process(@PathParam("id") long id, Map<String, Object> factMap) {
        return Collections.emptyList();
//        return RulesExecutorContainer.INSTANCE.get(id).process(factMap).stream()
//                .map(DurableRuleMatch::from).collect(Collectors.toList());
    }
}
