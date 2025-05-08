package ru.mooncess.media_catalog_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mooncess.media_catalog_service.domain.enums.UserStatus;
import ru.mooncess.media_catalog_service.entities.Producer;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long> {
    Optional<Producer> findByEmail(String email);
    List<Producer> findAllByNicknameContainingIgnoreCase(String nicknamePart);
    List<Producer> findAllByUserStatus(UserStatus userStatus);
    List<Producer> findAllByNicknameContainingIgnoreCaseAndUserStatus(String nicknamePart, UserStatus userStatus);
}
