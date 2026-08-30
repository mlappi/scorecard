package fi.mlappi.golf.service;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class BestCourseScore {

    private String courseName;
    private int strokes;
    private int toPar;
    private List<String> competitors = new ArrayList<>();

    public String getCompetitorNames() {
        return String.join(", ", competitors);
    }
}
