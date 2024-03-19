// package kernel.jdon.modulebatch.inflearn.job.processor;
//
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.item.ItemProcessor;
// import org.springframework.stereotype.Component;
//
// import kernel.jdon.moduledomain.inflearncourse.domain.InflearnCourse;
// import kernel.jdon.moduledomain.inflearnjdskill.domain.InflearnJdSkill;
//
// @Component
// @StepScope
// public class InflearnJdSkillItemProcessor implements ItemProcessor<InflearnCourse, InflearnJdSkill> {
//
//     @Override
//     public InflearnJdSkill process(InflearnCourse course) throws Exception {
//         // TODO: InflearnCourse 객체의 데이터를 분석하여 관련된 기술 스킬을 식별합니다.
//         // 예시로, 강의 제목이나 설명에서 특정 키워드를 찾아 기술 스킬을 결정하는 로직을 포함할 수 있습니다.
//
//         // 여기에서는 간단한 예시로, 강의 제목에 포함된 키워드를 바탕으로 기술 스킬을 설정합니다.
//         InflearnJdSkill jdSkill = new InflearnJdSkill();
//         jdSkill.setCourse(course); // 연관된 강의 설정
//
//         // 강의 제목을 분석하여 기술 스킬 설정
//         if (course.getTitle().contains("Java")) {
//             jdSkill.setSkillName("Java");
//         } else if (course.getTitle().contains("Spring")) {
//             jdSkill.setSkillName("Spring");
//         } else {
//             // 기타 조건에 따른 스킬 이름 설정
//             jdSkill.setSkillName("Unknown");
//         }
//
//         // 추가 필드 설정
//
//         return jdSkill;
//     }
// }
