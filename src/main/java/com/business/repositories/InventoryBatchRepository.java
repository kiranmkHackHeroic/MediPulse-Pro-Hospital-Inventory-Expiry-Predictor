package com.business.repositories;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.business.entities.HospitalDepartment;
import com.business.entities.InventoryBatch;
import com.business.entities.Product;

@Repository
public interface InventoryBatchRepository extends CrudRepository<InventoryBatch, Long> {

	List<InventoryBatch> findByDepartment(HospitalDepartment department);

	List<InventoryBatch> findByProduct(Product product);

	List<InventoryBatch> findByProductOrderByExpiryDateAsc(Product product);

	@Query("SELECT b FROM InventoryBatch b WHERE b.expiryDate <= :cutoffDate AND b.currentQuantity > 0 ORDER BY b.expiryDate ASC")
	List<InventoryBatch> findNearExpiryBatches(@Param("cutoffDate") LocalDate cutoffDate);

	@Query("SELECT b FROM InventoryBatch b ORDER BY b.expiryDate ASC")
	List<InventoryBatch> findAllOrderedByExpiry();
}
