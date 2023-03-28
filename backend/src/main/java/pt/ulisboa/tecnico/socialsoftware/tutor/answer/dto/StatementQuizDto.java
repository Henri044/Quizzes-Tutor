package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuestionAnswerItem;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler;

import java.io.Serializable;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatementQuizDto implements Serializable {
    private Integer id;
    private Integer quizAnswerId;
    private String title;
    private boolean oneWay;
    private boolean timed;
    private String availableDate;
    private String conclusionDate;
    private Integer questionOrder = 0;
    private Long timeToAvailability;
    private Long timeToSubmission;
    private List<StatementQuestionDto> questions = new ArrayList<>();
    private List<StatementAnswerDto> answers = new ArrayList<>();

    public StatementQuizDto() {}

    public StatementQuizDto(QuizAnswer quizAnswer, boolean complete) {
        this.id = quizAnswer.getQuiz().getId();
        this.quizAnswerId = quizAnswer.getId();
        this.title = quizAnswer.getQuiz().getTitle();
        this.oneWay = quizAnswer.getQuiz().isOneWay();
        this.timed = quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS);
        this.availableDate = DateHandler.toISOString(quizAnswer.getQuiz().getAvailableDate());
        this.conclusionDate = DateHandler.toISOString(quizAnswer.getQuiz().getConclusionDate());
        this.questionOrder = quizAnswer.getCurrentSequenceQuestion();

        if (quizAnswer.getQuiz().getConclusionDate() != null && (quizAnswer.getQuiz().getType().equals(Quiz.QuizType.IN_CLASS) || quizAnswer.getQuiz().getType().equals(Quiz.QuizType.TOURNAMENT))) {
            this.timeToSubmission = ChronoUnit.MILLIS.between(DateHandler.now(), quizAnswer.getQuiz().getConclusionDate());
        }

        boolean ghost = this.oneWay && !complete;
        this.questions = quizAnswer.getQuestionAnswers().stream()
                .map(questionAnswer -> new StatementQuestionDto(questionAnswer, ghost))
                .sorted(Comparator.comparing(StatementQuestionDto::getSequence))
                .collect(Collectors.toList());

        this.answers = quizAnswer.getQuestionAnswers().stream()
                .map(StatementAnswerDto::new)
                .sorted(Comparator.comparing(StatementAnswerDto::getSequence))
                .collect(Collectors.toList());
    }

    public StatementQuizDto(QuizAnswer quizAnswer, List<QuestionAnswerItem> items) {
        this(quizAnswer, false);

        List<QuestionAnswerItem> finalItems = new ArrayList<>();
        for (int i = 0; i < items.size() - 1; i++) {
            if (!items.get(i).getQuizQuestionId().equals(items.get(i+1).getQuizQuestionId())) {
                finalItems.add(items.get(i));
            }
        }
        finalItems.add(items.get(items.size() - 1));

        Map<Integer, StatementAnswerDto> mapAnswers = this.answers.stream()
                .collect(Collectors.toMap(StatementAnswerDto::getQuizQuestionId, Function.identity()));
        Map<Integer, QuestionDetails> mapQuestionDetails = quizAnswer.getQuestionAnswers().stream()
                .map(QuestionAnswer::getQuizQuestion)
                .collect(Collectors.toMap(QuizQuestion::getId, quizQuestion -> quizQuestion.getQuestion().getQuestionDetails()));
        for (QuestionAnswerItem finalItem : finalItems) {
            StatementAnswerDto answerDto = mapAnswers.get(finalItem.getQuizQuestionId());

            answerDto.setTimeTaken(finalItem.getTimeTaken());
            answerDto.setTimeToSubmission(finalItem.getTimeToSubmission());
            answerDto.getAnswerDetails().setAnswer(finalItem, mapQuestionDetails.get(finalItem.getQuizQuestionId()));
        }

        this.questionOrder = quizAnswer.getCurrentSequenceQuestion();
    }

    public Integer getId() {
        return id;
    }

    public Integer getQuizAnswerId() {
        return quizAnswerId;
    }

    public void setQuizAnswerId(Integer quizAnswerId) {
        this.quizAnswerId = quizAnswerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public boolean isTimed() {
        return timed;
    }

    public void setTimed(boolean timed) {
        this.timed = timed;
    }

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public Integer getQuestionOrder() {
        return questionOrder;
    }

    public void setQuestionOrder(Integer questionOrder) {
        this.questionOrder = questionOrder;
    }

    public Long getTimeToAvailability() {
        return timeToAvailability;
    }

    public void setTimeToAvailability(Long timeToAvailability) {
        this.timeToAvailability = timeToAvailability;
    }

    public Long getTimeToSubmission() {
        return timeToSubmission;
    }

    public void setTimeToSubmission(Long timeToSubmission) {
        this.timeToSubmission = timeToSubmission;
    }

    public List<StatementQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<StatementQuestionDto> questions) {
        this.questions = questions;
    }

    public List<StatementAnswerDto> getAnswers() {
        return answers;
    }

    public void setAnswers(List<StatementAnswerDto> answers) {
        this.answers = answers;
    }

    @Override
    public String toString() {
        return "StatementQuizDto{" +
                "id=" + id +
                ", quizAnswerId=" + quizAnswerId +
                ", title='" + title + '\'' +
                ", oneWay=" + oneWay +
                ", availableDate='" + availableDate + '\'' +
                ", conclusionDate='" + conclusionDate + '\'' +
                ", timeToAvailability=" + timeToAvailability +
                ", timeToSubmission=" + timeToSubmission +
                ", questions=" + questions +
                ", answers=" + answers +
                '}';
    }
}