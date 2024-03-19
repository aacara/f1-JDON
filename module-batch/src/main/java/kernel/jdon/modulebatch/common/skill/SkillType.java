package kernel.jdon.modulebatch.common.skill;

public interface SkillType {
    static String getOrderKeyword() {
        return "기타";
    }

    String getKeyword();
}
