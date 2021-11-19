package sharma.pankaj.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sharma.pankaj.auth.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Override
    void delete(Privilege privilege);

}