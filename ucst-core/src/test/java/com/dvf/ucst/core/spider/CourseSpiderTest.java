package com.dvf.ucst.core.spider;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class CourseSpiderTest {

    @Test
    void draft() {
        try {
            final Elements elements = Jsoup.parse(
                    Paths.get("test/com/dvf/ucst/data/spider/cpen").toFile(), "UTF-8"
            ).getElementsByTag("tbody");
            System.out.println(elements.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}