package com.business.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "requisitions")
public class Requisition {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String requisitionNumber; // e.g. 'REQ-2026-0811'

	@ManyToOne
	@JoinColumn(name = "department_id")
	private HospitalDepartment department;

	private String requestedBy; // Staff name or doctor
	private String urgency; // 'ROUTINE', 'URGENT_24H', 'CODE_RED_CRITICAL'
	private String status; // 'PENDING_APPROVAL', 'APPROVED_BY_PHARMACY', 'DISPATCHED', 'REJECTED'
	private double totalEstimatedCost;
	private LocalDateTime createdAt = LocalDateTime.now();
	private String notes;

	@OneToMany(mappedBy = "requisition", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RequisitionItem> items = new ArrayList<>();

	public Requisition() {
	}

	public Requisition(String requisitionNumber, HospitalDepartment department, String requestedBy, String urgency,
			String status, double totalEstimatedCost, String notes) {
		this.requisitionNumber = requisitionNumber;
		this.department = department;
		this.requestedBy = requestedBy;
		this.urgency = urgency;
		this.status = status;
		this.totalEstimatedCost = totalEstimatedCost;
		this.notes = notes;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRequisitionNumber() {
		return requisitionNumber;
	}

	public void setRequisitionNumber(String requisitionNumber) {
		this.requisitionNumber = requisitionNumber;
	}

	public HospitalDepartment getDepartment() {
		return department;
	}

	public void setDepartment(HospitalDepartment department) {
		this.department = department;
	}

	public String getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(String requestedBy) {
		this.requestedBy = requestedBy;
	}

	public String getUrgency() {
		return urgency;
	}

	public void setUrgency(String urgency) {
		this.urgency = urgency;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public double getTotalEstimatedCost() {
		return totalEstimatedCost;
	}

	public void setTotalEstimatedCost(double totalEstimatedCost) {
		this.totalEstimatedCost = totalEstimatedCost;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public List<RequisitionItem> getItems() {
		return items;
	}

	public void setItems(List<RequisitionItem> items) {
		this.items = items;
	}

	public void addItem(RequisitionItem item) {
		items.add(item);
		item.setRequisition(this);
	}
}
