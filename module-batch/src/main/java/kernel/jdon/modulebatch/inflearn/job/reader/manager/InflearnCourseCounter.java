package kernel.jdon.modulebatch.inflearn.job.reader.manager;

import java.util.ArrayList;
import java.util.List;

import kernel.jdon.moduledomain.inflearncourse.domain.InflearnCourse;
import lombok.Getter;

@Getter
public class InflearnCourseCounter {
    private int savedCourseCount = 0;
    private List<InflearnCourse> newCourses = new ArrayList<>();

    public void incrementSavedCourseCount() {
        this.savedCourseCount++;
    }

    public void addNewCourse(InflearnCourse course) {
        newCourses.add(course);
    }

    public void resetState() {
        savedCourseCount = 0;
        newCourses.clear();
    }
}
