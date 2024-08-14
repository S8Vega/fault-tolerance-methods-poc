package co.com.the_chaos_company.usecase.news;

import co.com.the_chaos_company.model.news.News;
import co.com.the_chaos_company.model.news.gateways.NewsRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class NewsUseCase {

    private final NewsRepository newsRepository;

    public Flux<News> findByText(String text) {
        return newsRepository.findByText(text);
    }
}
