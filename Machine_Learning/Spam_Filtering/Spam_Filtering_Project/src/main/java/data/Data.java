package data;

public class Data {

    public static String EMAILS_BG = "../DataSet/SMSSpamCollectionBG.txt";
    public static String EMAILS_EN = "../DataSet/SMSSpamCollection";

    public static final String SPAM_CLASS = "spam";
    public static final String NON_SPAM_CLASS = "ham";
    public static final int INDEX_OF_CLASS = 0;

    public static final String[] CLASSES = new String[]{ "ham", "spam" };
    public static final String HYPERLINKS_REGEX = "(http|www\\.)[^\\s]+";
    public static final String DELIMITER = " ";
    public static final String NUMBERS_REGEX = "[0-9]";
}
