package kernel.jdon.modulebatch.domain.inflearncourse;

import java.util.Optional;

import kernel.jdon.moduledomain.inflearncourse.domain.InflearnCourse;
import kernel.jdon.moduledomain.inflearncourse.repository.InflearnCourseDomainRepository;

public interface InflearnCourseRepository extends InflearnCourseDomainRepository {

    Optional<InflearnCourse> findByCourseId(Long courseId);
}
