package pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.StudentDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.TeacherDashboardDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.dto.StatsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.dashboard.services.DashboardService;

import java.security.Principal;

@RestController
public class DashboardController {
    @Autowired
    private DashboardService dashboardService;

    DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/students/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public StudentDashboardDto getStudentDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int studentId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return dashboardService.getStudentDashboard(courseExecutionId, studentId);
    }

    @GetMapping("/students/dashboards/{dashboardId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#dashboardId, 'DASHBOARD.ACCESS')")
    public StatsDto getStats(@PathVariable int dashboardId) {
        return dashboardService.getStats(dashboardId);
    }

    @GetMapping("/teachers/dashboards/executions/{courseExecutionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseExecutionId, 'EXECUTION.ACCESS')")
    public TeacherDashboardDto getTeacherDashboard(Principal principal, @PathVariable int courseExecutionId) {
        int teacherId = ((AuthUser) ((Authentication) principal).getPrincipal()).getUser().getId();

        return dashboardService.getTeacherDashboard(courseExecutionId, teacherId);
    }

}