package date;

import java.util.Date;

public class DateUtils {

    private static int[] gDaysInMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
    private static int[] jDaysInMonth = {31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29};

    public final static char PERSIAN_ZERO = 0x06f0;
    public final static char PERSIAN_NINE = 0x06f9;
    public final static char PERSIAN_DECIMAL_POINT = 0x066b;
    public final static char DIGIT_DIFF = PERSIAN_ZERO - '0';

    private static DateUtils instance;

    private DateUtils() {

    }

    public static DateUtils get() {
        if (instance == null)
            instance = new DateUtils();
        return instance;
    }

    public static String format(String format, long time) {

        JalaliDate jDate = DateUtils.gregorianToJalali(new Date(time));

        final char QUOTE = '%';

        int len = format.length();
        int count = 0;

        for (int i = 0; i < len; i += count) {
            count = 1;
            char ch = format.charAt(i);

            if (ch != QUOTE) {
                continue;
            }

            int hookStart = i;
            int hookEnd = hookStart + 1;
            while (!"aAbBcCdDeFgGhHIjklmMnpPrRsStTuUVwWxXyYzZ%".contains(format.subSequence(hookEnd, hookEnd + 1))) {
                hookEnd++;
            }

            String replacement = DateUtils.replaceHook(format.substring(hookStart + 1, hookEnd + 1), jDate);
            format = format.replace(format.subSequence(hookStart, hookEnd + 1), replacement);
            len = format.length();
            count = replacement.length();
        }
        return format;
    }

    private static String replaceHook(String hook, JalaliDate jDate) {
        boolean padNumbers = true;
        int padLength = 0;
        String padChar = " ";

        char ch;
        if (hook.contains("_")) {
            padChar = " ";
        } else if (hook.contains("0")) {
            padChar = "0";
        }

        if (hook.contains("-")) {
            padNumbers = false;
        }

        for (int i = hook.length() - 1; i > 0; i--) {
            ch = hook.charAt(i);
            if ((ch >= '1') && (ch <= '9')) {
                padLength = ch - '0';
                break;
            }
        }

        ch = hook.charAt(hook.length() - 1);
        String result;
        switch (ch) {
            case 'C':
                if (padLength == 0)
                    padLength = 2;
                result = DateUtils.padNumber(jDate.year / 100, padLength, padNumbers, padChar);
                break;
            case 'd':
                if (padLength == 0)
                    padLength = 2;
                result = DateUtils.padNumber(jDate.day, padLength, padNumbers, padChar);
                break;
            case 'e':
                if (padLength == 0)
                    padLength = 2;
                result = DateUtils.padNumber(jDate.day, padLength, padNumbers, "0");
                break;
            case 'm':
                if (padLength == 0)
                    padLength = 2;
                result = DateUtils.padNumber(jDate.month, padLength, padNumbers, "0");
                break;
            case 'y':
                if (padLength == 0)
                    padLength = 2;
                result = DateUtils.padNumber(jDate.year % 100, padLength, padNumbers, padChar);
                break;
            case 'Y':
                if (padLength == 0)
                    padLength = 4;
                result = DateUtils.padNumber(jDate.year, padLength, padNumbers, padChar);
                break;
            case '%':
                result = "%";
                break;
            default:
                result = "%" + hook;
        }

        return result;
    }

    private static String padNumber(int number, int length, boolean pad, String ch) {
        String str = "" + number;
        int resultLength = str.length();
        int i;

        if (pad && (resultLength < length)) {
            String padding = "";
            for (i = 0; i < (resultLength - length); i++)
                padding += ch;
            str = padding + str;
        }

        return str;
    }


    public static JalaliDate gregorianToJalali(Date gDate) {
        return gregorianToJalali(gDate.getYear() + 1900, gDate.getMonth() + 1, gDate.getDate());
    }

    public static JalaliDate gregorianToJalali(int gYear, int gMonth, int gDay) {
        int gy, gm, gd;
        int jy, jm, jd;
        long g_day_no, j_day_no;
        int j_np;
        int i;

        gy = gYear - 1600;
        gm = gMonth - 1;
        gd = gDay - 1;

        g_day_no = 365 * gy + (gy + 3) / 4 - (gy + 99) / 100 + (gy + 399) / 400;
        for (i = 0; i < gm; ++i)
            g_day_no += gDaysInMonth[i];
        if (gm > 1 && ((gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)))
            /* leap and after Feb */
            ++g_day_no;
        g_day_no += gd;

        j_day_no = g_day_no - 79;

        j_np = new Long(j_day_no / 12053).intValue();
        j_day_no %= 12053;

        jy = new Long(979 + 33 * j_np + 4 * (j_day_no / 1461)).intValue();
        j_day_no %= 1461;

        if (j_day_no >= 366) {
            jy += (j_day_no - 1) / 365;
            j_day_no = (j_day_no - 1) % 365;
        }

        for (i = 0; i < 11 && j_day_no >= jDaysInMonth[i]; ++i) {
            j_day_no -= jDaysInMonth[i];
        }
        jm = i + 1;
        jd = new Long(j_day_no + 1).intValue();
        return new JalaliDate(jy, jm, jd);
    }


    public static boolean isJalaliLeapYear(int year) {
        int mod = (year + 11) % 33;
        if ((mod != 32) && ((mod % 4) == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public static int getMaxMonthDay(int year, int month) {
        if (month < 7) {
            return 31; // months 1..6
        }
        if (month < 12) {
            return 30; // months 7..11
        }
        if (isJalaliLeapYear(year)) {
            return 30; // month 12, but leap year
        }
        return 29; // month 12 and not a leap year
    }

}
