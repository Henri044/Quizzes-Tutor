package pt.ulisboa.tecnico.socialsoftware.tutor.execution.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.AssessmentService;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.Assessment;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.AssessmentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.dto.TopicConjunctionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;

import javax.validation.Valid;
import java.util.List;

@RestController
public class AssessmentController {
    private AssessmentService assessmentService;

    AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/assessments/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<AssessmentDto> getExecutionCourseAssessments(@PathVariable int executionId) {
        return this.assessmentService.findAssessments(executionId);
    }

    @GetMapping("/assessments/executions/{executionId}/topicconjunctions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TopicConjunctionDto> getExecutionCourseTopicConjunctions(@PathVariable int executionId, @RequestParam(name = "assessmentId", required = false, defaultValue = "0") int assessmentId) {
        return this.assessmentService.findTopicConjunctions(executionId, assessmentId);
    }

    @GetMapping("/assessments/executions/{executionId}/available")
    @PreAuthorize("(hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')) " +
            "or (hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public List<AssessmentDto> getAvailableAssessments(@PathVariable int executionId) {
        return this.assessmentService.findAvailableAssessments(executionId);
    }

    @PostMapping("/assessments/executions/{executionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public AssessmentDto createAssessment(@PathVariable int executionId, @Valid @RequestBody AssessmentDto assessment) {
        return this.assessmentService.createAssessment(executionId, assessment);
    }

    @PutMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public AssessmentDto updateAssessment(@PathVariable Integer assessmentId, @Valid @RequestBody AssessmentDto assessment) {
        return this.assessmentService.updateAssessment(assessmentId, assessment);
    }

    @DeleteMapping("/assessments/{assessmentId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public void removeAssessment(@PathVariable Integer assessmentId) {
        assessmentService.removeAssessment(assessmentId);
    }

    @PostMapping("/assessments/{assessmentId}/set-status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public void assessmentSetStatus(@PathVariable Integer assessmentId, @Valid @RequestBody String status) {
        assessmentService.assessmentSetStatus(assessmentId, Assessment.Status.valueOf(status));
    }

    @GetMapping("/assessments/{assessmentId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#assessmentId, 'ASSESSMENT.ACCESS')")
    public List<QuestionDto> getAssessmentQuestions(@PathVariable Integer assessmentId) {
        return this.assessmentService.getAssessmentQuestions(assessmentId);
    }

    @PostMapping("/assessments/executions/{executionId}/topicconjunctions/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionDto> getTopicConjunctionQuestions(@PathVariable(value = "executionId") Integer executionId, @RequestBody TopicConjunctionDto topicConjunctionDto) {
        return this.assessmentService.getTopicConjunctionQuestions(executionId, topicConjunctionDto);
    }

}