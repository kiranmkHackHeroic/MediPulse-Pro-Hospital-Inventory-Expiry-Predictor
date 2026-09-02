package com.business.entities;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory_batch")
public class InventoryBatch {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String batchNumber; // e.g., 'MERO-2026-B91', 'STENT-TX-044'
	private String barcodeGtin; // e.g., '01089012345678901726113010BATCH99'

	@ManyToOne
	@JoinColumn(name = "product_pid")
	private Product product;

	@ManyToOne
	@JoinColumn(name = "department_id")
	private HospitalDepartment department;

	private LocalDate manufactureDate;
	private LocalDate expiryDate;

	private int initialQuantity;
	private int currentQuantity;
	private double purchaseCost;

	private String status; // 'ACTIVE', 'NEAR_EXPIRY_60D', 'CRITICAL_30D', 'EXPIRED'

	public InventoryBatch() {
	}

	public InventoryBatch(String batchNumber, String barcodeGtin, Product product, HospitalDepartment department,
			LocalDate manufactureDate, LocalDate expiryDate, int initialQuantity, int currentQuantity,
			double purchaseCost) {
		this.batchNumber = batchNumber;
		this.barcodeGtin = barcodeGtin;
		this.product = product;
		this.department = department;
		this.manufactureDate = manufactureDate;
		this.expiryDate = expiryDate;
		this.initialQuantity = initialQuantity;
		this.currentQuantity = currentQuantity;
		this.purchaseCost = purchaseCost;
		updateStatus();
	}

	public void updateStatus() {
		long days = getDaysToExpiry();
		if (days < 0) {
			this.status = "EXPIRED";
		} else if (days <= 30) {
			this.status = "CRITICAL_30D";
		} else if (days <= 60) {
			this.status = "NEAR_EXPIRY_60D";
		} else {
			this.status = "ACTIVE";
		}
	}

	public long getDaysToExpiry() {
		if (expiryDate == null) return 999;
		return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
	}

	public double getCapitalAtRisk() {
		return currentQuantity * purchaseCost;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public String getBarcodeGtin() {
		return barcodeGtin;
	}

	public void setBarcodeGtin(String barcodeGtin) {
		this.barcodeGtin = barcodeGtin;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public HospitalDepartment getDepartment() {
		return department;
	}

	public void setDepartment(HospitalDepartment department) {
		this.department = department;
	}

	public LocalDate getManufactureDate() {
		return manufactureDate;
	}

	public void setManufactureDate(LocalDate manufactureDate) {
		this.manufactureDate = manufactureDate;
	}

	public LocalDate getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDate expiryDate) {
		this.expiryDate = expiryDate;
		updateStatus();
	}

	public int getInitialQuantity() {
		return initialQuantity;
	}

	public void setInitialQuantity(int initialQuantity) {
		this.initialQuantity = initialQuantity;
	}

	public int getCurrentQuantity() {
		return currentQuantity;
	}

	public void setCurrentQuantity(int currentQuantity) {
		this.currentQuantity = currentQuantity;
	}

	public double getPurchaseCost() {
		return purchaseCost;
	}

	public void setPurchaseCost(double purchaseCost) {
		this.purchaseCost = purchaseCost;
	}

	public String getStatus() {
		if (status == null) updateStatus();
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "InventoryBatch [batchNumber=" + batchNumber + ", expiryDate=" + expiryDate + ", currentQuantity="
				+ currentQuantity + ", status=" + status + "]";
	}
}
