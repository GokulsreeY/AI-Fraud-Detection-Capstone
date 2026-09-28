package org.calderacity.repository;

import org.calderacity.entities.FraudPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FraudPredictionRepository
        extends JpaRepository<FraudPrediction, UUID> {
}
