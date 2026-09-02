package com.business.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int pid;
	private String pname;
	private double pprice;
	private String pdescription;

	// Hospital Formulary & Consumable Metadata
	private String genericName;
	private String category; // 'Surgical Implants', 'Critical Care IV', 'Anesthesia', 'Controlled Narcotics', 'PPE'
	private String dosageForm; // 'Vial', 'Syringe', 'Ampoule', 'Unit', 'Box'
	private String storageTemp; // '2°C - 8°C (Cold Chain)', '15°C - 25°C (Controlled Room)'
	private int minSafetyStock = 30; // Threshold below which emergency buffer is tapped
	private int reorderPoint = 60; // Algorithmic ROP
	private int leadTimeDays = 3; // Supplier delivery lead time
	private double dailyBurnRate = 5.0; // Average daily consumable consumption

	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getPname() {
		return pname;
	}
	public void setPname(String pname) {
		this.pname = pname;
	}
	public double getPprice() {
		return pprice;
	}
	public void setPprice(double pprice) {
		this.pprice = pprice;
	}
	public String getPdescription() {
		return pdescription;
	}
	public void setPdescription(String pdescription) {
		this.pdescription = pdescription;
	}
	public String getGenericName() {
		return genericName != null ? genericName : pname;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	public String getCategory() {
		return category != null ? category : "Clinical Consumables";
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getDosageForm() {
		return dosageForm != null ? dosageForm : "Unit";
	}
	public void setDosageForm(String dosageForm) {
		this.dosageForm = dosageForm;
	}
	public String getStorageTemp() {
		return storageTemp != null ? storageTemp : "15°C - 25°C";
	}
	public void setStorageTemp(String storageTemp) {
		this.storageTemp = storageTemp;
	}
	public int getMinSafetyStock() {
		return minSafetyStock;
	}
	public void setMinSafetyStock(int minSafetyStock) {
		this.minSafetyStock = minSafetyStock;
	}
	public int getReorderPoint() {
		return reorderPoint;
	}
	public void setReorderPoint(int reorderPoint) {
		this.reorderPoint = reorderPoint;
	}
	public int getLeadTimeDays() {
		return leadTimeDays;
	}
	public void setLeadTimeDays(int leadTimeDays) {
		this.leadTimeDays = leadTimeDays;
	}
	public double getDailyBurnRate() {
		return dailyBurnRate;
	}
	public void setDailyBurnRate(double dailyBurnRate) {
		this.dailyBurnRate = dailyBurnRate;
	}

	@Override
	public String toString() {
		return "Product [pid=" + pid + ", pname=" + pname + ", pprice=" + pprice + ", category=" + category + "]";
	}
}
