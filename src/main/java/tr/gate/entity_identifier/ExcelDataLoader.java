package tr.gate.entity_identifier;

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

    private final Map<String, String> excelData = new HashMap<>();

    @PostConstruct
    public void loadExcelData() throws IOException {
       try (InputStream inputStream = new FileInputStream("FlexSamples.xlsx");
            Workbook workbook = WorkbookFactory.create(inputStream)) {


           Sheet sheet = workbook.getSheetAt(0);
           for (Row row : sheet) {
               Cell inputTextCell = row.getCell(0);
               Cell outputTextCell = row.getCell(1);
               if (inputTextCell != null && outputTextCell != null
               && !inputTextCell.getStringCellValue().equals("Input")) {
                   excelData.put(inputTextCell.getStringCellValue(), outputTextCell.getStringCellValue());
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
