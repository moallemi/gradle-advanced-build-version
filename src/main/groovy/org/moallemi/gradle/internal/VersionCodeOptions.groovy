package org.moallemi.gradle.internal

import date.DateUtils
import date.JalaliDate
import org.gradle.api.GradleException
import org.gradle.api.Project

import java.text.SimpleDateFormat

class VersionCodeOptions {

    private static SimpleDateFormat formatter = new SimpleDateFormat("yyMMddHHmm", Locale.US);
    private static final DEFAULT_DEPENDS_ON = Collections.singletonList('release')

    private VersionCodeType mVersionCodeType = VersionCodeType.AUTO_INCREMENT_ONE_STEP
    private int mForceVersionCode = -1
    private String mDateFormat = "yyMMdd"
    private boolean mAutoIncrementReleaseOnly = true
    List<Object> dependsOnTasks = DEFAULT_DEPENDS_ON
    private File versionPropsFile

    private Project project

    VersionCodeOptions(Project project) {
        this.project = project
    }

    void dependsOnTasks(Object... paths) {
        this.dependsOnTasks = Arrays.asList(paths)
    }

    void forcedVersionCode(int code) {
        mForceVersionCode = code
    }

    void autoIncrementReleaseOnly(boolean b) {
        mAutoIncrementReleaseOnly = b
    }

    void versionCodeType(VersionCodeType type) {
        mVersionCodeType = type
    }

    VersionCodeType getVersionCodeType() {
        return mVersionCodeType
    }

    File getVersionFile() {
        return versionPropsFile
    }

    int getVersionCode() {
        if (mForceVersionCode != -1) {
            return mForceVersionCode
        }
        switch (mVersionCodeType) {
            case VersionCodeType.DATE:
                if (mDateFormat.equals("")) {
                    throw new GradleException("you must specify date format e.g. yyMMdd")
                }


                Calendar calendar = Calendar.getInstance(Locale.ENGLISH);

                int year = (calendar.get(Calendar.YEAR) - 2000) * 100000000
                int month = (calendar.get(Calendar.MONTH) + 1) * 1000000
                int day = calendar.get(Calendar.DAY_OF_MONTH) * 10000
                int hour = calendar.get(Calendar.HOUR_OF_DAY) * 100
                int minutes = calendar.get(Calendar.MINUTE)

                int code = year + month + day + hour + minutes

                return code
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

            case VersionCodeType.AUTO_INCREMENT_ONE_STEP:
                versionPropsFile = new File(project.buildFile.getParent() + "/version.properties")
                if (versionPropsFile.canRead()) {
                    def Properties versionProps = new Properties()
                    versionProps.load(new FileInputStream(versionPropsFile))
                    if (versionProps['AI_VERSION_CODE'] == null) {
                        versionProps['AI_VERSION_CODE'] = "0"
                    }
                    int code = Integer.valueOf(versionProps['AI_VERSION_CODE'].toString()) + 1
                    return code
                } else {
                    throw new GradleException("Could not read version.properties file in path \""
                            + versionPropsFile.getAbsolutePath() + "\" \r\n" +
                            "Please create this file and add it to your VCS (git, svn, ...).")
                }
                break;

            case VersionCodeType.AUTO_INCREMENT_DATE:
                return Integer.parseInt(formatter.format(new Date())) - 1400000000;
        }
        return 1;
    }
}
