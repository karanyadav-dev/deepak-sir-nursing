package com.deepaksir.service;

import com.deepaksir.entity.Presentation;
import com.deepaksir.entity.Subject;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.PresentationRepository;
import com.deepaksir.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PresentationService {

    private final PresentationRepository presentationRepository;
    private final SubjectRepository subjectRepository;

    @Transactional
    public Presentation uploadPresentation(MultipartFile file, String title, String description, UUID subjectId) {
        try {
            Path uploadDir = Paths.get("./uploads/presentations");
            Files.createDirectories(uploadDir);

            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".pptx";
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadDir.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            Presentation presentation = new Presentation();
            presentation.setTitle(title);
            presentation.setDescription(description);
            presentation.setFileUrl("/api/files/presentations/" + filename);
            presentation.setFileSize(file.getSize());

            if (subjectId != null) {
                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new ApiException("Subject not found"));
                presentation.setSubject(subject);
            }

            return presentationRepository.save(presentation);
        } catch (IOException e) {
            throw new ApiException("Failed to upload presentation: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public List<Presentation> getPublishedPresentations() {
        return presentationRepository.findByPublishedTrueAndActiveTrueOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Presentation getPresentationById(UUID id) {
        return presentationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Presentation not found"));
    }

    @Transactional
    public void deletePresentation(UUID id) {
        Presentation presentation = presentationRepository.findById(id)
                .orElseThrow(() -> new ApiException("Presentation not found"));
        presentation.setActive(false);
        presentationRepository.save(presentation);
    }
}