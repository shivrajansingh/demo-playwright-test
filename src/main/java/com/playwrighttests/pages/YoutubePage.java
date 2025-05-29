package com.playwrighttests.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;


public class YoutubePage extends BasePage {

    private static final String PAGE_NAME = "YouTube";
    private static final String HEADER_LOGO = "ytd-masthead#masthead";
    private final String PAGE_URL = config.getBaseUrl("youtube");
    private static final String SEARCH_INPUT = "input[name='search_query']";
    private static final String YOUTUBE_FIRST_THUMBNAIL = "div#contents yt-lockup-view-model:first-of-type";
    private static final String VIDEO_PLAYER = "div#container video";
    public YoutubePage(Page page) {
        super(page);
    }

    /**
     * Navigate to YouTube homepage
     */
    public void navigateToHome() {
        log.info("Navigating to YouTube homepage: {}", PAGE_URL);
        navigateTo(PAGE_URL);
    }

    /**
     * Search for a video on YouTube
     *
     * @param query the search term
     */
    public void search(String query) {
        log.info("Searching for: {}", query);
        page.fill(SEARCH_INPUT, query);
        page.keyboard().press("Enter");
        waitForNetworkIdle();
    }

    public void validatePageLoaded(){
        log.info("Validating if {} page is loaded", PAGE_NAME);
        validatePageTitle(PAGE_NAME);
        log.info("Page title validated successfully");
        if(!isVisible(HEADER_LOGO)){
            log.warn("YouTube header logo is not visible");
            throw new AssertionError("Logo is not visible on the page");
        }
    }

    public void clickOnFirstVideoThumbnail() {
        log.info("Clicking on the first video thumbnail");
        if (isVisible(YOUTUBE_FIRST_THUMBNAIL)) {
            page.click(YOUTUBE_FIRST_THUMBNAIL);
            log.info("First video thumbnail clicked successfully");
            waitForNetworkIdle();
            waitForElementVisible(VIDEO_PLAYER);
            log.info("Video player is now visible after clicking the first video thumbnail");
        } else {
            log.warn("First video thumbnail is not visible");
            throw new AssertionError("First video thumbnail is not visible on the page");
        }
    }

    public boolean isSkipButtonClicked(){
        log.info("Clicking on the skip button if it is visible within 15 seconds");
        String skipButtonSelector = "button.ytp-skip-ad-button";
        
        try {
            // Set a timeout of 15 seconds for waiting for the skip button
            page.waitForSelector(skipButtonSelector, 
                new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(15000));
                    
            if (isVisible(skipButtonSelector)) {
                page.click(skipButtonSelector);
                log.info("Skip button clicked successfully");
                return true;
            } else {
                log.info("Skip button is not visible after waiting, skipping click action");
                return false;
            }
        } catch (com.microsoft.playwright.TimeoutError e) {
            log.info("Skip button did not appear within 15 seconds timeout, continuing execution");
            return false;
        }
    }
    
}
