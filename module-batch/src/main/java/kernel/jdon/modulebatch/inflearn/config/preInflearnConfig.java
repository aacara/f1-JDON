// package kernel.jdon.modulebatch.inflearn.config;
//
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.configuration.annotation.JobScope;
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.core.job.builder.JobBuilder;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.core.repository.JobRepository;
// import org.springframework.batch.core.step.builder.StepBuilder;
// import org.springframework.batch.item.ItemReader;
// import org.springframework.batch.item.database.JpaItemWriter;
// import org.springframework.batch.item.database.JpaPagingItemReader;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.transaction.PlatformTransactionManager;
//
// import jakarta.persistence.EntityManagerFactory;
// import kernel.jdon.modulebatch.inflearn.job.processor.InflearnCourseItemProcessor;
// import kernel.jdon.modulebatch.inflearn.job.processor.InflearnJdSkillItemProcessor;
// import kernel.jdon.modulebatch.inflearn.job.reader.InflearnCourseItemReader;
// import kernel.jdon.modulebatch.inflearn.listener.InflearnJobExecutionListener;
// import kernel.jdon.moduledomain.inflearncourse.domain.InflearnCourse;
// import kernel.jdon.moduledomain.inflearnjdskill.domain.InflearnJdSkill;
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @RequiredArgsConstructor
// public class preInflearnConfig {
//
//     private final InflearnJobExecutionListener inflearnJobExecutionListener;
//     private final InflearnCourseItemReader inflearnCourseItemReader;
//     private final InflearnCourseItemProcessor inflearnCourseItemProcessor;
//     private final InflearnJdSkillItemProcessor inflearnJdSkillItemProcessor;
//     private final EntityManagerFactory entityManagerFactory;
//
//     @Bean
//     public Job inflearnCrawlJob(JobRepository jobRepository, Step inflearnCourseStep, Step inflearnJdSkillStep) {
//         return new JobBuilder("inflearnCrawlJob", jobRepository)
//             .incrementer(new RunIdIncrementer())
//             .listener(inflearnJobExecutionListener)
//             .start(inflearnCourseStep)
//             .next(inflearnJdSkillStep)
//             .build();
//     }
//
//     @Bean
//     @JobScope
//     public Step inflearnCourseStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//         return new StepBuilder("inflearnCourseStep", jobRepository)
//             .<InflearnCourse, InflearnCourse>chunk(10, transactionManager)
//             .reader(inflearnCourseItemReader)
//             .processor(inflearnCourseItemProcessor)
//             .writer(inflearnCourseItemWriter())
//             .build();
//     }
//
//     @Bean
//     @JobScope
//     public Step inflearnJdSkillStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
//         return new StepBuilder("inflearnJdSkillStep", jobRepository)
//             .<InflearnCourse, InflearnJdSkill>chunk(10, transactionManager)
//             .reader(inflearnJdSkillItemReader(entityManagerFactory))
//             .processor(inflearnJdSkillItemProcessor)
//             .writer(inflearnJdSkillItemWriter())
//             .build();
//     }
//
//     @Bean
//     @StepScope
//     public ItemReader<InflearnCourse> inflearnJdSkillItemReader(EntityManagerFactory entityManagerFactory) {
//         JpaPagingItemReader<InflearnCourse> reader = new JpaPagingItemReader<>();
//         reader.setQueryString("select c from InflearnCourse c");
//         reader.setEntityManagerFactory(entityManagerFactory);
//         reader.setPageSize(10);
//
//         return reader;
//     }
//
//     @Bean
//     @StepScope
//     public JpaItemWriter<InflearnCourse> inflearnCourseItemWriter() {
//         JpaItemWriter<InflearnCourse> writer = new JpaItemWriter<>();
//         writer.setEntityManagerFactory(entityManagerFactory);
//
//         return writer;
//     }
//
//     @Bean
//     @StepScope
//     public JpaItemWriter<InflearnJdSkill> inflearnJdSkillItemWriter() {
//         JpaItemWriter<InflearnJdSkill> writer = new JpaItemWriter<>();
//         writer.setEntityManagerFactory(entityManagerFactory);
//
//         return writer;
//     }
//
// }