package kernel.jdon.moduleapi.domain.skill.infrastructure.wantedjd;

import java.util.List;

import org.springframework.stereotype.Component;

import kernel.jdon.moduleapi.domain.skill.core.SkillInfo;
import kernel.jdon.moduleapi.domain.skill.core.wantedjd.WantedJdSkillReader;
import kernel.jdon.moduleapi.domain.skill.infrastructure.SkillReaderInfoMapper;
import kernel.jdon.moduleapi.domain.skill.infrastructure.SkillRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WantedJdSkillReaderImpl implements WantedJdSkillReader {
    private final SkillRepository skillRepository;
    private final SkillReaderInfoMapper skillReaderInfoMapper;

    @Override
    public List<SkillInfo.FindJd> findWantedJdListBySkill(List<String> keywordList) {
        return skillRepository.findWantedJdListBySkill(keywordList).stream()
            .map(skillReaderInfoMapper::of)
            .toList();
    }
}
