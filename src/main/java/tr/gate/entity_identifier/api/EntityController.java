package tr.gate.entity_identifier.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tr.gate.entity_identifier.service.ONLPPersonEntityService;

import java.util.List;

@RestController
@RequestMapping("/api/entity")
@RequiredArgsConstructor
public class EntityController {

    private final ONLPPersonEntityService entityService;

    @PostMapping("/names")
    public ResponseEntity<List<String>> identifyPersonNames(@RequestBody String text) {
        return new ResponseEntity<>(entityService.identifyPersonNames(text), HttpStatus.OK);
    }
}
