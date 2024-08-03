package com.wha.warehousemanagement.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.services.InventorySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RestController
@RequestMapping("/inventory-snapshot")
@RequiredArgsConstructor
public class InventorySnapshotController {
    private final InventorySnapshotService inventorySnapshotService;

//    @GetMapping("/report")
//    public ResponseEntity<?> getInventoryReports(@RequestParam("year") int year,
//                                                 @RequestParam("month") int month,
//                                                 @RequestParam("warehouseId") int warehouseId) throws IOException, InterruptedException {
//        List<InventorySnapshotResponse> reports = inventorySnapshotService.getSnapshotsByMonth(year, month, warehouseId);
//
//        if (reports.isEmpty()) {
//            System.out.println("No snapshots found for the given parameters.");
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        File tempFile = File.createTempFile("inventory_snapshot_report", ".json");
//        mapper.writeValue(tempFile, reports);
//
//        // In ra dữ liệu JSON để kiểm tra
//        System.out.println("JSON Data: " + mapper.writeValueAsString(reports));
//
//        String outputExcelFile = tempFile.getAbsolutePath().replace(".json", ".xlsx");
//        ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/python-scripts/generate_excel_report.py", tempFile.getAbsolutePath(), outputExcelFile);
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//
//        if (exitCode != 0) {
//            // Đọc log lỗi từ ProcessBuilder
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            StringBuilder errorMsg = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                errorMsg.append(line).append("\n");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error generating Excel report: " + errorMsg.toString()));
//        }
//
//        File excelFile = new File(outputExcelFile);
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

    @PostMapping("/create-snapshots")
    public ResponseEntity<String> createSnapshotsForPeriod(@RequestParam("startYear") int startYear,
                                                           @RequestParam("startMonth") int startMonth,
                                                           @RequestParam("endYear") int endYear,
                                                           @RequestParam("endMonth") int endMonth) {
        inventorySnapshotService.createSnapshotsForPeriod(startYear, startMonth, endYear, endMonth);
        return ResponseEntity.ok("Snapshots created successfully for the given period.");
    }

//    @GetMapping("/warehouse-report")
//    public ResponseEntity<?> getWarehouseReports() throws IOException, InterruptedException {
//        List<Map<String, Object>> reportData = inventorySnapshotService.getAllWarehouseSnapshots();
//
//        if (reportData.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        File tempFile = File.createTempFile("warehouse_inventory_report", ".json");
//        mapper.writeValue(tempFile, reportData);
//
//        String outputExcelFile = tempFile.getAbsolutePath().replace(".json", ".xlsx");
//        ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/python-scripts/generate_warehouse_excel_report.py", tempFile.getAbsolutePath(), outputExcelFile);
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//
//        if (exitCode != 0) {
//            // Đọc log lỗi từ ProcessBuilder
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            StringBuilder errorMsg = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                errorMsg.append(line).append("\n");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error generating Excel report: " + errorMsg.toString()));
//        }
//
//        File excelFile = new File(outputExcelFile);
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }

    @GetMapping("/warehouse-report")
    public ResponseEntity<?> getWarehouseReports() throws IOException {
        List<Map<String, Object>> reportData = inventorySnapshotService.getAllWarehouseSnapshots();

        if (reportData == null || reportData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        String outputExcelFile = "warehouse_inventory_report.xlsx";
        inventorySnapshotService.generateWarehouseExcelReport(reportData, outputExcelFile);

        File excelFile = new File(outputExcelFile);
        if (!excelFile.exists()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Excel file.");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @GetMapping("/current-warehouse-report")
    public ResponseEntity<?> getCurrentWarehouseReports() throws IOException {
        List<Map<String, Object>> reportData = inventorySnapshotService.getAllWarehouse();

        if (reportData == null || reportData.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        String outputExcelFile = "current_warehouse_inventory_report.xlsx";
        inventorySnapshotService.generateCurrentWarehouseExcelReport(reportData, outputExcelFile);

        File excelFile = new File(outputExcelFile);
        if (!excelFile.exists()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create Excel file.");
        }

        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

//    @GetMapping("/multi-month-report")
//    public ResponseEntity<?> getMultiMonthInventoryReports(@RequestParam("startYear") int startYear,
//                                                           @RequestParam("startMonth") int startMonth,
//                                                           @RequestParam("endYear") int endYear,
//                                                           @RequestParam("endMonth") int endMonth,
//                                                           @RequestParam("warehouseId") int warehouseId) throws IOException, InterruptedException {
//        List<Map<String, Object>> reportData = inventorySnapshotService.getMultiMonthSnapshots(startYear, startMonth, endYear, endMonth, warehouseId);
//
//        if (reportData.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
//        }
//
//        ObjectMapper mapper = new ObjectMapper();
//        File tempFile = File.createTempFile("multi_month_inventory_report", ".json");
//        mapper.writeValue(tempFile, reportData);
//
//        String outputExcelFile = tempFile.getAbsolutePath().replace(".json", ".xlsx");
//        ProcessBuilder processBuilder = new ProcessBuilder("python", "src/main/python-scripts/generate_multi_month_excel_report.py", tempFile.getAbsolutePath(), outputExcelFile);
//        processBuilder.redirectErrorStream(true);
//        Process process = processBuilder.start();
//        int exitCode = process.waitFor();
//
//        if (exitCode != 0) {
//            // Đọc log lỗi từ ProcessBuilder
//            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
//            StringBuilder errorMsg = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                errorMsg.append(line).append("\n");
//            }
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Error generating Excel report: " + errorMsg.toString()));
//        }
//
//        File excelFile = new File(outputExcelFile);
//        InputStreamResource resource = new InputStreamResource(new FileInputStream(excelFile));
//
//        return ResponseEntity.ok()
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + excelFile.getName())
//                .contentType(MediaType.APPLICATION_OCTET_STREAM)
//                .body(resource);
//    }
}
