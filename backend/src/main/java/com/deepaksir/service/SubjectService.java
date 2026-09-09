package com.deepaksir.service;

import com.deepaksir.dto.SubjectDTO;
import com.deepaksir.dto.TopicDTO;
import com.deepaksir.entity.Subject;
import com.deepaksir.entity.Topic;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.SubjectRepository;
import com.deepaksir.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SubjectService {
    
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    
    public SubjectService(SubjectRepository subjectRepository,
                         TopicRepository topicRepository) {
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
    }
    
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findByActiveTrueOrderByName()
            .stream()
            .map(SubjectDTO::new)
            .collect(Collectors.toList());
    }
    
    public SubjectDTO getSubjectById(UUID id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subject not found"));
        return new SubjectDTO(subject);
    }
    
    public List<TopicDTO> getSubjectTopics(UUID subjectId) {
        return topicRepository.findBySubjectIdAndActiveTrueOrderByName(subjectId)
            .stream()
            .map(TopicDTO::new)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public SubjectDTO createSubject(SubjectDTO subjectDTO) {
        if (subjectRepository.existsByNameIgnoreCase(subjectDTO.getName())) {
            throw new ApiException("Subject already exists");
        }
        
        Subject subject = new Subject();
        subject.setName(subjectDTO.getName());
        subject.setDescription(subjectDTO.getDescription());
        subject.setIcon(subjectDTO.getIcon());
        subject.setActive(true);
        
        Subject saved = subjectRepository.save(subject);
        return new SubjectDTO(saved);
    }
    
    @Transactional
    public SubjectDTO updateSubject(UUID id, SubjectDTO subjectDTO) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subject not found"));
        
        subject.setName(subjectDTO.getName());
        subject.setDescription(subjectDTO.getDescription());
        subject.setIcon(subjectDTO.getIcon());
        
        Subject saved = subjectRepository.save(subject);
        return new SubjectDTO(saved);
    }
    
    @Transactional
    public void deleteSubject(UUID id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ApiException("Subject not found"));
        subject.setActive(false);
        subjectRepository.save(subject);
    }
}