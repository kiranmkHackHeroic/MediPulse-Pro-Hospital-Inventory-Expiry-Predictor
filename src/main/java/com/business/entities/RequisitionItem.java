package com.business.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "requisition_items")
public class RequisitionItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "requisition_id")
	private Requisition requisition;

	@ManyToOne
	@JoinColumn(name = "product_pid")
	private Product product;

	private int quantityRequested;
	private int quantityApproved;
	private double unitPrice;

	public RequisitionItem() {
	}

	public RequisitionItem(Product product, int quantityRequested, double unitPrice) {
		this.product = product;
		this.quantityRequested = quantityRequested;
		this.quantityApproved = quantityRequested;
		this.unitPrice = unitPrice;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Requisition getRequisition() {
		return requisition;
	}

	public void setRequisition(Requisition requisition) {
		this.requisition = requisition;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public int getQuantityRequested() {
		return quantityRequested;
	}

	public void setQuantityRequested(int quantityRequested) {
		this.quantityRequested = quantityRequested;
	}

	public int getQuantityApproved() {
		return quantityApproved;
	}

	public void setQuantityApproved(int quantityApproved) {
		this.quantityApproved = quantityApproved;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}
}
