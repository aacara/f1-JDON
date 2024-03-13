package kernel.jdon.moduleapi.domain.skill.core.keyword;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import kernel.jdon.moduleapi.domain.skill.infrastructure.SkillRepository;
import kernel.jdon.moduleapi.domain.skill.infrastructure.keyword.SkillKeywordRepository;
import kernel.jdon.moduledomain.skill.domain.Skill;
import kernel.jdon.moduledomain.skillkeyword.domain.SkillKeyword;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SkillKeywordCache {

    private static final String SKILL_KEYWORDS = "SkillKeywords";
    private final SkillRepository skillRepository;
    private final SkillKeywordRepository skillKeywordRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, Set<String>> hashOperations;

    @PostConstruct
    public void initializeCache() {
        this.hashOperations = redisTemplate.opsForHash();

        List<Skill> skillList = skillRepository.findAll();
        skillList.forEach(skill -> {
            Set<String> keywords = new HashSet<>();
            keywords.add(skill.getKeyword().toLowerCase());
            hashOperations.put(SKILL_KEYWORDS, skill.getKeyword().toLowerCase(), keywords);
        });

        List<SkillKeyword> skillKeywordList = skillKeywordRepository.findAll();

        skillKeywordList.forEach(skillKeyword -> {
            String relatedKeyword = skillKeyword.getRelatedKeyword().toLowerCase();
            String keyword = skillKeyword.getSkill().getKeyword().toLowerCase();
            Set<String> keywords = hashOperations.get(SKILL_KEYWORDS, relatedKeyword);

            if (keywords == null) {
                keywords = new HashSet<>();
            }
            keywords.add(keyword);
            hashOperations.put(SKILL_KEYWORDS, relatedKeyword, keywords);
        });
    }

    public Set<String> getAssociatedKeywords(String relatedKeyword) {
        Set<String> associatedKeywords = hashOperations.get(SKILL_KEYWORDS, relatedKeyword);

        if (associatedKeywords == null) {
            return new HashSet<>();
        }

        return associatedKeywords;
    }
}
