package fi.mlappi.golf.model;

public enum PlayFormat {
    INDIVIDUAL(false),
    GREENSOME(true),
    SCRAMBLE(true),
    BEST_BALL(true),
    OTHER_TEAM(true);

    private final boolean teamFormat;

    PlayFormat(boolean teamFormat) {
        this.teamFormat = teamFormat;
    }

    public boolean isTeamFormat() {
        return teamFormat;
    }
}
