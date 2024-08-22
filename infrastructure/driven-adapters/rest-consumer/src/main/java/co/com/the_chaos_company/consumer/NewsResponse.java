package co.com.the_chaos_company.consumer;

import co.com.the_chaos_company.model.news.News;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class NewsResponse {
    private Map<String, String> source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
    private String content;

    public News toNews() {
        return News.builder()
                .source(source.get("name"))
                .author(author)
                .title(title)
                .description(description)
                .url(url)
                .urlToImage(urlToImage)
                .publishedAt(OffsetDateTime.parse(publishedAt).toLocalDateTime())
                .content(content)
                .build();
    }
}
