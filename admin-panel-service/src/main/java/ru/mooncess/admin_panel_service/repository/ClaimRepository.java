package ru.mooncess.admin_panel_service.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.admin_panel_service.domain.enums.ClaimStatus;
import ru.mooncess.admin_panel_service.entity.Claim;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findAllByStatus(ClaimStatus status, Sort sort);
}
