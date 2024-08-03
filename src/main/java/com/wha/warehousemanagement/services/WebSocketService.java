package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.models.ExportNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private SimpMessagingTemplate template;

    public void sendNotification(ExportNotification notification, Integer warehouseToId) {
        String topic = "/topic/warehouse_" + warehouseToId;
        template.convertAndSend(topic, notification);
    }
}
