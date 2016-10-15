package com.bzvir.reader;

import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

/**
 * Created by root.
 */
public class Privat24ResourceUtilTest {

    @Test
    public void readP24propertiesFromFile() {
        Privat24ResourceUtil p24ResourceUtil = new Privat24ResourceUtil("application");
        List<String> titles = p24ResourceUtil.readProperties();

        assertThat(titles, everyItem(not(isEmptyOrNullString())));
        assertThat(titles, allOf(
                hasItem("Дата"),
                hasItem("Категорія"),
                hasItem("Сума у валюті картки")));
    }
}
