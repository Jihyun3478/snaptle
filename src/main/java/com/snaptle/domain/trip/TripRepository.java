package com.snaptle.domain.trip;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TripRepository extends JpaRepository<Trip, Long> {

    Optional<Trip> findByInviteCode(String inviteCode);

    boolean existsByInviteCode(String inviteCode);
}
