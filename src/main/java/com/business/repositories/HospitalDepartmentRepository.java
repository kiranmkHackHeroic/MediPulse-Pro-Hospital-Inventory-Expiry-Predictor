package com.business.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.business.entities.HospitalDepartment;

@Repository
public interface HospitalDepartmentRepository extends CrudRepository<HospitalDepartment, Long> {
	Optional<HospitalDepartment> findByDeptCode(String deptCode);
}
