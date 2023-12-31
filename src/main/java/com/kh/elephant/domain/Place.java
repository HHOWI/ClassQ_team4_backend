package com.kh.elephant.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
@Builder
@Table(name = "PLACE")
public class Place {

    @Id
    @Column(name = "place_seq")
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "placeSequence")
    @SequenceGenerator(name = "placeSequence", sequenceName = "SEQ_PLACE", allocationSize = 1)
    private int placeSEQ;

    @Column(name = "PLACE_NAME")
    private String placeName;

    @ManyToOne
    @JoinColumn(name = "PLACE_TYPE_SEQ", referencedColumnName = "PLACE_TYPE_SEQ")
    private PlaceType placeType;

}
