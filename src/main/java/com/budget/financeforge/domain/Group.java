package com.budget.financeforge.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.TreeSet;

@Entity
@Table(name = "groups")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Group implements Comparable<Group> {

    private Long id;
    private String name;
    private BigDecimal totalBudget;
    private Set<Category> categories = new TreeSet<>();
    private Budget budget ;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "group")
    public Set<Category> getCategories() {
        return categories;
    }

    @Override
    public int compareTo(Group g) {

        return g.name.compareTo(this.name);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Group other = (Group) obj;
        if (id == null)
        {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @ManyToOne
    @JsonIgnore
    public Budget getBudget() {
        return budget;
    }
}
