package org.example.ons.Repository;

import org.example.ons.Model.AuthorizedStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedStaffRepository extends JpaRepository<AuthorizedStaff,Long> {
    AuthorizedStaff findAuthorizedStaffByStaffId(Long staffId);

    AuthorizedStaff findAuthorizedStaffByEmail(String email);
}
