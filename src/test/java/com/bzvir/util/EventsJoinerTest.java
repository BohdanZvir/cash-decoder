package com.bzvir.util;

import com.bzvir.model.Event;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Created by bohdan.
 */
public class EventsJoinerTest {

    @Test
    public void joinOneCashAndOnePrivat24Event() {
        Event cash = new Event();
        cash.setProperty("Категорія", "@string/leisure_activities");
        cash.setProperty("Дата", "2014-09.05");
        cash.setProperty("Час", "");
        cash.setProperty("Сума у валюті картки", 26.0);
        cash.setProperty("Опис операції", "ратуша");
        cash.setProperty("Валюта картки", "default");
        cash.setProperty("accountId", "6ca510bd-28ca-45a1-93ab-e45797d2832e");

        Event privat24 = new Event();
        privat24.setProperty("Категорія", "Перекази");
        privat24.setProperty("Дата", "01.07.2016");
        privat24.setProperty("Час", "19:39");
        privat24.setProperty("Сума у валюті картки", 300.0);
        privat24.setProperty("Опис операції", "Переказ зі своєї карти 51**82 через додаток Приват24");
        privat24.setProperty("Валюта картки","грн");

        EventJoiner eventJoiner = new EventJoiner();
        Set<Event> list = eventJoiner.join(Arrays.asList(cash), Arrays.asList(privat24));

        assertThat(list, hasItem(cash));
        assertThat(list, hasItem(privat24));
    }
}
