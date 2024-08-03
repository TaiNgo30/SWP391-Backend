package com.wha.warehousemanagement.schedulers;

import com.wha.warehousemanagement.services.InventorySnapshotService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SnapshotScheduler {
    private final InventorySnapshotService inventorySnapshotService;

    @Scheduled(cron = "0 0 0 L * ?") // Chạy vào ngày cuối cùng hàng tháng
    public void scheduleMonthlySnapshot() {
        inventorySnapshotService.createMonthlySnapshot();
    }
}
