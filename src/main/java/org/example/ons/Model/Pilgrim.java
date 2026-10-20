package org.example.ons.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pilgrim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pilgrimId;

    @NotBlank(message = "اسم الحاج مطلوب")
    @Size(max = 120, message = "الاسم لا يتجاوز 120 حرفاً")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "الجنسية مطلوبة")
    @Size(max = 60, message = "الجنسية لا تتجاوز 60 حرفاً")
    @Column(nullable = false)
    private String nationality;


    @Pattern(regexp = "^\\+?[0-9]{8,15}$",
            message = "رقم الجوال غير صحيح")
    @Column(nullable = false)
    private String phoneNumber;


    @NotBlank(message = "رقم التأشيرة مطلوب")
    @Column(unique = true, nullable = false)
    private String visaNumber;



    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonIgnore
    private Campaign campaign;


    @ManyToOne
    @JoinColumn
    private Accommodation accommodation;



    @OneToOne(mappedBy = "pilgrim")
    @JsonIgnore
    private PilgrimCard pilgrimCard;

}