package com.udit.inventory_service;

import com.udit.inventory_service.entity.Inventory;
import com.udit.inventory_service.repository.InventoryRepository;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Optional;

@SpringBootApplication
public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
	}

	//Automatically post data in database at startup of application
	@Bean
	public CommandLineRunner loadData(InventoryRepository inventoryRepository) {
		return args -> {
			// Step 1: Create a new Inventory object
			Inventory inventory = new Inventory();
			// Step 2: Set the skuCode to "Laptop-901" and quantity to 100
			inventory.setSkuCode("Laptop-901");
			inventory.setQuantity(100);
			// Step 3: Save it to the database
			Optional<Inventory> existingInventory = inventoryRepository.findBySkuCode(inventory.getSkuCode());
			if(existingInventory.isEmpty()) {
				inventoryRepository.save(inventory);
			}
		};
	}
}
