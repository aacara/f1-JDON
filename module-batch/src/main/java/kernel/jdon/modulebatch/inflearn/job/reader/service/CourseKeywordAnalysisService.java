package kernel.jdon.modulebatch.inflearn.job.reader.service;

import org.springframework.stereotype.Service;

import kernel.jdon.modulebatch.inflearn.job.reader.converter.KeywordTranslator;

@Service
public class CourseKeywordAnalysisService {

    public boolean isKeywordPresentInTitleAndDescription(String title, String description, String keyword) {
        String keywordInKorean = KeywordTranslator.translateToKorean(keyword);

        return (title.toLowerCase().contains(keyword.toLowerCase()) || title.contains(keywordInKorean)) &&
            (description.toLowerCase().contains(keyword.toLowerCase()) || description.contains(keywordInKorean));
    }
}
