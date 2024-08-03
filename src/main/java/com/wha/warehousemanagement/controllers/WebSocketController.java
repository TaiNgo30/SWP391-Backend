package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.models.Export;
import com.wha.warehousemanagement.models.ExportNotification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @MessageMapping("/export")
    @SendTo("/topic/exports")
    public ExportNotification handleExport(Export export) {
        return new ExportNotification(export);
    }
}
