package org.calderacity.repository;

import org.calderacity.entities.FraudOutboxEvent;
import org.calderacity.enums.FraudOutboxStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface FraudOutboxEventRepository extends JpaRepository<FraudOutboxEvent, UUID> {

    List<FraudOutboxEvent> findByStatusOrderByCreatedAtAsc(
            FraudOutboxStatus status);
}
