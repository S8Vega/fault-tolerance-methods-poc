package co.com.the_chaos_company.model.news.gateways;

import co.com.the_chaos_company.model.news.News;
import reactor.core.publisher.Flux;

public interface NewsRepository {
    Flux<News> findByText(String text);
}
