package ru.mooncess.trading_operations_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.mooncess.trading_operations_service.entities.Revenue;

import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    @Query("SELECT r FROM Revenue r JOIN r.sale s WHERE r.producerId = :producerId ORDER BY s.saleDate DESC")
    List<Revenue> findAllByProducerIdOrderBySaleDateDesc(@Param("producerId") Long producerId);
}

