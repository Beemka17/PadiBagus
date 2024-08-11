package app.ij.mlwithtensorflowlite;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.List;

public class CustomValueFormatter extends IndexAxisValueFormatter {
    private final List<String> values;

    public CustomValueFormatter(List<String> values) {
        this.values = values;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = Math.round(value);
        if (index >= 0 && index < values.size()) {
            return values.get(index);
        } else {
            return "";
        }
    }
}
