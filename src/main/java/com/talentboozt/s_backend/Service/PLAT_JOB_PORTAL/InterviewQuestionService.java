package com.talentboozt.s_backend.Service.PLAT_JOB_PORTAL;

import com.talentboozt.s_backend.Model.PLAT_JOB_PORTAL.InterviewQuestionModel;
import com.talentboozt.s_backend.Repository.PLAT_JOB_PORTAL.InterviewQuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class InterviewQuestionService {
    private final InterviewQuestionRepository repository;

    @Autowired
    public InterviewQuestionService(InterviewQuestionRepository repository) {
        this.repository = repository;
    }

    public InterviewQuestionModel saveQuestion(InterviewQuestionModel question) {
        return repository.save(question);
    }

    public List<InterviewQuestionModel> getAllQuestions() { return repository.findAll(); }

    @Transactional
    public void incrementQuestionViewCount(String questionId) {
        InterviewQuestionModel questionModel = repository.findByQuestions_Id(questionId)
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        InterviewQuestionModel.Question question = questionModel.getQuestions().stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Question not found"));

        question.setViewCount(question.getViewCount() + 1);
        repository.save(questionModel);
    }

    @Transactional
    public void incrementAnswerViewCount(String answerId) {
        InterviewQuestionModel questionModel = repository.findByQuestions_Answers_Id(answerId)
                .orElseThrow(() -> new NoSuchElementException("Question containing the answer not found"));

        InterviewQuestionModel.Question question = questionModel.getQuestions().stream()
                .filter(q -> q.getAnswers().stream().anyMatch(a -> a.getId().equals(answerId)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Question with answer not found"));

        InterviewQuestionModel.Answer answer = question.getAnswers().stream()
                .filter(a -> a.getId().equals(answerId))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Answer not found"));

        answer.setViewCount(answer.getViewCount() + 1);
        repository.save(questionModel);
    }
}
