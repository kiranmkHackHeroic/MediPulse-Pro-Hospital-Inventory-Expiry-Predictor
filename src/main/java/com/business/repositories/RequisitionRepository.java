package com.business.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.business.entities.HospitalDepartment;
import com.business.entities.Requisition;

@Repository
public interface RequisitionRepository extends CrudRepository<Requisition, Long> {

	List<Requisition> findByDepartment(HospitalDepartment department);

	List<Requisition> findByStatus(String status);

	List<Requisition> findAllByOrderByCreatedAtDesc();
}
