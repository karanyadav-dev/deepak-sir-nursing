package com.deepaksir.dto;

import com.deepaksir.entity.Topic;
import java.util.UUID;

public class TopicDTO {
    
    private UUID id;
    private String name;
    private String description;
    private UUID subjectId;
    private UUID parentId;
    
    public TopicDTO() {}
    
    public TopicDTO(Topic topic) {
        this.id = topic.getId();
        this.name = topic.getName();
        this.description = topic.getDescription();
        this.subjectId = topic.getSubject() != null ? topic.getSubject().getId() : null;
        this.parentId = topic.getParent() != null ? topic.getParent().getId() : null;
    }
    
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public UUID getSubjectId() { return subjectId; }
    public void setSubjectId(UUID subjectId) { this.subjectId = subjectId; }
    
    public UUID getParentId() { return parentId; }
    public void setParentId(UUID parentId) { this.parentId = parentId; }
}