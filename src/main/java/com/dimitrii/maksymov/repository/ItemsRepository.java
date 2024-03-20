package com.dimitrii.maksymov.repository;

import com.dimitrii.maksymov.model.Items;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ItemsRepository extends MongoRepository<Items, String> {

    Optional<Items> findByItemId(String itemId);
}
