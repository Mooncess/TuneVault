package ru.mooncess.media_catalog_service.filter;

import org.springframework.data.jpa.domain.Specification;
import ru.mooncess.media_catalog_service.domain.enums.MusicResourceStatus;
import ru.mooncess.media_catalog_service.entities.MusicResource;
import ru.mooncess.media_catalog_service.entities.Producer;

public class ProducerMusicResourceSpecifications {
    public static Specification<MusicResource> withProducer(Producer producer) {
        return (root, query, cb) ->
                producer == null ? null : cb.equal(root.get("producer"), producer);
    }

    public static Specification<MusicResource> withGenre(String genre) {
        return (root, query, cb) ->
                genre == null ? null : cb.equal(root.get("genre"), genre);
    }

    public static Specification<MusicResource> withPriceBetween(Double minPrice, Double maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<MusicResource> withType(String type) {
        return (root, query, cb) ->
                type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<MusicResource> withStatus() {
        return (root, query, cb) ->
                cb.equal(root.get("status"), MusicResourceStatus.AVAILABLE);
    }

    public static Specification<MusicResource> buildSpecification(ProducerMusicResourceFilter filter) {
        return Specification.where(withProducer(filter.getProducer()))
                .and(withGenre(filter.getGenre()))
                .and(withPriceBetween(filter.getMinPrice(), filter.getMaxPrice()))
                .and(withType(filter.getType()))
                .and(withStatus());
    }
}
