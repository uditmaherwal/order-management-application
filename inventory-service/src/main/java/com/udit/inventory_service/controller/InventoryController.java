package com.udit.inventory_service.controller;

import com.udit.inventory_service.entity.Inventory;
import com.udit.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/{skuCode}")
    public Boolean productQuantity(@PathVariable String skuCode,@RequestParam Long quantity){
        return inventoryService.isProductAvailable(skuCode, quantity);
    }

    @PatchMapping("/{skuCode}")
    public Inventory updateQuantity(@PathVariable String skuCode, @RequestParam Long quantity){
        return inventoryService.updateQuantity(skuCode, quantity);
    }
}
