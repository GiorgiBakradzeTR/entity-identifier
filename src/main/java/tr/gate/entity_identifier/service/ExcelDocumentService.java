package tr.gate.entity_identifier.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tr.gate.entity_identifier.payload.DocumentResponse;

import java.io.*;
import java.util.List;

@Service
@Slf4j
public class ExcelDocumentService implements DocumentService {

    private static final String FILE_PATH = "updated_data.xlsx";
    private int nextRowNum = 1;

    @Override
    public void writeDataToExcelFile(DocumentResponse response) {
        File file = new File(FILE_PATH);
        Workbook workbook;
        Sheet sheet;

        List<String> extractedNames = response.getNames();
        List<String> expectedNames = response.getExpectedNames();
        String difference = response.getDifference();

        if (file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
              workbook = new XSSFWorkbook(inputStream);
              sheet = workbook.getSheetAt(0);
            } catch (IOException e) {
                throw new RuntimeException("Error reading the existing excel file ", e);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Output");

            createHeaderRow(sheet);
        }

        Row row = sheet.createRow(nextRowNum);

        log.info("nextRowNum");
        row.createCell(0).setCellValue(String.join(", ", extractedNames));
        row.createCell(1).setCellValue(String.join(", ", expectedNames));
        row.createCell(2).setCellValue(difference);

        nextRowNum++;

        try (FileOutputStream fileOut = new FileOutputStream("updated_data.xlsx")) {
            workbook.write(fileOut);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to excel file", e);
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Excel file created: " + "updated_data.xlsx");
    }

    private void createHeaderRow(Sheet sheet) {

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Identified names from Input");
        headerRow.createCell(1).setCellValue("Names from Expected Output");
        headerRow.createCell(2).setCellValue("Difference");
    }
}
