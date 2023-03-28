package pt.ulisboa.tecnico.socialsoftware.tutor.user.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.Student
import pt.ulisboa.tecnico.socialsoftware.tutor.utils.DateHandler

@DataJpaTest
class ResetDemoStudentTest extends SpockTest {

    def course
    def courseExecution

    def setup() {
        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)
    }

    def "reset all demo students"() {
        given:
        def birthDate = DateHandler.now().toString() + new Random().nextDouble();
        def user1 = new Student("Demo-Student-" + birthDate, "Demo-Student-" + birthDate,
                USER_1_EMAIL, false, AuthUser.Type.DEMO)
        userRepository.save(user1)
        def user1Username = user1.username
        def user2 = new Student(USER_2_NAME, USER_2_USERNAME, USER_2_EMAIL,
                false, AuthUser.Type.DEMO)
        userRepository.save(user2)

        when:
        demoService.resetDemoStudents()

        then:
        AuthUser authUser1 = authUserRepository.findAuthUserByUsername(user1Username).orElse(null)
        AuthUser authUser2 = authUserRepository.findAuthUserByUsername(USER_2_USERNAME).orElse(null)
        authUser1 == null
        authUser2.id == user2.authUser.id
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
