package kernel.jdon.modulebatch.inflearn.job.writer;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import kernel.jdon.modulebatch.inflearn.dto.InflearnCourseResponse;
import kernel.jdon.modulebatch.inflearn.job.reader.service.CourseStorageService;
import lombok.RequiredArgsConstructor;

@Component
@StepScope
@RequiredArgsConstructor
public class InflearnCourseItemWriter implements ItemWriter<InflearnCourseResponse> {
    private final CourseStorageService courseStorageService;

    @Override
    public void write(Chunk<? extends InflearnCourseResponse> responses) throws Exception {
        for (InflearnCourseResponse response : responses) {
            courseStorageService.createInflearnCourseAndInflearnJdSkill(
                response.getSkillKeyword(),
                response.getCourses()
            );
        }
    }
}
