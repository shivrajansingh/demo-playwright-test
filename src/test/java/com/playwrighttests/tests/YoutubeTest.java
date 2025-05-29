package com.playwrighttests.tests;

import org.junit.jupiter.api.*;
import com.playwrighttests.pages.YoutubePage;

public class YoutubeTest extends BaseTest {

    private YoutubePage youtube;
    
    @BeforeEach
    void setUpTest() {
        youtube = new YoutubePage(page);
    }

    private void openYoutubeHomepage() {
        reportManager.addInfo("test-001-youtube-homepage-loads", "Starting homepage load test");
        
        youtube.navigateToHome();
        reportManager.addInfo("test-001-youtube-homepage-loads", "Navigated to YouTube homepage");
        
        youtube.validatePageLoaded();
        reportManager.addInfo("test-001-youtube-homepage-loads", "Verified homepage loaded successfully");
        
        // Take a screenshot of successful load
        takeScreenshot("youtube_homepage");
        reportManager.addInfo("test-001-youtube-homepage-loads", "Screenshot captured of homepage");
    }

    private void performSearch(String search_query) {
        reportManager.addInfo("test-001-youtube-homepage-loads", "Starting search test");
        
        String searchQuery = search_query;
        youtube.search(searchQuery);
        reportManager.addInfo("test-001-youtube-homepage-loads", "Performed search operation");
        
        // Assert search results are displayed
        Assertions.assertTrue(page.url().contains("search_query=" + searchQuery.replace(" ", "+")), 
                             "Search URL should contain the search query parameter");
        
        // Assert search results are displayed on the page
        String searchResultsSelector = "ytd-video-renderer";
        page.waitForSelector(searchResultsSelector);
        Assertions.assertTrue(page.locator(searchResultsSelector).count() > 0, 
                             "Search results should be displayed");
        reportManager.addInfo("test-001-youtube-homepage-loads", "Verified search results displayed");
        
        // Take a screenshot of search results
        takeScreenshot("youtube_search_results");
        reportManager.addInfo("test-001-youtube-homepage-loads", "Screenshot captured of search results");
    }

    private void playFirstVideo(){
        youtube.clickOnFirstVideoThumbnail();
        reportManager.addInfo("test-001-youtube-homepage-loads", "Clicked on the first video thumbnail");
        takeScreenshot(getCurrentUrl() + "_first_video_played");
        reportManager.addInfo("test-001-youtube-homepage-loads", "Screenshot captured of first video played");
        Assertions.assertTrue(page.url().contains("watch"), 
                             "URL should contain 'watch' indicating a video is playing");

        if(youtube.isSkipButtonClicked()) {
            reportManager.addInfo("test-001-youtube-homepage-loads", "Skipped the ad successfully");
        } else {
            reportManager.addInfo("test-001-youtube-homepage-loads", "No ad to skip or skip button not found");
        }
        reportManager.addInfo("test-001-youtube-homepage-loads", "Play the video for 30 seconds");
        page.waitForTimeout(30000); // Wait for 30 seconds to simulate video playback
        reportManager.addInfo("test-001-youtube-homepage-loads", "Video playback completed"); 
    }

    @Test
    @DisplayName("test-001-youtube-homepage-loads")
    void testYoutubeHomepageLoads() {
        openYoutubeHomepage();
        performSearch("Playwright testing with Java");
        playFirstVideo();
    }
}
