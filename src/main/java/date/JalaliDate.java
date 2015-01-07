package date;


public class JalaliDate {

    public int year;
    public int month;
    public int day;

    public JalaliDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public JalaliDate(JalaliDate that) {
        this(that.year, that.month, that.day);
    }

    public void set(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public void set(JalaliDate that) {
        set(that.year, that.month, that.day);
    }

    public void increaseMonth(int num) {
        if (num < 0) {
            decreaseMonth(-num);
        }
        if (num > 12) {
            year += num / 12;
            num %= 12;
        }
        month += num;
        if (month > 12) {
            year++;
            month -= 12;
        }

        checkMonthDay();
    }

    public void decreaseMonth(int num) {
        if (num < 0) {
            increaseMonth(-num);
        }
        if (num > 12) {
            year -= num / 12;
            num %= 12;
        }
        month -= num;
        if (month < 1) {
            year--;
            month += 12;
        }

        checkMonthDay();
    }

    public void checkMonthDay() {
        if (day < 29)
            return;
        int max = DateUtils.getMaxMonthDay(year, month);
        if (day > max) {
            day = max;
        }
    }
}