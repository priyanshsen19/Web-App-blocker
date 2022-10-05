package xyz.codewithcoffee.cysapp;
import xyz.codewithcoffee.cyc_app.R;
public class Question {
    
    private String question;
    private int no;
    private String option_a;
    private String option_b;
    private String option_c;
    private String option_d;
    private int lastCheck;
    private int correct;

    public Question(){}

    public Question(String question, int no, String option_a, String option_b, String option_c, String option_d,int correct) {
        this.question = question;
        this.no = no;
        this.option_a = option_a;
        this.option_b = option_b;
        this.option_c = option_c;
        this.option_d = option_d;
        this.correct = correct;
        this.lastCheck = R.id.clear;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }

    public String getOption_a() {
        return option_a;
    }

    public void setOption_a(String option_a) {
        this.option_a = option_a;
    }

    public String getOption_b() {
        return option_b;
    }

    public void setOption_b(String option_b) {
        this.option_b = option_b;
    }

    public String getOption_c() {
        return option_c;
    }

    public void setOption_c(String option_c) {
        this.option_c = option_c;
    }

    public String getOption_d() {
        return option_d;
    }

    public void setOption_d(String option_d) {
        this.option_d = option_d;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getLastCheck() { return lastCheck; }

    public void setLastCheck(int lastCheck) { this.lastCheck = lastCheck; }
}
