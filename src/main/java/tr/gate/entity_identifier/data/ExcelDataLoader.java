package tr.gate.entity_identifier.data;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class ExcelDataLoader {

    private static final String INPUT_XML_FILE = "gold-data.xlsx";
    private final Map<String, String> excelData = new HashMap<>();

    @PostConstruct
    public void loadExcelData() {
       try (InputStream inputStream = new FileInputStream(INPUT_XML_FILE);
            Workbook workbook = WorkbookFactory.create(inputStream)) {


           Sheet sheet = workbook.getSheetAt(0);
           DataFormatter dataFormatter = new DataFormatter();
           for (Row row : sheet) {
               Cell inputTextCell = row.getCell(0);
               Cell outputTextCell = row.getCell(1);
               if (inputTextCell != null && outputTextCell != null
               && !dataFormatter.formatCellValue(inputTextCell).equals("Input")) {
                   String inputText = dataFormatter.formatCellValue(inputTextCell);
                   log.info("inputText: {}", inputText);
                   String outputText = dataFormatter.formatCellValue(outputTextCell);
                   excelData.put(inputText, outputText);
               }
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
    }

    public Map<String, String> getExcelData() {
        return this.excelData;
    }
}
