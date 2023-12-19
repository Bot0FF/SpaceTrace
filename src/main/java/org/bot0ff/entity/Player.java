package org.bot0ff.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "players")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Location location;

    @Column(name = "x")
    private int x;

    @Column(name = "y")
    private int y;

    @Column(name = "hp")
    private int hp;

    @Column(name = "mana")
    private int mana;

    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private Status status;

    public Long getLocationId() {
        return Long.parseLong("" + this.getX() + this.getY());
    }
}
