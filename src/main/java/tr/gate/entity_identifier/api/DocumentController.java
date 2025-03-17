package tr.gate.entity_identifier.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.gate.entity_identifier.payload.DocumentResponse;
import tr.gate.entity_identifier.service.TaggerService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DocumentController {

    private final TaggerService taggerService;

    public DocumentController(TaggerService taggerService) {
        this.taggerService = taggerService;
    }

    @GetMapping("/document")
    public ResponseEntity<List<DocumentResponse>> getDocuments() {
        return new ResponseEntity<>(taggerService.processExcelData(), HttpStatus.OK);
    }
}
