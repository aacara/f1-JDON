package kernel.jdon.moduleapi.domain.skill.core;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kernel.jdon.moduleapi.domain.jobcategory.core.JobCategoryReader;
import kernel.jdon.moduleapi.domain.skill.core.inflearnjd.InflearnJdSkillReader;
import kernel.jdon.moduleapi.domain.skill.core.keyword.SkillKeywordReader;
import kernel.jdon.moduleapi.domain.skill.core.wantedjd.WantedJdSkillReader;
import kernel.jdon.moduledomain.jobcategory.domain.JobCategory;
import kernel.jdon.moduledomain.skill.domain.Skill;
import kernel.jdon.moduledomain.skill.domain.SkillType;
import kernel.jdon.moduledomain.skillkeyword.domain.SkillKeyword;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {
    private final SkillReader skillReader;
    private final WantedJdSkillReader wantedJdSkillReader;
    private final InflearnJdSkillReader inflearnJdSkillReader;
    private final JobCategoryReader jobCategoryReader;
    private final SkillKeywordReader skillKeywordReader;

    @Override
    public SkillInfo.FindHotSkillListResponse getHotSkillList() {
        final List<SkillInfo.FindHotSkill> hotSkillList = skillReader.findHotSkillList();
        return new SkillInfo.FindHotSkillListResponse(hotSkillList);
    }

    @Override
    public SkillInfo.FindMemberSkillListResponse getMemberSkillList(final Long memberId) {
        final List<SkillInfo.FindMemberSkill> memberSkillList = skillReader.findMemberSkillList(memberId);
        return new SkillInfo.FindMemberSkillListResponse(memberSkillList);
    }

    @Override
    public SkillInfo.FindJobCategorySkillListResponse getJobCategorySkillList(final Long jobCategoryId) {
        final JobCategory findJobCategory = jobCategoryReader.findById(jobCategoryId);
        final List<SkillInfo.FindJobCategorySkill> jobCategorySkillList = findJobCategory.getSkillList().stream()
            .filter(skill -> !skill.getKeyword().equals(SkillType.getOrderKeyword()))
            .map(skill -> new SkillInfo.FindJobCategorySkill(skill.getId(), skill.getKeyword()))
            .toList();

        return new SkillInfo.FindJobCategorySkillListResponse(jobCategorySkillList);
    }

    // @Override
    // public SkillInfo.FindDataListBySkillResponse getDataListBySkill(String relatedkeyword, final Long memberId) {
    //     final String searchKeyword = getKeyword(relatedkeyword);
    //
    //     final List<SkillInfo.FindJd> findJdList = wantedJdSkillReader.findWantedJdListBySkill(
    //         searchKeyword);
    //     final List<SkillInfo.FindLecture> findLectureList = inflearnJdSkillReader.findInflearnLectureListBySkill(
    //         searchKeyword, memberId);
    //
    //     return new SkillInfo.FindDataListBySkillResponse(searchKeyword, findLectureList, findJdList);
    // }

    @Override
    public SkillInfo.FindDataListBySkillResponse getDataListBySkill(String relatedKeyword, final Long memberId) {
        List<String> searchKeywords = skillKeywordReader.findSkillKeywordByRelatedKeywordIgnoreCase(relatedKeyword)
            .stream()
            .map(SkillKeyword::getSkill)
            .map(Skill::getKeyword)
            .toList();

        if (searchKeywords.isEmpty()) {
            searchKeywords.add(getHotSkillKeyword());
        }

        final List<SkillInfo.FindJd> findJdList = searchKeywords.stream()
            .flatMap(keyword -> wantedJdSkillReader.findWantedJdListBySkill(keyword).stream())
            .toList();
        final List<SkillInfo.FindLecture> findLectureList = searchKeywords.stream()
            .flatMap(keyword -> inflearnJdSkillReader.findInflearnLectureListBySkill(keyword, memberId).stream())
            .toList();

        return new SkillInfo.FindDataListBySkillResponse(searchKeywords, findLectureList, findJdList);
    }

    // private String getKeyword(final String searchKeyword) {
    //     return Optional.ofNullable(searchKeyword)
    //         .filter(StringUtils::hasText)
    //         .map(skillKeywordReader::findSkillKeywordByRelatedKeywordIgnoreCase)
    //         .map(SkillKeyword::getSkill)
    //         .map(Skill::getKeyword)
    //         .orElseGet(this::getHotSkillKeyword);
    // }

    private String getHotSkillKeyword() {
        return skillReader.findHotSkillList().get(0).getKeyword();
    }
}
