package decisionTree;

import java.util.ArrayList;
import java.util.List;

public class ID3Feature {
    private String word;
    private Integer index;
    private List<Integer> emails;

    ID3Feature(Integer index, String word) {
        this.index = index;
        this.word = word;
        this.emails = new ArrayList<>();
    }

    public Integer getIndex() {
        return index;
    }

    public String getWord() {
        return word;
    }

    public void addEmailIndex(Integer index) {
        if (this.emails.indexOf(index) == -1) {
            this.emails.add(index);
        }
    }

    public List<Integer> getEmailIndexes() {
        return this.emails;
    }
}
