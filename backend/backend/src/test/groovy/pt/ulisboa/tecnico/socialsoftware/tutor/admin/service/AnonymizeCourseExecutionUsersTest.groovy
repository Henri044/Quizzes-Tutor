package pt.ulisboa.tecnico.socialsoftware.tutor.admin.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student

import java.util.stream.Collectors

@DataJpaTest
class AnonymizeCourseExecutionUsersTest extends SpockTest {
    def user
    def authUserId

    def tecnicoCourse
    def tecnicoCourseExecution

    def answerItemsIds

    def setup() {
        createExternalCourseAndExecution()

        tecnicoCourse = new Course(COURSE_1_NAME, Course.Type.TECNICO)
        courseRepository.save(tecnicoCourse)
        tecnicoCourseExecution = new CourseExecution(tecnicoCourse, COURSE_2_ACRONYM, COURSE_2_ACADEMIC_TERM, Course.Type.TECNICO, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(tecnicoCourseExecution)

        user = new Student(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, false, AuthUser.Type.EXTERNAL)
        userRepository.save(user)
        tecnicoCourseExecution.addUser(user)
        user.addCourse(tecnicoCourseExecution)
        authUserId = user.getAuthUser().getId()

        answerItemsIds = questionAnswerItemRepository.findQuestionAnswerItemsByUsername(USER_1_USERNAME)
                .stream().map({ answerItem -> answerItem.getId() })
                .collect(Collectors.toList())
    }

    def "anonymize course execution users"() {
        when:
        adminService.anonymizeCourseExecutionUsers(tecnicoCourseExecution.id)

        then:
        user.authUser == null
        authUserRepository.findById(authUserId).orElse(null) == null
        user.name == "Student " + user.id
        user.getUsername() == "student-" + user.id
        answerItemsIds.equals(
                questionAnswerItemRepository.findQuestionAnswerItemsByUsername(user.getUsername())
                        .stream().map({ answerItem -> answerItem.getId() })
                        .collect(Collectors.toList()))
        def courseExecution = courseExecutionRepository.findById(tecnicoCourseExecution.id).get()
        courseExecution.status == CourseExecution.Status.HISTORIC
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
