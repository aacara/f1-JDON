package kernel.jdon.modulebatch.inflearn.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import kernel.jdon.modulebatch.inflearn.dto.InflearnCourseResponse;
import kernel.jdon.modulebatch.inflearn.job.reader.InflearnCourseItemReader;
import kernel.jdon.modulebatch.inflearn.job.writer.InflearnCourseItemWriter;
import kernel.jdon.modulebatch.inflearn.listener.InflearnJobExecutionListener;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class InflearnCourseConfig {

    private final InflearnJobExecutionListener inflearnJobExecutionListener;
    private final InflearnCourseItemReader inflearnCourseItemReader;
    private final InflearnCourseItemWriter inflearnCourseItemWriter;

    @Bean
    public Job inflearnCrawlJob(JobRepository jobRepository, Step inflearnCourseStep) {
        return new JobBuilder("inflearnCrawlJob", jobRepository)
            .incrementer(new RunIdIncrementer())
            .listener(inflearnJobExecutionListener)
            .start(inflearnCourseStep)
            .build();
    }

    @Bean
    @JobScope
    public Step inflearnCourseStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("inflearnCourseStep", jobRepository)
            .<InflearnCourseResponse, InflearnCourseResponse>chunk(10, transactionManager)
            .reader(inflearnCourseItemReader)
            .writer(inflearnCourseItemWriter)
            .build();
    }
}