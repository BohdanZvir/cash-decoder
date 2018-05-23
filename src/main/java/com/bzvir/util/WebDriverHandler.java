package com.bzvir.util;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Set;

public interface WebDriverHandler {

    void startBrowser();

    boolean isUp();

    void loadPage(String url, int timeout);

    void loadPage(String url, String xpath);

    void loadPageWait60(String url, String elementXpath);

    void scrollToElement(WebElement element);

    void scrollClickElement(String xpath);

    void scrollClickElement(String xpath, Object... xpathArgs);

    void scrollClickElementWithPresence(int timeOut, String xpath);

    void typeIntoElement(WebElement element, String text);

    void typeIntoElementScrollTo(WebElement element, String text);

    void clickElement(WebElement element);

    void waitAjax();

    void scrollClickElement(WebElement element);

    void clickElement(String xpath);

    WebElement findElementWithClickable(int timeOut, By locator);

    List<WebElement> findElementsWithClickable(int timeOut, By locator);

    void clickElement(String xpath, Object... xpathArgs);

    void clickElementWithPresence(int timeOut, String xpath);

    void clickElementWithClickable(int timeOut, String xpath);

    WebElement expectElementPresence(int timeOut, String xpath);

    void closeDriver();

    void handleAlert();

    void waitForPageLoaded(int timeOut);

    void switchToFrame(String xpath);

    void switchToParentFrame();

    void scrollTop();

    WebElement findElement(String xpath);

    WebElement findElement(String xpath, Object... args);

    WebElement findElement(WebElement element, String xpath);

    WebElement findElement2bPresent(int timeOut, String xpath, Object... args);

    WebElement findElementById2bPresent(int timeOut, String id);

    List<WebElement> findElements(String xpath);

    List<WebElement> findElements2bVisible(int timeOut, String xpath);

    WebElement findElement2bClickable(String xpath, int timeOut);

    void loadPage(String url);

    boolean findElements2bNotVisible(int timeOut, String xpath);

    void waitElement2bNotVisible(String xpath, int... timeOut);

    List<WebElement> findElements2bPresent(int timeOut, String xpath);

    void saveScreenshot();

    String getSessionId();

    Cookie getCookie(String name);

    Set<Cookie> getCookies();

    void addCookie(Cookie singleSignOnCookie);

    void refreshPage();

}
