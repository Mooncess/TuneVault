package ru.mooncess.trading_operations_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.trading_operations_service.entities.Withdraw;

import java.util.List;

@Repository
public interface WithdrawRepository extends JpaRepository<Withdraw, Long> {
    List<Withdraw> findAllByProducerIdOrderByWithdrawDateDesc(Long producerId);
}
