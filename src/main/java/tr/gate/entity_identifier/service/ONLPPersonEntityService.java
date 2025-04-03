package tr.gate.entity_identifier.service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ONLPPersonEntityService implements EntityService {

    public static final String PERIOD_SYMBOL = ".";
    public static final String WHITESPACE = " ";
    private final NameFinderME nameFinder;

    public ONLPPersonEntityService() throws IOException {
        try (InputStream modelIn = getClass().getResourceAsStream("/en-ner-person.bin")) {
            if (modelIn == null) {
                throw new IOException("Model file not found in resources");
            }
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            nameFinder = new NameFinderME(model);
        }
    }

    public List<String> identifyPersonNames(String text) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);

        Span[] nameSpans = nameFinder.find(tokens);

        Set<String> personNames = new HashSet<>();
        for (Span span : nameSpans) {
            StringBuilder name = new StringBuilder();
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                name.append(tokens[i]);
                if (tokens[i].equals(PERIOD_SYMBOL)) {
                    name.setLength(name.length() - 2);
                    name.append(tokens[i]);
                }
                name.append(WHITESPACE);

            }
            personNames.add(name.toString().trim());
        }
        return new ArrayList<>(personNames);
    }
}
