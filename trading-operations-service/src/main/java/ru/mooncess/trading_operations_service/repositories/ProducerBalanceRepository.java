package ru.mooncess.trading_operations_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.trading_operations_service.entities.ProducerBalance;

@Repository
public interface ProducerBalanceRepository extends JpaRepository<ProducerBalance, Long> {
}
