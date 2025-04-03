package ru.mooncess.media_catalog_service.mappers;

import org.springframework.stereotype.Component;
import ru.mooncess.media_catalog_service.domain.MusicResourceInfo;
import ru.mooncess.media_catalog_service.entities.MusicResource;

@Component
public class MusicResourceMapper {
    public MusicResource map(MusicResourceInfo m) {
        MusicResource mr = new MusicResource();
        mr.setSourceURI(m.getSourceURI());
        mr.setCoverURI(m.getCoverURI());
        mr.setDemoURI(m.getDemoURI());
        mr.setName(m.getName());
        mr.setKey(m.getKey());
        mr.setBpm(m.getBpm());
        mr.setGenre(m.getGenre());
        mr.setPrice(m.getPrice());
        mr.setType(m.getType());

        return mr;
    }
}