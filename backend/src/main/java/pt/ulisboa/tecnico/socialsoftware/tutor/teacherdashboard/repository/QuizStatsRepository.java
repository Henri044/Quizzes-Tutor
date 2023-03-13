package pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.teacherdashboard.domain.QuizStats;

@Repository
@Transactional
public interface QuizStatsRepository extends JpaRepository<QuizStats, Integer> {
}
