package org.acme.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Room extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    public Room() {
    }

    public Room(Long id, String name) {
        this.id = id;
        this.name = name.trim();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long x) {
        this.id = x;
    }

    public String getName() {
        return name;
    }
    public void setName(String x){
        this.name = x;
    }

	@Override
	public String toString() {
		return "Room [id=" + id + ", name=" + name + "]";
	}


}
