package uk.co.spotistats.spotistatsservice.Domain.Request.Search;

public enum DayOfTheWeek {
    SUNDAY(0),
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6);

    private final Integer dbRepresentation;

    DayOfTheWeek(Integer dbRepresentation) {
        this.dbRepresentation = dbRepresentation;
    }

    public Integer getDbRepresentation() {
        return dbRepresentation;
    }
}
