package com.business.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hospital_department")
public class HospitalDepartment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String deptName; // e.g. 'Emergency OT', 'Intensive Care Unit (ICU)', 'Central Pharmacy'
	private String deptCode; // e.g. 'EMERG-OT', 'ICU-MAIN', 'PHARM-CTR'
	private String headOfDept; // e.g. 'Dr. Rajesh Sharma'
	private int bedCapacity; // e.g. 50
	private String floorLocation; // e.g. 'Floor 2, Block B'

	public HospitalDepartment() {
	}

	public HospitalDepartment(String deptName, String deptCode, String headOfDept, int bedCapacity, String floorLocation) {
		this.deptName = deptName;
		this.deptCode = deptCode;
		this.headOfDept = headOfDept;
		this.bedCapacity = bedCapacity;
		this.floorLocation = floorLocation;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeptCode() {
		return deptCode;
	}

	public void setDeptCode(String deptCode) {
		this.deptCode = deptCode;
	}

	public String getHeadOfDept() {
		return headOfDept;
	}

	public void setHeadOfDept(String headOfDept) {
		this.headOfDept = headOfDept;
	}

	public int getBedCapacity() {
		return bedCapacity;
	}

	public void setBedCapacity(int bedCapacity) {
		this.bedCapacity = bedCapacity;
	}

	public String getFloorLocation() {
		return floorLocation;
	}

	public void setFloorLocation(String floorLocation) {
		this.floorLocation = floorLocation;
	}

	@Override
	public String toString() {
		return "HospitalDepartment [id=" + id + ", deptName=" + deptName + ", deptCode=" + deptCode + "]";
	}
}
