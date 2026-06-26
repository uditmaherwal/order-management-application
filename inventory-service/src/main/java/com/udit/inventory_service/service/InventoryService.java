package com.udit.inventory_service.service;

import com.udit.inventory_service.entity.Inventory;
import com.udit.inventory_service.repository.InventoryRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public Boolean isProductAvailable(String skuCode, Long buyingQuantity){
        Optional<Inventory> inventory = inventoryRepository.findBySkuCode(skuCode);
        return inventory.filter(value -> buyingQuantity <= value.getQuantity()).isPresent();
    }

    public Inventory updateQuantity(String skuCode, Long updateQuantity){
        Inventory inventory = inventoryRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new RuntimeException("No inventory found for SKU: " + skuCode));

        if(inventory.getQuantity() < updateQuantity){
            throw new RuntimeException("Insufficient inventory");
        }

        inventory.setQuantity(inventory.getQuantity() - updateQuantity);

        return inventoryRepository.save(inventory);
    }
}
