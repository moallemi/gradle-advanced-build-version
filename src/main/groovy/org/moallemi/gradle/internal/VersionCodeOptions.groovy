package org.moallemi.gradle.internal

import date.DateUtils
import date.JalaliDate
import org.gradle.api.GradleException

import java.text.SimpleDateFormat

class VersionCodeOptions {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm", Locale.US);

    private VersionCodeType mVersionCodeType = VersionCodeType.AUTO_INCREAMENT
    private int mForceVersionCode = -1
    private String mDateFormat = "yyMMdd"
    private boolean mAutoIncrementReleaseOnly = true

    void forcedVersionCode(int code) {
        mForceVersionCode = code
    }

    void autoIncrementReleaseOnly(boolean b) {
        mAutoIncrementReleaseOnly = b
    }

    void versionCodeType(VersionCodeType type) {
        mVersionCodeType = type
    }

    int getVersionCode() {
        if (mForceVersionCode != -1) {
            return mForceVersionCode
        }
        switch (mVersionCodeType) {
            case VersionCodeType.DATE:
                return Integer.parseInt(formatter.format(new Date())) - 1400000000;

            case VersionCodeType.JALALI_DATE:
                if (mDateFormat.equals("")) {
                    throw new GradleException("you must specify date format e.g. yyMMdd")
                }

                def code = 1
                Date gregorianDate = new Date();
                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
                JalaliDate jalaliDate = DateUtils.gregorianToJalali(gregorianDate);

                int year = (jalaliDate.year - 1300) * 1000000 // only works until 1399 :D
                int month = jalaliDate.month * 10000
                int day = jalaliDate.day * 100
                int autoInc = 1

                //int hour = calendar.get(Calendar.HOUR_OF_DAY) * 100
                //int minutes = calendar.get(Calendar.MINUTE)

                code = year + month + day //+ hour + minutes

                return code

            case VersionCodeType.AUTO_INCREMENT:
                def versionPropsFile = new File('version.properties')
                if (versionPropsFile.canRead()) {
                    def Properties versionProps = new Properties()
                    versionProps.load(new FileInputStream(versionPropsFile))
                    if (versionProps['VERSION_CODE'] == null) {
                        versionProps['VERSION_CODE'] = "1"
                    }
                    def code = versionProps['VERSION_CODE'].toInteger()
                    if (mAutoIncrementReleaseOnly) {
                        code += 1
                    }
                    //TODO write signature on file
                    versionProps['VERSION_CODE'] = code.toString()
                    versionProps.store(versionPropsFile.newWriter(), null)
                    return code
                } else {
                    throw new GradleException("Could not read version.properties file in path \""
                            + versionPropsFile.getAbsolutePath() + "\"")
                }
                break;
        }
        return 1;
    }
}
