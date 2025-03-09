package com.redis.archive.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.yaml.snakeyaml.events.Event;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String testId;
    private String name;

    public Test(String testId, Object name){
        this.testId = testId;
        this.name = name.toString();
    }
}
