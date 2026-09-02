package com.business.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.business.entities.HospitalDepartment;
import com.business.entities.InventoryBatch;
import com.business.entities.Product;
import com.business.entities.Requisition;
import com.business.repositories.HospitalDepartmentRepository;
import com.business.repositories.InventoryBatchRepository;
import com.business.repositories.ProductRepository;
import com.business.repositories.RequisitionRepository;

@Service
public class HospitalInventoryService {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private InventoryBatchRepository batchRepository;

	@Autowired
	private HospitalDepartmentRepository departmentRepository;

	@Autowired
	private RequisitionRepository requisitionRepository;

	/**
	 * Compute Expiry Radar metrics: batches expiring within 30, 60, and 90 days,
	 * along with total financial capital at risk.
	 */
	public Map<String, Object> getExpiryRadar(int daysThreshold) {
		Map<String, Object> radar = new HashMap<>();
		LocalDate cutoff = LocalDate.now().plusDays(daysThreshold);

		List<InventoryBatch> allBatches = batchRepository.findAllOrderedByExpiry();
		List<Map<String, Object>> criticalList = new ArrayList<>();
		List<Map<String, Object>> nearExpiryList = new ArrayList<>();
		List<Map<String, Object>> warningList = new ArrayList<>();

		double totalCapitalAtRisk = 0.0;
		int totalAtRiskUnits = 0;

		for (InventoryBatch batch : allBatches) {
			if (batch.getCurrentQuantity() <= 0) continue;
			long daysLeft = batch.getDaysToExpiry();

			if (daysLeft < 0) {
				// Expired
				batch.setStatus("EXPIRED");
			} else if (daysLeft <= 30) {
				batch.setStatus("CRITICAL_30D");
				criticalList.add(formatBatchMap(batch, daysLeft));
				totalCapitalAtRisk += batch.getCapitalAtRisk();
				totalAtRiskUnits += batch.getCurrentQuantity();
			} else if (daysLeft <= 60) {
				batch.setStatus("NEAR_EXPIRY_60D");
				nearExpiryList.add(formatBatchMap(batch, daysLeft));
				totalCapitalAtRisk += batch.getCapitalAtRisk();
				totalAtRiskUnits += batch.getCurrentQuantity();
			} else if (daysLeft <= 90) {
				batch.setStatus("WARNING_90D");
				warningList.add(formatBatchMap(batch, daysLeft));
			}
		}

		radar.put("criticalCount", criticalList.size());
		radar.put("nearExpiryCount", nearExpiryList.size());
		radar.put("warningCount", warningList.size());
		radar.put("totalCapitalAtRisk", Math.round(totalCapitalAtRisk * 100.0) / 100.0);
		radar.put("totalAtRiskUnits", totalAtRiskUnits);
		radar.put("criticalBatches", criticalList);
		radar.put("nearExpiryBatches", nearExpiryList);
		radar.put("warningBatches", warningList);

		// Generate automated inter-ward transfer recommendations
		List<Map<String, String>> recommendations = generateTransferRecommendations(criticalList);
		radar.put("transferRecommendations", recommendations);

		return radar;
	}

	private Map<String, Object> formatBatchMap(InventoryBatch batch, long daysLeft) {
		Map<String, Object> m = new HashMap<>();
		m.put("batchId", batch.getId());
		m.put("batchNumber", batch.getBatchNumber());
		m.put("itemName", batch.getProduct() != null ? batch.getProduct().getPname() : "Unknown");
		m.put("category", batch.getProduct() != null ? batch.getProduct().getCategory() : "Consumable");
		m.put("department", batch.getDepartment() != null ? batch.getDepartment().getDeptName() : "Central Pharmacy");
		m.put("expiryDate", batch.getExpiryDate().toString());
		m.put("daysLeft", daysLeft);
		m.put("quantity", batch.getCurrentQuantity());
		m.put("capitalAtRisk", Math.round(batch.getCapitalAtRisk() * 100.0) / 100.0);
		m.put("status", batch.getStatus());
		return m;
	}

	/**
	 * Recommend moving near-expiry stock from low-velocity departments to high-velocity units
	 * (e.g., Central Pharmacy or Ortho -> Emergency OT or ICU).
	 */
	public List<Map<String, String>> generateTransferRecommendations(List<Map<String, Object>> criticalBatches) {
		List<Map<String, String>> recommendations = new ArrayList<>();
		for (Map<String, Object> b : criticalBatches) {
			String dept = (String) b.get("department");
			String item = (String) b.get("itemName");
			String batchNum = (String) b.get("batchNumber");
			long days = (long) b.get("daysLeft");

			if (!dept.contains("Emergency") && !dept.contains("ICU")) {
				Map<String, String> rec = new HashMap<>();
				rec.put("batchNumber", batchNum);
				rec.put("itemName", item);
				rec.put("fromDepartment", dept);
				rec.put("toDepartment", "Emergency OT / ICU");
				rec.put("reason", "Batch expires in " + days + " days. High daily patient throughput in Emergency OT can consume this before expiry.");
				recommendations.add(rec);
			}
		}
		return recommendations;
	}

	/**
	 * Scan inventory for products below their Reorder Point (ROP) and Safety Stock (SS).
	 */
	public List<Map<String, Object>> getStockoutAlerts() {
		List<Map<String, Object>> alerts = new ArrayList<>();
		Iterable<Product> products = productRepository.findAll();

		for (Product p : products) {
			List<InventoryBatch> batches = batchRepository.findByProduct(p);
			int totalAvailable = 0;
			for (InventoryBatch b : batches) {
				if (b.getDaysToExpiry() > 0) {
					totalAvailable += b.getCurrentQuantity();
				}
			}

			// Dynamic calculations
			int rop = calculateROP(p);
			int ss = calculateSafetyStock(p);
			int eoq = calculateEOQ(p);

			if (totalAvailable <= rop) {
				Map<String, Object> alert = new HashMap<>();
				alert.put("productId", p.getPid());
				alert.put("productName", p.getPname());
				alert.put("category", p.getCategory());
				alert.put("currentStock", totalAvailable);
				alert.put("reorderPoint", rop);
				alert.put("safetyStock", ss);
				alert.put("suggestedOrderQty", eoq);
				alert.put("urgency", totalAvailable <= ss ? "CODE_RED_CRITICAL" : "REORDER_REQUIRED");
				alerts.add(alert);
			}
		}
		return alerts;
	}

	/**
	 * Reorder Point formula: ROP = (dailyBurnRate * leadTimeDays) + SafetyStock
	 */
	public int calculateROP(Product p) {
		int ss = calculateSafetyStock(p);
		double demandDuringLeadTime = p.getDailyBurnRate() * p.getLeadTimeDays();
		return (int) Math.ceil(demandDuringLeadTime + ss);
	}

	/**
	 * Safety stock for 99% service level (Z = 2.33):
	 * SS = Z * stdDev(d) * sqrt(L). Assuming stdDev(d) ~ 30% of daily burn.
	 */
	public int calculateSafetyStock(Product p) {
		double z = 2.33; // 99% clinical availability SLA
		double sigmaD = Math.max(1.0, p.getDailyBurnRate() * 0.35);
		double leadTimeSqrt = Math.sqrt(Math.max(1, p.getLeadTimeDays()));
		return (int) Math.ceil(z * sigmaD * leadTimeSqrt);
	}

	/**
	 * Economic Order Quantity formula: EOQ = sqrt((2 * D * S) / H)
	 * D = annual demand, S = Rs 500 admin order cost, H = unitPrice * 0.15 holding cost
	 */
	public int calculateEOQ(Product p) {
		double annualDemand = p.getDailyBurnRate() * 365.0;
		double orderingCost = 500.0;
		double holdingCost = Math.max(5.0, p.getPprice() * 0.15);
		double eoq = Math.sqrt((2.0 * annualDemand * orderingCost) / holdingCost);
		return Math.max(10, (int) Math.ceil(eoq));
	}

	/**
	 * FEFO (First-Expire-First-Out) Allocation for a consumable item.
	 */
	public List<InventoryBatch> getFefoOrderedBatches(Product product) {
		return batchRepository.findByProductOrderByExpiryDateAsc(product);
	}

	/**
	 * Inter-ward stock transfer execution.
	 */
	public boolean transferStock(Long batchId, Long targetDepartmentId, int quantity) {
		Optional<InventoryBatch> batchOpt = batchRepository.findById(batchId);
		Optional<HospitalDepartment> targetDeptOpt = departmentRepository.findById(targetDepartmentId);

		if (batchOpt.isEmpty() || targetDeptOpt.isEmpty() || quantity <= 0) {
			return false;
		}

		InventoryBatch sourceBatch = batchOpt.get();
		HospitalDepartment targetDept = targetDeptOpt.get();

		if (sourceBatch.getCurrentQuantity() < quantity) {
			return false;
		}

		// Deduct from source batch
		sourceBatch.setCurrentQuantity(sourceBatch.getCurrentQuantity() - quantity);
		batchRepository.save(sourceBatch);

		// Create or allocate to target department batch
		InventoryBatch targetBatch = new InventoryBatch(
			sourceBatch.getBatchNumber() + "-TRF",
			sourceBatch.getBarcodeGtin(),
			sourceBatch.getProduct(),
			targetDept,
			sourceBatch.getManufactureDate(),
			sourceBatch.getExpiryDate(),
			quantity,
			quantity,
			sourceBatch.getPurchaseCost()
		);
		batchRepository.save(targetBatch);
		return true;
	}
}
