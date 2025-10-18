package com.auctionaa.backend.Entity;

import lombok.Setter;
import org.springframework.data.annotation.Id;

@Setter
public abstract class BaseEntity {
    @Id
    protected String id;

    public String getId() {
        return id;
    }

    public abstract String getPrefix();

    public void generateId() {
        this.id = getPrefix() + System.nanoTime();
    }
}
