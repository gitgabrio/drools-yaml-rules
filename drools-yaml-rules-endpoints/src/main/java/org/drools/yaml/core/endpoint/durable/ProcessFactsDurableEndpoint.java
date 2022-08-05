package org.drools.yaml.core.endpoint.durable;

import org.kie.efesto.runtimemanager.api.service.RuntimeManager;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

import static org.drools.yaml.runtime.utils.RuntimeUtils.processFactsDurableRules;

@Path("/rules-durable-executors/{id}/process-facts")
public class ProcessFactsDurableEndpoint {

    private static final RuntimeManager runtimeManager =
            org.kie.efesto.runtimemanager.api.utils.SPIUtils.getRuntimeManager(true).get();

    @POST()
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<Map<String, Map>> process(@PathParam("id") long id, Map<String, Object> factMap) {
        return processFactsDurableRules(id, factMap);
    }
}
