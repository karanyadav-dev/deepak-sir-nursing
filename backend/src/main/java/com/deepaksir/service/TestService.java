package com.deepaksir.service;

import com.deepaksir.dto.*;
import com.deepaksir.entity.*;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    
    private final TestRepository testRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    
    @Transactional(readOnly = true)
    public Page<TestResponse> getTests(String search, UUID subjectId, Boolean isPremium, 
                                       Pageable pageable) {
        Page<Test> tests;
        
        if (search != null && !search.isEmpty()) {
            tests = testRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (subjectId != null) {
            tests = testRepository.findBySubjectId(subjectId, pageable);
        } else if (isPremium != null) {
            tests = testRepository.findByIsPremium(isPremium, pageable);
        } else {
            tests = testRepository.findAll(pageable);
        }
        
        return tests.map(this::mapToTestResponse);
    }
    
    @Transactional(readOnly = true)
    public TestResponse getTestById(UUID testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new ApiException("Test not found"));
        return mapToTestResponse(test);
    }
    
    @Transactional
    public TestResponse createTest(TestRequest request) {
        Test test = new Test();
        updateTestFromRequest(test, request);
        
        Test saved = testRepository.save(test);
        return mapToTestResponse(saved);
    }
    
    @Transactional
    public TestResponse updateTest(UUID testId, TestRequest request) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new ApiException("Test not found"));
        
        updateTestFromRequest(test, request);
        
        Test updated = testRepository.save(test);
        return mapToTestResponse(updated);
    }
    
    @Transactional
    public void deleteTest(UUID testId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new ApiException("Test not found"));
        test.setActive(false);
        testRepository.save(test);
    }
    
    @Transactional
    public TestAttemptResponse startTest(UUID testId, UUID userId) {
        Test test = testRepository.findById(testId)
            .orElseThrow(() -> new ApiException("Test not found"));
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ApiException("User not found"));
        
        List<Question> questions = getTestQuestions(test);
        
        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(user);
        attempt.setStartedAt(LocalDateTime.now());
        attempt.setStatus(TestAttempt.AttemptStatus.IN_PROGRESS);
        attempt.setTotalQuestions(questions.size());
        
        TestAttempt saved = testAttemptRepository.save(attempt);
        
        return TestAttemptResponse.builder()
            .attemptId(saved.getId())
            .testId(test.getId())
            .testName(test.getTitle())
            .totalQuestions(questions.size())
            .durationMinutes(test.getDurationMinutes())
            .startedAt(saved.getStartedAt())
            .expiresAt(saved.getStartedAt().plusMinutes(test.getDurationMinutes()))
            .questions(questions.stream().map(this::mapToQuestionDTO).collect(Collectors.toList()))
            .status("IN_PROGRESS")
            .build();
    }
    
    @Transactional
    public TestResultResponse submitTest(UUID attemptId, TestSubmissionRequest submission, 
                                         UUID userId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new ApiException("Test attempt not found"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized to submit this test");
        }
        
        if (attempt.getStatus() == TestAttempt.AttemptStatus.COMPLETED) {
            throw new ApiException("Test already submitted");
        }
        
        List<Question> questions = getTestQuestions(attempt.getTest());
        Map<UUID, String> answers = submission.getAnswers();
        
        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        
        for (Question question : questions) {
            String userAnswer = answers.get(question.getId());
            if (userAnswer == null || userAnswer.isEmpty()) {
                unanswered++;
            } else if (userAnswer.equals(question.getCorrectAnswer())) {
                correct++;
            } else {
                wrong++;
            }
        }
        
        double score = correct * 1.0;
        double maxMarks = questions.size() * 1.0;
        double percentage = maxMarks > 0 ? (score / maxMarks) * 100 : 0;
        double accuracy = (correct + wrong) > 0 ? (correct / (double) (correct + wrong)) * 100 : 0;
        
        attempt.setCorrectAnswers(correct);
        attempt.setWrongAnswers(wrong);
        attempt.setUnanswered(unanswered);
        attempt.setScore(score);
        attempt.setPercentage(percentage);
        attempt.setAccuracy(accuracy);
        attempt.setTimeTakenSeconds(submission.getTimeTakenSeconds());
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setStatus(TestAttempt.AttemptStatus.COMPLETED);
        
        testAttemptRepository.save(attempt);
        
        return TestResultResponse.builder()
            .attemptId(attempt.getId())
            .testId(attempt.getTest().getId())
            .testName(attempt.getTest().getTitle())
            .totalQuestions(questions.size())
            .attemptedQuestions(correct + wrong)
            .correctAnswers(correct)
            .wrongAnswers(wrong)
            .unanswered(unanswered)
            .score(score)
            .maxMarks(maxMarks)
            .percentage(percentage)
            .accuracy(accuracy)
            .timeTakenSeconds(submission.getTimeTakenSeconds())
            .submittedAt(attempt.getSubmittedAt())
            .status("COMPLETED")
            .build();
    }
    
    @Transactional(readOnly = true)
    public TestResultResponse getTestResult(UUID attemptId, UUID userId) {
        TestAttempt attempt = testAttemptRepository.findById(attemptId)
            .orElseThrow(() -> new ApiException("Test attempt not found"));
        
        if (!attempt.getUser().getId().equals(userId)) {
            throw new ApiException("Unauthorized to view this result");
        }
        
        return TestResultResponse.builder()
            .attemptId(attempt.getId())
            .testId(attempt.getTest().getId())
            .testName(attempt.getTest().getTitle())
            .totalQuestions(attempt.getTotalQuestions())
            .attemptedQuestions(attempt.getCorrectAnswers() + attempt.getWrongAnswers())
            .correctAnswers(attempt.getCorrectAnswers())
            .wrongAnswers(attempt.getWrongAnswers())
            .unanswered(attempt.getUnanswered())
            .score(attempt.getScore())
            .percentage(attempt.getPercentage())
            .accuracy(attempt.getAccuracy())
            .timeTakenSeconds(attempt.getTimeTakenSeconds())
            .submittedAt(attempt.getSubmittedAt())
            .status(attempt.getStatus().toString())
            .build();
    }
    
    @Transactional(readOnly = true)
    public Page<TestAttemptSummary> getUserAttempts(UUID userId, Pageable pageable) {
        Page<TestAttempt> attempts = testAttemptRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        
        return attempts.map(attempt -> TestAttemptSummary.builder()
            .attemptId(attempt.getId())
            .testId(attempt.getTest().getId())
            .testName(attempt.getTest().getTitle())
            .totalQuestions(attempt.getTotalQuestions())
            .attemptedQuestions(attempt.getCorrectAnswers() + attempt.getWrongAnswers())
            .correctAnswers(attempt.getCorrectAnswers())
            .wrongAnswers(attempt.getWrongAnswers())
            .unanswered(attempt.getUnanswered())
            .score(attempt.getScore())
            .percentage(attempt.getPercentage())
            .accuracy(attempt.getAccuracy())
            .timeTakenSeconds(attempt.getTimeTakenSeconds())
            .submittedAt(attempt.getSubmittedAt())
            .status(attempt.getStatus().toString())
            .build());
    }
    
    private void updateTestFromRequest(Test test, TestRequest request) {
        test.setTitle(request.getTitle());
        test.setDescription(request.getDescription());
        test.setDurationMinutes(request.getDurationMinutes() != null ? request.getDurationMinutes() : 60);
        test.setTotalQuestions(request.getTotalQuestions() != null ? request.getTotalQuestions() : 10);
        test.setMaxMarks(request.getMaxMarks() != null ? request.getMaxMarks() : request.getTotalQuestions());
        test.setNegativeMarking(request.getNegativeMarking() != null ? request.getNegativeMarking() : 0.0);
        test.setIsPremium(request.isPremium());
        test.setActive(request.getActive() != null ? request.getActive() : true);
        
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ApiException("Subject not found"));
            test.setSubject(subject);
        }
    }
    
    private List<Question> getTestQuestions(Test test) {
        if (test.getQuestions() != null && !test.getQuestions().isEmpty()) {
            return test.getQuestions();
        }
        
        if (test.getSubject() != null) {
            List<Question> questions = questionRepository.findBySubjectId(test.getSubject().getId());
            return questions.stream()
                .limit(test.getTotalQuestions())
                .collect(Collectors.toList());
        }
        
        return questionRepository.findAll()
            .stream()
            .limit(test.getTotalQuestions())
            .collect(Collectors.toList());
    }
    
    private TestResponse mapToTestResponse(Test test) {
        return TestResponse.builder()
            .id(test.getId())
            .title(test.getTitle())
            .description(test.getDescription())
            .durationMinutes(test.getDurationMinutes())
            .totalQuestions(test.getTotalQuestions())
            .maxMarks(test.getMaxMarks())
            .negativeMarking(test.getNegativeMarking())
            .isPremium(test.getIsPremium())
            .active(test.getActive())
            .subjectId(test.getSubject() != null ? test.getSubject().getId() : null)
            .subjectName(test.getSubject() != null ? test.getSubject().getName() : null)
            .createdAt(test.getCreatedAt())
            .build();
    }
    
    private QuestionDTO mapToQuestionDTO(Question question) {
        return QuestionDTO.builder()
            .id(question.getId())
            .questionText(question.getQuestionText())
            .imageUrl(question.getImageUrl())
            .questionType(question.getQuestionType() != null ? question.getQuestionType().toString() : "SINGLE")
            .difficulty(question.getDifficulty() != null ? question.getDifficulty().toString() : "MEDIUM")
            .explanation(question.getExplanation())
            .correctAnswer(question.getCorrectAnswer())
            .optionA(question.getOptionA())
            .optionB(question.getOptionB())
            .optionC(question.getOptionC())
            .optionD(question.getOptionD())
            .subjectId(question.getSubject() != null ? question.getSubject().getId() : null)
            .subjectName(question.getSubject() != null ? question.getSubject().getName() : null)
            .topicId(question.getTopic() != null ? question.getTopic().getId() : null)
            .topicName(question.getTopic() != null ? question.getTopic().getName() : null)
            .build();
    }
}