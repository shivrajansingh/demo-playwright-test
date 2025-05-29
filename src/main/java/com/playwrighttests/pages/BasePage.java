package com.playwrighttests.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.playwrighttests.utils.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Base Page class providing common page object functionality
 */
public abstract class BasePage {
    protected static final Logger log = LoggerFactory.getLogger(BasePage.class);
    protected final Page page;
    protected final ConfigManager config;

    public BasePage(Page page) {
        this.page = page;
        this.config = ConfigManager.getInstance();
    }

    // Navigation methods
    protected void navigateTo(String url) {
        log.info("Navigating to: {}", url);
        page.navigate(url);
        waitForPageLoad();
    }

    protected void reload() {
        log.info("Reloading page");
        page.reload();
        waitForPageLoad();
    }

    protected void goBack() {
        log.info("Going back to previous page");
        page.goBack();
        waitForPageLoad();
    }

    protected void goForward() {
        log.info("Going forward to next page");
        page.goForward();
        waitForPageLoad();
    }

    // Wait methods
    protected void waitForPageLoad() {
        page.waitForLoadState();
    }

    protected void waitForNetworkIdle() {
        page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE);
    }

    protected void waitForElementVisible(String selector) {
        log.debug("Waiting for element to be visible: {}", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.VISIBLE));
    }

    protected void waitForElementHidden(String selector) {
        log.debug("Waiting for element to be hidden: {}", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.HIDDEN));
    }

    protected void waitForElementAttached(String selector) {
        log.debug("Waiting for element to be attached: {}", selector);
        page.waitForSelector(selector, new Page.WaitForSelectorOptions().setState(WaitForSelectorState.ATTACHED));
    }

    protected void waitForTimeout(int milliseconds) {
        log.debug("Waiting for {} milliseconds", milliseconds);
        page.waitForTimeout(milliseconds);
    }

    // Element interaction methods
    protected void click(String selector) {
        log.debug("Clicking element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).click();
    }

    protected void doubleClick(String selector) {
        log.debug("Double clicking element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).dblclick();
    }

    protected void rightClick(String selector) {
        log.debug("Right clicking element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).click(new Locator.ClickOptions().setButton(com.microsoft.playwright.options.MouseButton.RIGHT));
    }

    protected void fill(String selector, String text) {
        log.debug("Filling element '{}' with text: {}", selector, text);
        waitForElementVisible(selector);
        page.locator(selector).fill(text);
    }

    protected void type(String selector, String text) {
        log.debug("Typing into element '{}': {}", selector, text);
        waitForElementVisible(selector);
        page.locator(selector).pressSequentially(text);
    }

    protected void clear(String selector) {
        log.debug("Clearing element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).clear();
    }

    protected void selectOption(String selector, String value) {
        log.debug("Selecting option '{}' in element: {}", value, selector);
        waitForElementVisible(selector);
        page.locator(selector).selectOption(value);
    }

    protected void check(String selector) {
        log.debug("Checking checkbox/radio: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).check();
    }

    protected void uncheck(String selector) {
        log.debug("Unchecking checkbox: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).uncheck();
    }

    protected void hover(String selector) {
        log.debug("Hovering over element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).hover();
    }

    protected void focus(String selector) {
        log.debug("Focusing on element: {}", selector);
        waitForElementVisible(selector);
        page.locator(selector).focus();
    }

    protected void scrollIntoView(String selector) {
        log.debug("Scrolling element into view: {}", selector);
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    // Element state methods
    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected boolean isHidden(String selector) {
        return page.locator(selector).isHidden();
    }

    protected boolean isEnabled(String selector) {
        return page.locator(selector).isEnabled();
    }

    protected boolean isDisabled(String selector) {
        return page.locator(selector).isDisabled();
    }

    protected boolean isChecked(String selector) {
        return page.locator(selector).isChecked();
    }

    protected boolean isElementPresent(String selector) {
        return page.locator(selector).count() > 0;
    }

    // Text and attribute methods
    protected String getText(String selector) {
        log.debug("Getting text from element: {}", selector);
        waitForElementVisible(selector);
        return page.locator(selector).textContent();
    }

    protected String getValue(String selector) {
        log.debug("Getting value from element: {}", selector);
        waitForElementVisible(selector);
        return page.locator(selector).inputValue();
    }

    protected String getAttribute(String selector, String attributeName) {
        log.debug("Getting attribute '{}' from element: {}", attributeName, selector);
        waitForElementVisible(selector);
        return page.locator(selector).getAttribute(attributeName);
    }

    protected List<String> getAllText(String selector) {
        log.debug("Getting all text from elements: {}", selector);
        return page.locator(selector).allTextContents();
    }

    protected int getElementCount(String selector) {
        return page.locator(selector).count();
    }

    // Page information methods
    protected String getCurrentUrl() {
        return page.url();
    }

    protected String getPageTitle() {
        return page.title();
    }

    // JavaScript execution
    protected Object executeScript(String script) {
        log.debug("Executing JavaScript: {}", script);
        return page.evaluate(script);
    }

    protected Object executeScript(String script, Object arg) {
        log.debug("Executing JavaScript with argument: {}", script);
        return page.evaluate(script, arg);
    }

    // Alert handling
    protected void acceptAlert() {
        page.onDialog(dialog -> {
            log.info("Accepting dialog: {}", dialog.message());
            dialog.accept();
        });
    }

    protected void dismissAlert() {
        page.onDialog(dialog -> {
            log.info("Dismissing dialog: {}", dialog.message());
            dialog.dismiss();
        });
    }

    protected void acceptAlertWithText(String text) {
        page.onDialog(dialog -> {
            log.info("Accepting dialog with text '{}': {}", text, dialog.message());
            dialog.accept(text);
        });
    }

    // Screenshot methods
    protected byte[] takeScreenshot() {
        return page.screenshot();
    }

    protected byte[] takeElementScreenshot(String selector) {
        return page.locator(selector).screenshot();
    }

    // Validation methods
    protected void validatePageTitle(String expectedTitle) {
        String actualTitle = getPageTitle();
        if (!actualTitle.equals(expectedTitle)) {
            throw new AssertionError(String.format("Page title mismatch. Expected: '%s', Actual: '%s'", 
                expectedTitle, actualTitle));
        }
    }

    protected void validateCurrentUrl(String expectedUrl) {
        String actualUrl = getCurrentUrl();
        if (!actualUrl.equals(expectedUrl)) {
            throw new AssertionError(String.format("URL mismatch. Expected: '%s', Actual: '%s'", 
                expectedUrl, actualUrl));
        }
    }

    protected void validateElementText(String selector, String expectedText) {
        String actualText = getText(selector);
        if (!actualText.equals(expectedText)) {
            throw new AssertionError(String.format("Element text mismatch for '%s'. Expected: '%s', Actual: '%s'", 
                selector, expectedText, actualText));
        }
    }
}
