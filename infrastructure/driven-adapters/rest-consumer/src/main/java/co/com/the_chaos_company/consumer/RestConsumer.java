package co.com.the_chaos_company.consumer;

import co.com.the_chaos_company.model.news.News;
import co.com.the_chaos_company.model.news.gateways.NewsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class RestConsumer implements NewsRepository {
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<ObjectResponse> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    //    @CircuitBreaker(name = "testPost")
//    public Mono<ObjectResponse> testPost() {
//        ObjectRequest request = ObjectRequest.builder()
//                .val1("exampleval1")
//                .val2("exampleval2")
//                .build();
//        return client
//                .post()
//                .body(Mono.just(request), ObjectRequest.class)
//                .retrieve()
//                .bodyToMono(ObjectResponse.class);
//    }

    @CircuitBreaker(name = "findByText", fallbackMethod = "findByTextFallback")
    @Override
    public Flux<News> findByText(String text) {
        return fetchNews(text)
                .map(ObjectResponse::getArticles)
                .flatMapIterable(this::convertArticlesToNews);
    }

    public Flux<News> findByTextFallback(String text, Exception ignored) {
        log.error("Fallback method called for text: {}", text);
        log.error("Error: {} {}", ignored.getClass().getName(), ignored.getMessage());
        return Flux.just(News.builder()
                .title("Fallback title")
                .description("Fallback description")
                .url("Fallback url")
                .urlToImage("Fallback urlToImage")
                .publishedAt(LocalDateTime.now())
                .build());
    }

    private Flux<ObjectResponse> fetchNews(String text) {
        return client
                .get()
                .uri(uriBuilder -> buildUri(uriBuilder, text))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> Mono.error(new RuntimeException("Error >>> " + clientResponse.statusCode())))
                .bodyToFlux(ObjectResponse.class);
    }

    private URI buildUri(UriBuilder uriBuilder, String text) {
        return uriBuilder.path("/v2/everything")
                .queryParam("language", "es")
                .queryParam("pageSize", "5")
                .queryParam("from", LocalDate.now().minusDays(1).toString())
                .queryParam("to", LocalDate.now().toString())
                .queryParam("sortBy", "relevancy")
                .queryParam("q", text)
                .build();
    }

    private List<News> convertArticlesToNews(List<NewsResponse> articles) {
        return articles.stream()
                .map(NewsResponse::toNews)
                .toList();
    }

}
