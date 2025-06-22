
package org.codenews.dto;

public class ArticlesTotalResponse {
    private long totalArticles;
    public ArticlesTotalResponse(long totalArticles) {
        this.totalArticles = totalArticles;
    }
    public long getTotalArticles() {
        return totalArticles;
    }
}
