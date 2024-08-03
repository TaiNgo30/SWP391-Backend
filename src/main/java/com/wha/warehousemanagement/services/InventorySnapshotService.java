package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.responses.InventorySnapshotResponse;
import com.wha.warehousemanagement.dtos.responses.ZoneResponse;
import com.wha.warehousemanagement.mappers.InventorySnapshotMapper;
import com.wha.warehousemanagement.models.Inventory;
import com.wha.warehousemanagement.models.InventorySnapshot;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.InventoryRepository;
import com.wha.warehousemanagement.repositories.InventorySnapshotRepository;
import com.wha.warehousemanagement.services.ZoneService;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventorySnapshotService {
    private final InventorySnapshotRepository inventorySnapshotRepository;
    private final InventorySnapshotMapper inventorySnapshotMapper;
    private final InventoryRepository inventoryRepository;
    private final ZoneService zoneService;

    public List<InventorySnapshotResponse> getSnapshotsByMonth(int year, int month, int warehouseId) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, 1);
        Date startDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date endDate = calendar.getTime();

        List<Integer> zoneIds = getZoneIdsByWarehouseId(warehouseId);

        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findBySnapshotDateBetweenAndZoneIdIn(startDate, endDate, zoneIds);
        return snapshots.stream()
                .map(inventorySnapshotMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getAllWarehouseSnapshots() {
        List<Map<String, Object>> reportData = new ArrayList<>();
        List<InventorySnapshot> snapshots = inventorySnapshotRepository.findAll();

        for (InventorySnapshot snapshot : snapshots) {
            Map<String, Object> data = new HashMap<>();
            data.put("warehouse", snapshot.getZone().getWarehouse().getName());
            data.put("product", snapshot.getProduct().getName());
            data.put("zone", snapshot.getZone().getName());
            data.put("quantity", snapshot.getQuantity());
            data.put("snapshotDate", formatDate(snapshot.getSnapshotDate()));
            reportData.add(data);
        }

        return reportData;
    }

    public List<Map<String, Object>> getAllWarehouse() {
        List<Map<String, Object>> reportData = new ArrayList<>();
        List<Inventory> inventories = inventoryRepository.findAll();

        for (Inventory inventory : inventories) {
            Map<String, Object> data = new HashMap<>();
            data.put("warehouse", inventory.getZone().getWarehouse().getName());
            data.put("product", inventory.getProduct().getName());
            data.put("zone", inventory.getZone().getName());
            data.put("quantity", inventory.getQuantity());
            reportData.add(data);
        }
        return reportData;
    }

    public List<Map<String, Object>> getMultiMonthSnapshots(int startYear, int startMonth, int endYear, int endMonth, int warehouseId) {
        List<Map<String, Object>> reportData = new ArrayList<>();

        Calendar startCal = Calendar.getInstance();
        startCal.set(startYear, startMonth - 1, 1);

        Calendar endCal = Calendar.getInstance();
        endCal.set(endYear, endMonth - 1, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));

        List<Integer> zoneIds = getZoneIdsByWarehouseId(warehouseId);

        while (!startCal.after(endCal)) {
            int year = startCal.get(Calendar.YEAR);
            int month = startCal.get(Calendar.MONTH) + 1;

            Date startDate = startCal.getTime();
            startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMaximum(Calendar.DAY_OF_MONTH));
            Date endDate = startCal.getTime();
            startCal.set(Calendar.DAY_OF_MONTH, 1);

            List<InventorySnapshot> snapshots = inventorySnapshotRepository.findBySnapshotDateBetweenAndZoneIdIn(
                    startDate,
                    endDate,
                    zoneIds
            );

            for (InventorySnapshot snapshot : snapshots) {
                Map<String, Object> data = new HashMap<>();
                data.put("year", year);
                data.put("month", month);
                data.put("product", snapshot.getProduct().getName());
                data.put("zone", snapshot.getZone().getName());
                data.put("quantity", snapshot.getQuantity());
                data.put("snapshotDate", formatDate(snapshot.getSnapshotDate()));
                reportData.add(data);
            }

            startCal.add(Calendar.MONTH, 1);
        }

        return reportData;
    }

    public void createMonthlySnapshot() {
        createSnapshotForMonth(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public void createSnapshotsForPeriod(int startYear, int startMonth, int endYear, int endMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(startYear, startMonth - 1, 1);
        Date startDate = calendar.getTime();

        calendar.set(endYear, endMonth - 1, 1);
        Date endDate = calendar.getTime();

        while (startDate.before(endDate) || startDate.equals(endDate)) {
            createSnapshotForMonth(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
            calendar.add(Calendar.MONTH, 1);
            startDate = calendar.getTime();
        }
    }

    public void createSnapshotForMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date snapshotDate = calendar.getTime();

        List<Inventory> inventories = inventoryRepository.findAll();
        List<InventorySnapshot> snapshots = inventories.stream()
                .map(inventory -> new InventorySnapshot(
                        null,
                        inventory.getProduct(),
                        inventory.getZone(),
                        snapshotDate,
                        inventory.getQuantity()
                ))
                .collect(Collectors.toList());

        inventorySnapshotRepository.saveAll(snapshots);
    }

    private List<Integer> getZoneIdsByWarehouseId(int warehouseId) {
        ResponseObject<List<ZoneResponse>> responseObject = (ResponseObject<List<ZoneResponse>>) zoneService.getZonesByWarehouseId(warehouseId);
        List<ZoneResponse> zoneResponses = responseObject.getData();
        if (zoneResponses == null) {
            throw new RuntimeException("Failed to fetch zones for warehouse ID: " + warehouseId);
        }
        return zoneResponses.stream()
                .map(ZoneResponse::getId)
                .collect(Collectors.toList());
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public void generateWarehouseExcelReport(List<Map<String, Object>> snapshots, String outputExcelFile) throws IOException {
        if (snapshots == null || snapshots.isEmpty()) {
            throw new IllegalArgumentException("Snapshots list is null or empty");
        }

        // Sắp xếp snapshots theo tiêu chí mong muốn
        snapshots.sort(Comparator.comparing((Map<String, Object> m) -> (String) m.get("warehouse"))
                .thenComparing(m -> (String) m.get("zone"))
                .thenComparing(m -> (String) m.get("product"))
                .thenComparing(m -> (String) m.get("snapshotDate")));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Warehouse Report");

        // Tạo dòng tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Warehouse", "Product Name", "Zone Name"};
        for (int i = 0; i < 3; i++) {  // Assuming a max of 5 quantities and dates for simplicity
            headers = Arrays.copyOf(headers, headers.length + 2);
            headers[headers.length - 2] = "Quantity " + (i + 1);
            headers[headers.length - 1] = "Snapshot Date " + (i + 1);
        }
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Điền dữ liệu
        int rowNum = 1;
        Map<String, List<Object[]>> data = new LinkedHashMap<>();
        for (Map<String, Object> snapshot : snapshots) {
            String key = snapshot.get("warehouse") + "|" + snapshot.get("product") + "|" + snapshot.get("zone");
            if (!data.containsKey(key)) {
                data.put(key, new ArrayList<>());
            }
            data.get(key).add(new Object[]{
                    snapshot.get("quantity"), snapshot.get("snapshotDate")
            });
        }

        for (Map.Entry<String, List<Object[]>> entry : data.entrySet()) {
            Row row = sheet.createRow(rowNum++);
            String[] keys = entry.getKey().split("\\|");
            row.createCell(0).setCellValue(keys[0]);
            row.createCell(1).setCellValue(keys[1]);
            row.createCell(2).setCellValue(keys[2]);

            int cellNum = 3;
            for (Object[] record : entry.getValue()) {
                row.createCell(cellNum++).setCellValue((Integer) record[0]);
                row.createCell(cellNum++).setCellValue((String) record[1]);
            }
        }

        // Tự động điều chỉnh kích thước cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi vào file
        try (FileOutputStream fileOut = new FileOutputStream(outputExcelFile)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }

    public void generateCurrentWarehouseExcelReport(List<Map<String, Object>> inventories, String outputExcelFile) throws IOException {
        if (inventories == null || inventories.isEmpty()) {
            throw new IllegalArgumentException("Inventory list is null or empty");
        }

        // Sắp xếp inventories theo tiêu chí mong muốn
        inventories.sort(Comparator.comparing((Map<String, Object> m) -> (String) m.get("warehouse"))
                .thenComparing(m -> (String) m.get("product"))
                .thenComparing(m -> (String) m.get("zone")));

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Warehouse Report");

        // Tạo dòng tiêu đề
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Warehouse", "Product Name", "Zone Name", "Quantity"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // Điền dữ liệu
        int rowNum = 1;
        for (Map<String, Object> inventory : inventories) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue((String) inventory.get("warehouse"));
            row.createCell(1).setCellValue((String) inventory.get("product"));
            row.createCell(2).setCellValue((String) inventory.get("zone"));
            row.createCell(3).setCellValue((Integer) inventory.get("quantity"));
        }

        // Tự động điều chỉnh kích thước cột
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi vào file
        try (FileOutputStream fileOut = new FileOutputStream(outputExcelFile)) {
            workbook.write(fileOut);
        } finally {
            workbook.close();
        }
    }
}
