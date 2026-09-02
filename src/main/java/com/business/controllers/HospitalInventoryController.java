package com.business.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.business.entities.HospitalDepartment;
import com.business.entities.InventoryBatch;
import com.business.entities.Product;
import com.business.entities.Requisition;
import com.business.repositories.HospitalDepartmentRepository;
import com.business.repositories.InventoryBatchRepository;
import com.business.repositories.ProductRepository;
import com.business.repositories.RequisitionRepository;
import com.business.services.HospitalInventoryService;

@RestController
@RequestMapping("/api/hospital")
public class HospitalInventoryController {

	@Autowired
	private HospitalInventoryService inventoryService;

	@Autowired
	private HospitalDepartmentRepository departmentRepository;

	@Autowired
	private InventoryBatchRepository batchRepository;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private RequisitionRepository requisitionRepository;

	/**
	 * Expiry Radar & Capital at Risk Telemetry
	 */
	@GetMapping("/expiry-radar")
	public ResponseEntity<Map<String, Object>> getExpiryRadar(
			@RequestParam(defaultValue = "60") int daysThreshold) {
		return ResponseEntity.ok(inventoryService.getExpiryRadar(daysThreshold));
	}

	/**
	 * Stockout & ROP alerts across all hospital consumables
	 */
	@GetMapping("/stockout-alerts")
	public ResponseEntity<List<Map<String, Object>>> getStockoutAlerts() {
		return ResponseEntity.ok(inventoryService.getStockoutAlerts());
	}

	/**
	 * List all hospital departments / wards
	 */
	@GetMapping("/departments")
	public ResponseEntity<Iterable<HospitalDepartment>> getDepartments() {
		return ResponseEntity.ok(departmentRepository.findAll());
	}

	/**
	 * Inter-ward stock transfer
	 */
	@PostMapping("/transfer")
	public ResponseEntity<Map<String, Object>> transferStock(@RequestBody Map<String, Object> payload) {
		Map<String, Object> response = new HashMap<>();
		try {
			Long batchId = Long.valueOf(payload.get("batchId").toString());
			Long targetDeptId = Long.valueOf(payload.get("targetDepartmentId").toString());
			int quantity = Integer.parseInt(payload.get("quantity").toString());

			boolean success = inventoryService.transferStock(batchId, targetDeptId, quantity);
			response.put("success", success);
			response.put("message", success ? "Stock successfully transferred and re-allocated via FEFO rule." : "Transfer failed: insufficient batch stock or invalid department.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * Add or register a newly received consumable batch
	 */
	@PostMapping("/batch/add")
	public ResponseEntity<Map<String, Object>> addBatch(@RequestBody Map<String, Object> payload) {
		Map<String, Object> response = new HashMap<>();
		try {
			int productId = Integer.parseInt(payload.get("productId").toString());
			long deptId = Long.parseLong(payload.get("departmentId").toString());
			String batchNumber = payload.get("batchNumber").toString();
			String barcodeGtin = payload.getOrDefault("barcodeGtin", "010890" + System.currentTimeMillis()).toString();
			LocalDate expiryDate = LocalDate.parse(payload.get("expiryDate").toString());
			int quantity = Integer.parseInt(payload.get("quantity").toString());
			double cost = Double.parseDouble(payload.get("purchaseCost").toString());

			Optional<Product> prodOpt = productRepository.findById(productId);
			Optional<HospitalDepartment> deptOpt = departmentRepository.findById(deptId);

			if (prodOpt.isEmpty() || deptOpt.isEmpty()) {
				response.put("success", false);
				response.put("message", "Invalid Product ID or Department ID");
				return ResponseEntity.badRequest().body(response);
			}

			InventoryBatch batch = new InventoryBatch(
				batchNumber,
				barcodeGtin,
				prodOpt.get(),
				deptOpt.get(),
				LocalDate.now().minusMonths(1),
				expiryDate,
				quantity,
				quantity,
				cost
			);
			batchRepository.save(batch);

			response.put("success", true);
			response.put("batchId", batch.getId());
			response.put("message", "Batch " + batchNumber + " registered with FEFO expiry tracking.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Error registering batch: " + e.getMessage());
			return ResponseEntity.badRequest().body(response);
		}
	}

	/**
	 * Approve a requisition and mark it for pharmacy dispatch
	 */
	@PostMapping("/requisition/{id}/approve")
	public ResponseEntity<Map<String, Object>> approveRequisition(@PathVariable("id") long id) {
		Map<String, Object> res = new HashMap<>();
		Optional<Requisition> reqOpt = requisitionRepository.findById(id);
		if (reqOpt.isPresent()) {
			Requisition req = reqOpt.get();
			req.setStatus("APPROVED_BY_PHARMACY");
			requisitionRepository.save(req);
			res.put("success", true);
			res.put("message", "Requisition " + req.getRequisitionNumber() + " approved for dispatch.");
			return ResponseEntity.ok(res);
		}
		res.put("success", false);
		res.put("message", "Requisition not found");
		return ResponseEntity.badRequest().body(res);
	}
}
