package tr.gate.entity_identifier.service;

import tr.gate.entity_identifier.Accuracy;
import tr.gate.entity_identifier.payload.DocumentResponse;

import java.util.List;

public interface DocumentService {
    void writeDataToExcelFile(String text, String expectedOutput, List<String> extractedNames,
                              List<String> expectedNames, String difference, Accuracy accuracy,
                              boolean isLastRow);
}
