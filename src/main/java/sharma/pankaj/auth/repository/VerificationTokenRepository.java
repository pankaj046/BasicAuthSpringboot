package sharma.pankaj.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sharma.pankaj.auth.model.ConfirmationToken;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByToken(String token);
}
