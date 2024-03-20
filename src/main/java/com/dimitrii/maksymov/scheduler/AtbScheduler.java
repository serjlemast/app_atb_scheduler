package com.dimitrii.maksymov.scheduler;

import com.dimitrii.maksymov.model.Items;
import com.dimitrii.maksymov.model.MultiSearch;
import com.dimitrii.maksymov.repository.ItemsRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class AtbScheduler {
    private final RestClient restClient;
    private final ItemsRepository repository;

    @SneakyThrows
    @Async
    @Scheduled(cron = "${scheduler.cron}")
    public void processing() {
        URI uri = UriComponentsBuilder.fromUriString("https://api.multisearch.io")
                .queryParam("id", "11280")
                .queryParam("location", "1154")
                .queryParam("query", "Пельмені 0,5 кг EcoSmac")
                .build()
                .toUri();

        MultiSearch searchEntity = restClient
                .get()
                .uri(uri)
                .retrieve().body(MultiSearch.class);
        log.info("Testing - {},time - {}", searchEntity, new Date());

        MultiSearch.Results results = searchEntity.getResults();
        if (results != null) {
            List<MultiSearch.ItemsGroups> itemGroups = results.getItem_groups();
            Optional<Items> first = itemGroups.stream()
                    .flatMap(l -> l.getItems().stream())
                    .flatMap(l -> l.stream())
                    .findFirst();
            if (first.isPresent()) {
                Items item = first.get();
                Optional<Items> byItemId = repository.findByItemId(item.getItemId());
                if (byItemId.isPresent()) {
                    double priceFromResp = item.getPrice();
                    double priceFromDB = byItemId.get().getPrice();
                    if (priceFromResp != priceFromDB) {
                        sendMessageToTelegram(item);
                    }
                } else {
                    repository.save(first.get());
                }
            }
        }
    }

    private void sendMessageToTelegram(Items item) {
        restClient.post().uri("https://springboot-telegram-bot.onrender.com/v1/messages")
                .body(MessageRequest
                        .builder()
                        .catId(1138276555)
                        .message(String.format("name - %s, price - %s \n %s", item.getName(), item.getPrice(), item.getPicture()))
                        .build())
                .retrieve();
    }

    @Builder
    @Data
    static class MessageRequest {
        private int catId;
        private String message;
    }

}
