package kernel.jdon.modulebatch.inflearn.job.reader.manager;

import java.util.Random;

import kernel.jdon.modulebatch.inflearn.config.ScrapingInflearnProperties;
import lombok.Getter;

@Getter
public class DynamicSleepTimeManager {
    private final int minInitial;
    private final int maxInitial;
    private final int max;
    private final int increment;
    private final Random random = new Random();
    private int dynamicSleepTime;

    public DynamicSleepTimeManager(ScrapingInflearnProperties scrapingInflearnProperties) {
        this.minInitial = scrapingInflearnProperties.getMinInitialSleepTime();
        this.maxInitial = scrapingInflearnProperties.getMaxInitialSleepTime();
        this.max = scrapingInflearnProperties.getMaxSleepTime();
        this.increment = scrapingInflearnProperties.getSleepTimeIncrement();
        this.dynamicSleepTime = this.minInitial;
    }

    public void adjustSleepTime(boolean requestSuccess) {
        if (requestSuccess) {
            dynamicSleepTime = minInitial + random.nextInt(maxInitial - minInitial + 1);
        } else {
            dynamicSleepTime = Math.min(dynamicSleepTime + increment, max);
        }
    }
}
