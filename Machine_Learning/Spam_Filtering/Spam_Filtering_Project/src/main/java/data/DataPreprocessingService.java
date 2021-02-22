package data;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.bg.BulgarianAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataPreprocessingService {

    private Analyzer analyzer;

    public DataPreprocessingService() {
//        this.analyzer = new EnglishAnalyzer();
        this.analyzer = new BulgarianAnalyzer();
    }

    public List<String> analyze(String text) throws IOException {
        List<String> result = new ArrayList<>();
        TokenStream tokenStream = this.analyzer.tokenStream("TOKEN", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        tokenStream.close();
        return result;
    }

    public void close() {
        this.analyzer.close();
    }

}
