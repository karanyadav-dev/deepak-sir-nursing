package com.deepaksir.dto;

import com.deepaksir.entity.Subject;
import java.util.UUID;

public class SubjectDTO {
    
    private UUID id;
    private String name;
    private String description;
    private String icon;
    
    public SubjectDTO() {}
    
    public SubjectDTO(Subject subject) {
        this.id = subject.getId();
        this.name = subject.getName();
        this.description = subject.getDescription();
        this.icon = subject.getIcon();
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
}