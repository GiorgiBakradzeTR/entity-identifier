package tr.gate.entity_identifier.service;

import java.util.List;

public interface EntityService {
    List<String> identifyPersonNames(String text);
}
