package kernel.jdon.modulebatch.inflearn.scheduler;

import java.time.LocalDateTime;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InflearnScheduler {
    private final JobLauncher jobLauncher;
    private final Job inflearnCrawlJob;

    @Scheduled(fixedDelay = 1000)
    public void executeInflearnCrawlJob() {
        executeJob(inflearnCrawlJob);
    }

    private synchronized void executeJob(Job job) {
        try {
            jobLauncher.run(
                job,
                new JobParametersBuilder()
                    .addString("DATETIME", LocalDateTime.now().toString())
                    .toJobParameters());
        } catch (JobExecutionException je) {
            log.info("JobExecution : " + je);
        }
    }
}
