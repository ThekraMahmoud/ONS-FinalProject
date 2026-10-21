package org.example.ons.Repository;

import org.example.ons.Model.AuthorizedStaff;
import org.example.ons.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorizedStaffRepository extends JpaRepository<AuthorizedStaff,Integer> {
    AuthorizedStaff findAuthorizedStaffById(Integer staffId);
}
