package tr.gate.entity_identifier.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.gate.entity_identifier.payload.DocumentResponse;
import tr.gate.entity_identifier.service.TaggerService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentController {

    private final TaggerService taggerService;

    @GetMapping("/document")
    public ResponseEntity<List<DocumentResponse>> getDocuments() {
        List<DocumentResponse> response = taggerService.processExcelData();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
