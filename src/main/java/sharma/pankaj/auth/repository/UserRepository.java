package sharma.pankaj.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import sharma.pankaj.auth.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Modifying
    @Query("update User u set u.isActive = ?1, u.isNotLock = ?2 where u.id = ?3")
    void updateData(boolean isActive, boolean isNotLock, long id);
}
