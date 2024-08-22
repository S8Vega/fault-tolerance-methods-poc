package co.com.the_chaos_company.api;

import co.com.the_chaos_company.model.news.News;
import co.com.the_chaos_company.usecase.news.NewsUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
@AllArgsConstructor
public class ApiRest {
    private final NewsUseCase useCase;


    @GetMapping(path = "/{text}")
    public Flux<News> commandName(@PathVariable("text") String text) {
        return useCase.findByText(text);
    }
}
