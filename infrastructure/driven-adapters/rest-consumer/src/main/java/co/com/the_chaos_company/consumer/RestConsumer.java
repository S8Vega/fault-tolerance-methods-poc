package co.com.the_chaos_company.consumer;

import co.com.the_chaos_company.model.news.News;
import co.com.the_chaos_company.model.news.gateways.NewsRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

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
    
    @CircuitBreaker(name = "findByText")
    @Override
    public Flux<News> findByText(String text) {
        return fetchNews(text)
                .map(ObjectResponse::getArticles)
                .flatMapIterable(this::convertArticlesToNews);
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
        return uriBuilder.path("/v2/everything1")
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
