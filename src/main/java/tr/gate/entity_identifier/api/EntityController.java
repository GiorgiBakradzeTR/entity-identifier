package tr.gate.entity_identifier.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.gate.entity_identifier.service.EntityService;
import tr.gate.entity_identifier.service.SNLPPersonEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/entity")
@RequiredArgsConstructor
public class EntityController {

    private final SNLPPersonEntityService entityService;

    @PostMapping("/names")
    public ResponseEntity<List<String>> identifyPersonNames(@RequestBody String text) {
        return new ResponseEntity<>(entityService.identifyPersonNames(text), HttpStatus.OK);
    }
}
