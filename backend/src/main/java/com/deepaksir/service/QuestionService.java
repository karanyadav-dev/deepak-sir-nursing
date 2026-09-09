package com.deepaksir.service;

import com.deepaksir.dto.QuestionRequest;
import com.deepaksir.dto.QuestionResponse;
import com.deepaksir.entity.Question;
import com.deepaksir.entity.Subject;
import com.deepaksir.entity.Topic;
import com.deepaksir.exception.ApiException;
import com.deepaksir.repository.QuestionRepository;
import com.deepaksir.repository.SubjectRepository;
import com.deepaksir.repository.TopicRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final SubjectRepository subjectRepository;
    private final TopicRepository topicRepository;
    
    public QuestionService(QuestionRepository questionRepository,
                          SubjectRepository subjectRepository,
                          TopicRepository topicRepository) {
        this.questionRepository = questionRepository;
        this.subjectRepository = subjectRepository;
        this.topicRepository = topicRepository;
    }
    
    public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findByActiveTrue()
            .stream()
            .map(QuestionResponse::new)
            .collect(Collectors.toList());
    }
    
    public QuestionResponse getQuestionById(UUID id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ApiException("Question not found"));
        return new QuestionResponse(question);
    }
    
    public List<QuestionResponse> getRandomQuestions(UUID subjectId, UUID topicId, int count) {
        List<Question> questions;
        
        if (subjectId != null) {
            questions = questionRepository.findRandomQuestionsBySubject(subjectId, count);
        } else {
            questions = questionRepository.findRandomQuestions(count);
        }
        
        return questions.stream()
            .map(QuestionResponse::new)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = new Question();
        updateQuestionFromRequest(question, request);
        
        Question saved = questionRepository.save(question);
        return new QuestionResponse(saved);
    }
    
    @Transactional
    public QuestionResponse updateQuestion(UUID id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ApiException("Question not found"));
        
        updateQuestionFromRequest(question, request);
        
        Question saved = questionRepository.save(question);
        return new QuestionResponse(saved);
    }
    
    @Transactional
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new ApiException("Question not found"));
        question.setActive(false);
        questionRepository.save(question);
    }
    
    private void updateQuestionFromRequest(Question question, QuestionRequest request) {
        question.setQuestionText(request.getQuestionText());
        question.setOptionA(request.getOptionA());
        question.setOptionB(request.getOptionB());
        question.setOptionC(request.getOptionC());
        question.setOptionD(request.getOptionD());
        question.setCorrectAnswer(request.getCorrectAnswer());
        question.setExplanation(request.getExplanation());
        
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new ApiException("Subject not found"));
            question.setSubject(subject);
        }
        
        if (request.getTopicId() != null) {
            Topic topic = topicRepository.findById(request.getTopicId())
                .orElseThrow(() -> new ApiException("Topic not found"));
            question.setTopic(topic);
        }
        
        if (request.getDifficulty() != null) {
            question.setDifficulty(Question.DifficultyLevel.valueOf(request.getDifficulty()));
        }
        
        if (request.getQuestionType() != null) {
            question.setQuestionType(Question.QuestionType.valueOf(request.getQuestionType()));
        }
    }
}