package org.codenews.repository;

import org.codenews.model.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    boolean existsByEmail(String email);

    Optional<Subscriber> findByEmail(String email);

}
