package com.dvf.ucst.core.spider;

import com.dvf.ucst.core.courseutils.Course;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip;
import com.dvf.ucst.core.spider.CourseWip.CourseSectionWip.CourseLectureSectionWip;
import org.jsoup.nodes.Element;

import java.io.IOException;

public class CourseSectionSpider {

    public static CourseSectionWip fetchCourseSectionFromWeb(final String courseSectionUrl,
                                                             final String sectionIdToken) throws IOException {
        final Element sectionDom = Spider.fetchDocument(courseSectionUrl);
        if (Course.isSectionIdTokenForLectureSection(sectionIdToken)) {
            return makeLectureSectionWip(sectionDom);
        } else {
            return makeNonLectureSectionWip(sectionDom, new CourseSectionWip());
        }
    }

    // TODO [wip:make][CourseSectionWip]:
    private static <T extends CourseSectionWip> T makeNonLectureSectionWip(final Element sectionDom, final T host) {
        // work goes here.

        return host;
    }

    // TODO [wip:make][CourseLectureSectionWip]:
    private static CourseLectureSectionWip makeLectureSectionWip(final Element sectionDom) {
        final CourseLectureSectionWip host = makeNonLectureSectionWip(sectionDom, new CourseLectureSectionWip());

        // things that lectures have but labs and tutorials don't have (like required lab/tutorial options):

        return host;
    }

}
