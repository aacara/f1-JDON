package kernel.jdon.crawler.inflearn.service;

import static kernel.jdon.util.StringUtil.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kernel.jdon.crawler.config.UrlConfig;
import kernel.jdon.crawler.inflearn.search.CourseSearchSort;
import kernel.jdon.crawler.inflearn.util.InflearnCrawlerState;
import kernel.jdon.crawler.wanted.skill.BackendSkillType;
import kernel.jdon.crawler.wanted.skill.FrontendSkillType;
import kernel.jdon.inflearncourse.domain.InflearnCourse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class InflearnCrawlerService implements CrawlerService {

	private final UrlConfig urlConfig;
	private final CourseScraperService courseScraperService;
	private final CourseParserService courseParserService;
	private final CourseStorageService courseStorageService;
	private static final int MAX_COURSES_PER_KEYWORD = 3;
	private static final int MIN_INITIAL_SLEEP_TIME = 1000;
	private static final int MAX_INITIAL_SLEEP_TIME = 3000;
	private static final int MAX_SLEEP_TIME = 10000;
	private static final int INCREMENT_SLEEP_TIME = 1000;
	private int dynamicSleepTime = MIN_INITIAL_SLEEP_TIME;
	private final Random random = new Random();

	@Transactional
	@Override
	public void createCourseInfo(int pageNum) {
		List<String> keywordList = new ArrayList<>();
		keywordList.addAll(FrontendSkillType.getAllKeywords());
		keywordList.addAll(BackendSkillType.getAllKeywords());

		for (String keyword : keywordList) {
			processKeyword(keyword, pageNum);
		}
	}

	private void processKeyword(String skillKeyword, int pageNum) {
		log.info("처리중인 키워드: {}, 페이지: {}", skillKeyword, pageNum);
		InflearnCrawlerState state = new InflearnCrawlerState();

		while (state.getSavedCourseCount() < MAX_COURSES_PER_KEYWORD && !state.isLastPage()) {
			try {
				Thread.sleep(dynamicSleepTime);
				log.debug("쓰레드 sleep {} ms", dynamicSleepTime);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("쓰레드가 중지", e);
				break;
			}
			String currentUrl = createInflearnSearchUrl(skillKeyword, CourseSearchSort.SORT_POPULARITY, pageNum);
			log.info("currentUrl: {}", currentUrl);

			boolean isSuccess = scrapeAndParsePage(currentUrl, skillKeyword, pageNum, state);
			adjustDynamicSleepTime(isSuccess);

			if (!isSuccess) {
				log.warn("페이지 스크래핑 및 파싱 실패: {}", currentUrl);
			}

			if (isSuccess && state.getSavedCourseCount() < MAX_COURSES_PER_KEYWORD) {
				pageNum++;
			}
		}
		saveCourseInfo(skillKeyword, state);
	}

	private boolean scrapeAndParsePage(String currentUrl, String skillKeyword, int pageNum,
		InflearnCrawlerState state) {
		try {
			Elements scrapeCourseElements = courseScraperService.scrapeCourses(currentUrl);
			int coursesCount = scrapeCourseElements.size();
			state.checkIfLastPageBasedOnCourseCount(coursesCount);
			parseAndCreateCourses(scrapeCourseElements, currentUrl, skillKeyword, pageNum, state);
			return true;
		} catch (Exception e) {
			log.error("페이지 처리 중 오류 발생: {}", currentUrl, e);
			return false;
		}
	}

	private void adjustDynamicSleepTime(boolean requestSuccess) {
		if (requestSuccess) {
			dynamicSleepTime =
				MIN_INITIAL_SLEEP_TIME + random.nextInt(MAX_INITIAL_SLEEP_TIME - MIN_INITIAL_SLEEP_TIME + 1);
		} else {
			dynamicSleepTime = Math.min(dynamicSleepTime + INCREMENT_SLEEP_TIME, MAX_SLEEP_TIME);
		}
	}

	private void saveCourseInfo(String skillKeyword, InflearnCrawlerState state) {
		if (!state.getNewCourses().isEmpty()) {
			courseStorageService.createInflearnCourseAndInflearnJdSkill(skillKeyword, state.getNewCourses());
			state.resetState();
		}
	}

	private String createInflearnSearchUrl(String skillKeyword, CourseSearchSort searchSort, int pageNum) {
		String path = joinToString(urlConfig.getInflearnCourseListUrl(), "/");

		String queryString = joinToString(
			createQueryString("s", skillKeyword),
			createQueryString(CourseSearchSort.SEARCH_KEY, searchSort.getSearchValue()),
			createQueryString("page", String.valueOf(pageNum))
		);

		return joinToString(path, "?", queryString);
	}

	private void parseAndCreateCourses(Elements courseElements, String lectureUrl, String skillKeyword, int pageNum,
		InflearnCrawlerState state) {
		for (Element courseElement : courseElements) {
			if (state.getSavedCourseCount() >= MAX_COURSES_PER_KEYWORD) {
				break;
			}

			InflearnCourse parsedCourse = courseParserService.parseCourse(courseElement, lectureUrl, skillKeyword);

			if (parsedCourse != null) {
				state.addNewCourse(parsedCourse);
				state.incrementSavedCourseCount();
				log.info("{} 키워드에 대해 저장된 강의 수: {}", skillKeyword, state.getSavedCourseCount());
			}
		}
	}
}
