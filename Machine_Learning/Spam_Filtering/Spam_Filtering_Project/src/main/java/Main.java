import data.Data;

public class Main {

    public static void main(String[] args) {
        SpamFiltering filter = new SpamFiltering(Data.EMAILS_BG);
        filter.start();
    }
}
