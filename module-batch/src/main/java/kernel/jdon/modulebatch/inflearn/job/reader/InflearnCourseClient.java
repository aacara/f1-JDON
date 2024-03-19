package kernel.jdon.modulebatch.inflearn.job.reader;

import static kernel.jdon.modulecommon.util.StringUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import kernel.jdon.modulebatch.common.skill.BackendSkillType;
import kernel.jdon.modulebatch.common.skill.FrontendSkillType;
import kernel.jdon.modulebatch.inflearn.config.ScrapingInflearnProperties;
import kernel.jdon.modulebatch.inflearn.dto.InflearnCourseResponse;
import kernel.jdon.modulebatch.inflearn.job.reader.manager.DynamicSleepTimeManager;
import kernel.jdon.modulebatch.inflearn.job.reader.manager.InflearnCourseCounter;
import kernel.jdon.modulebatch.inflearn.job.reader.manager.LastPageDiscriminator;
import kernel.jdon.modulebatch.inflearn.job.reader.service.CourseParserService;
import kernel.jdon.modulebatch.inflearn.job.reader.service.CourseScraperService;
import kernel.jdon.modulebatch.inflearn.search.CourseSearchSort;
import kernel.jdon.moduledomain.inflearncourse.domain.InflearnCourse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class InflearnCourseClient {

    private final ScrapingInflearnProperties scrapingInflearnProperties;
    private final CourseScraperService courseScraperService;
    private final CourseParserService courseParserService;
    // TODO: writer로 옮기기

    public List<InflearnCourseResponse> getInflearnData() {
        List<InflearnCourseResponse> responses = new ArrayList<>();
        List<String> keywordList = new ArrayList<>();
        keywordList.addAll(FrontendSkillType.getAllKeywords());
        keywordList.addAll(BackendSkillType.getAllKeywords());
        // TODO: InflearnCourseResponse 생성해서 반환

        for (String keyword : keywordList) {
            InflearnCourseCounter inflearnCourseCounter = new InflearnCourseCounter();
            processKeyword(keyword, 1, inflearnCourseCounter);
            if (!inflearnCourseCounter.getNewCourses().isEmpty()) {
                responses.add(
                    new InflearnCourseResponse(keyword, new ArrayList<>(inflearnCourseCounter.getNewCourses())));
            }
        }

        return responses;
    }

    private void processKeyword(String skillKeyword, int pageNum, InflearnCourseCounter inflearnCourseCounter) {
        final int maxCoursesPerKeyword = scrapingInflearnProperties.getMaxCoursesPerKeyword();
        LastPageDiscriminator lastPageDiscriminator = new LastPageDiscriminator(scrapingInflearnProperties);
        DynamicSleepTimeManager sleepTimeManager = new DynamicSleepTimeManager(scrapingInflearnProperties);

        while (inflearnCourseCounter.getSavedCourseCount() < maxCoursesPerKeyword
            && !lastPageDiscriminator.isLastPage()) {
            try {
                Thread.sleep(sleepTimeManager.getDynamicSleepTime());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            String currentUrl = createInflearnSearchUrl(skillKeyword, pageNum);
            log.info("currentUrl: {}", currentUrl);

            boolean isSuccess = scrapeAndParsePage(currentUrl, inflearnCourseCounter, lastPageDiscriminator);
            sleepTimeManager.adjustSleepTime(isSuccess);

            if (isSuccess && inflearnCourseCounter.getSavedCourseCount() < maxCoursesPerKeyword) {
                pageNum++;
            }
        }
    }

    private boolean scrapeAndParsePage(String currentUrl, InflearnCourseCounter inflearnCourseCounter,
        LastPageDiscriminator lastPageDiscriminator) {
        try {
            Elements scrapeCourseElements = courseScraperService.scrapeCourses(currentUrl);
            int coursesCount = scrapeCourseElements.size();
            lastPageDiscriminator.checkIfLastPageBasedOnCourseCount(coursesCount);
            parseAndCreateCourses(scrapeCourseElements, currentUrl, inflearnCourseCounter);
            return true;
        } catch (Exception e) {
            log.error("페이지 처리 중 오류 발생: {}", currentUrl, e);
            return false;
        }
    }

    // private void saveCourseInfo(String skillKeyword, InflearnCourseCounter inflearnCourseCounter) {
    //     if (!inflearnCourseCounter.getNewCourses().isEmpty()) {
    //         courseStorageService.createInflearnCourseAndInflearnJdSkill(skillKeyword,
    //             inflearnCourseCounter.getNewCourses());
    //         inflearnCourseCounter.resetState();
    //     }
    // }

    private String createInflearnSearchUrl(String skillKeyword, int pageNum) {
        final String courseListUrl = scrapingInflearnProperties.getUrl();
        String path = joinToString(courseListUrl, "/");

        String queryString = joinToString(
            createQueryString("s", skillKeyword),
            createQueryString(CourseSearchSort.SEARCH_KEY, CourseSearchSort.SORT_POPULARITY.getSearchValue()),
            createQueryString("page", String.valueOf(pageNum))
        );

        return joinToString(path, "?", queryString);
    }

    private void parseAndCreateCourses(Elements courseElements, String skillKeyword,
        InflearnCourseCounter inflearnCourseCounter) {
        final int maxCoursesPerKeyword = scrapingInflearnProperties.getMaxCoursesPerKeyword();

        for (Element courseElement : courseElements) {
            if (inflearnCourseCounter.getSavedCourseCount() >= maxCoursesPerKeyword) {
                break;
            }

            InflearnCourse parsedCourse = courseParserService.parseCourse(courseElement, skillKeyword);

            if (parsedCourse != null) {
                inflearnCourseCounter.addNewCourse(parsedCourse);
                inflearnCourseCounter.incrementSavedCourseCount();
            }
        }
    }
}


