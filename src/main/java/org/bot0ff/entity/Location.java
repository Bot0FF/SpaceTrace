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
    @JsonIgnore
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "locationType")
    @JsonIgnore
    private LocationType locationType;

    @Column(name = "name")
    private String name;

    @Column(name = "coordinate")
    private String coordinate;

    @Column(name = "ais")
    @JsonIgnore
    private List<Long> ais;

    @Column(name = "units")
    @JsonIgnore
    private List<Long> units;

    @Column(name = "things")
    @JsonIgnore
    private List<Long> things;

    @Column(name = "isWorld")
    private boolean isWorld;

    @Column(name = "doorId")
    private Long doorId;
}
