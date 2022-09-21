package org.drools.yaml.runtime.utils;

import java.util.Map;

import org.drools.yaml.runtime.model.EfestoInputId;
import org.drools.yaml.runtime.model.EfestoInputMap;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.runtimemanager.api.model.EfestoInput;

import static org.drools.yaml.runtime.utils.RuntimeUtils.makeModelLocalUriId;

public class InputMaps {

    public static EfestoInput<?> getAllFacts(long id) {
        ModelLocalUriId modelLocalUriId = makeModelLocalUriId(id);
        return new EfestoInputId(modelLocalUriId, id);
    }

    public static EfestoInputMap executeFacts(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "execute-facts");
    }

    public static EfestoInputMap processFacts(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "process-facts");
    }

    public static EfestoInputMap processEvents(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "process-events");
    }

    public static EfestoInputMap retract(long id, Map<String, Object> factMap) {
        return commonInputMap(id, factMap, "retract");
    }

    private static EfestoInputMap commonInputMap(long id, Map<String, Object> factMap, String operation) {
        ModelLocalUriId modelLocalUriId = makeModelLocalUriId(id, operation);
        return new EfestoInputMap(modelLocalUriId, id, factMap, operation);
    }

}
