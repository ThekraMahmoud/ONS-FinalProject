package org.example.ons.Repository;

import org.example.ons.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedStaffRepository extends JpaRepository<User,Long> {
    User findAuthorizedStaffByStaffId(Long staffId);

    User findAuthorizedStaffByEmail(String email);
}
