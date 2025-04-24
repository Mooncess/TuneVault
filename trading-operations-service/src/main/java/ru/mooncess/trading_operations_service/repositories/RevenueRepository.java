package ru.mooncess.trading_operations_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.trading_operations_service.entities.Revenue;

import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, Long> {
    List<Revenue> findAllByProducerId(Long producerId);
}
