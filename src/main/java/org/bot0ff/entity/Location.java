package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bot0ff.entity.enums.LocationType;

import java.io.Serializable;
import java.util.List;

@Data
@Table(name = "location")
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Location implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "locationType")
    @JsonIgnore
    private LocationType locationType;

    @Column(name = "name")
    private String name;

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private int y;

    @Column(name = "ais")
    @JsonIgnore
    private List<Long> ais;

    @Column(name = "units")
    @JsonIgnore
    private List<Long> units;

    @Column(name = "things")
    @JsonIgnore
    private List<Long> things;
}
