package gg.chill.stardewskills.model;

public enum Skill {
    FARMING("농사"),
    FISHING("낚시"),
    WOODCUTTING("벌목"),
    MINING("채광");

    private final String koreanName;
    Skill(String ko) { this.koreanName = ko; }
    public String getKoreanName() { return koreanName; }
}
