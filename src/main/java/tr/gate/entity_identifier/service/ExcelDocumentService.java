package tr.gate.entity_identifier.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import tr.gate.entity_identifier.Accuracy;

import java.io.*;
import java.util.List;

@Service
@Slf4j
public class ExcelDocumentService implements DocumentService {

    private static final String FILE_PATH = "output_data.xlsx";
    private Workbook workbook;
    private int nextRowNum = 1;
    private Sheet sheet;
    private boolean initialized = false;

    public ExcelDocumentService() {
        initializeWorkbook();
    }

    private void initializeWorkbook() {
        ZipSecureFile.setMinInflateRatio(0.001);
        File file = new File(FILE_PATH);

        if (file.exists()) {
            try (FileInputStream inputStream = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(inputStream);
                sheet = workbook.getSheetAt(0);
                nextRowNum = sheet.getLastRowNum() + 1;
                initialized = true;
            } catch (IOException e) {
                throw new RuntimeException("Error reading the existing excel file ", e);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Output");
            createHeaderRow(sheet);
            initialized = true;
        }

    }

    @Override
    public void writeDataToExcelFile(String text, String expectedOutput,
                                     List<String> extractedNames, List<String> expectedNames,
                                     String difference, Accuracy accuracy, boolean isLastRow) {

        if (!initialized) {
            initializeWorkbook();
        }
        Row row = sheet.createRow(nextRowNum++);

        log.info("nextRowNum");
        row.createCell(0).setCellValue(text);
        row.createCell(1).setCellValue(expectedOutput);
        row.createCell(2).setCellValue(String.join(", ", extractedNames));
        row.createCell(3).setCellValue(String.join(", ", expectedNames));
        row.createCell(4).setCellValue(difference);
        row.createCell(5).setCellValue(accuracy.getMatchCount());
        applyPercentageStyleToColumn(row.createCell(6), accuracy, workbook);

        try (FileOutputStream fileOut = new FileOutputStream("output_data.xlsx")) {

            workbook.write(fileOut);
            if (isLastRow) {
                log.info("CLOSE REACHED");
                workbook.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to excel file", e);
        }

        log.info("Excel file created: {}", "output_data.xlsx");
    }


    private void applyPercentageStyleToColumn(Cell precisionCell, Accuracy accuracy, Workbook workbook) {
        precisionCell.setCellValue(accuracy.getPrecision() / 100);
        CellStyle percentStyle = workbook.createCellStyle();
        percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00%"));
        precisionCell.setCellStyle(percentStyle);
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Input");
        headerRow.createCell(1).setCellValue("Expected Output");
        headerRow.createCell(2).setCellValue("Identified names from Input");
        headerRow.createCell(3).setCellValue("Names from Expected Output");
        headerRow.createCell(4).setCellValue("Difference");
        headerRow.createCell(5).setCellValue("Match Count");
        headerRow.createCell(6).setCellValue("Precision (%)");
    }
}
