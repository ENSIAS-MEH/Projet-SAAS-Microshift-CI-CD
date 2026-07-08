package com.jeemobile.application.dto;

import java.util.List;

public class PieChartData {

    private List<String> labels;
    private List<Long> values;

    public PieChartData(List<String> labels, List<Long> values) {
        this.labels = labels;
        this.values = values;
    }

    public List<String> getLabels() { return labels; }
    public void setLabels(List<String> labels) { this.labels = labels; }

    public List<Long> getValues() { return values; }
    public void setValues(List<Long> values) { this.values = values; }
}
