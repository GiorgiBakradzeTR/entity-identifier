package tr.gate.entity_identifier.service;

import tr.gate.entity_identifier.payload.DocumentResponse;

import java.util.List;

public interface DocumentService {
    void writeDataToExcelFile(DocumentResponse response);
}
