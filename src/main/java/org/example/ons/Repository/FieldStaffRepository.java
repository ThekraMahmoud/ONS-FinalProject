package org.example.ons.Repository;

import org.example.ons.Model.FieldStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FieldStaffRepository extends JpaRepository<FieldStaff,Integer> {


}
