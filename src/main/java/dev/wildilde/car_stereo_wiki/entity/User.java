package dev.wildilde.car_stereo_wiki.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "user")
@Entity
public class User {

    @Id
    private String id;
    private boolean admin;

    public User() {
    }

    public User(String id, boolean admin) {
        this.id = id;
        this.admin = admin;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
