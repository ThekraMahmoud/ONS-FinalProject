package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.engine.internal.Cascade;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Set;

@Entity
@Getter
@Setter
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer campaignId;

    @NotBlank(message = "اسم الحملة مطلوب")
    @Size(max = 100, message = "اسم الحملة لا يتجاوز 100 حرف")
    @Column(nullable = false, length = 100)
    private String campaignName;

    @Pattern(
            regexp = "^\\+?[0-9]{8,15}$",
            message = "رقم التواصل غير صحيح"
    )
    @Column(length = 16)
    private String contactNumber;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @NotBlank(message = "حالة الحملة مطلوبة")
    @Pattern(
            regexp = "PLANNED|ACTIVE|COMPLETED|CANCELLED",
            message = "حالة الحملة غير صحيحة"
    )
    @Column(nullable = false, length = 20)
    private String status ;


    @OneToMany(mappedBy = "campaign")
    @JsonIgnore
    private Set<Pilgrim> pilgrims;


    @OneToMany(mappedBy = "campaign")
    @JsonIgnore
    private Set<Accommodation> accommodations;
}



