package ru.mooncess.file_service.mapper;

import org.springframework.stereotype.Component;
import ru.mooncess.file_service.domain.MusicResourceBaseInfo;
import ru.mooncess.file_service.domain.MusicResourceInfo;

@Component
public class MusicResourceMapper {
    public MusicResourceInfo map(MusicResourceBaseInfo m) {
        MusicResourceInfo mri = new MusicResourceInfo();
        mri.setBpm(m.getBpm());
        mri.setKey(m.getKey());
        mri.setGenre(m.getGenre());
        mri.setName(m.getName());
        mri.setPrice(m.getPrice());
        mri.setType(m.getType());
        mri.setAuthors(m.getAuthors());

        return mri;
    }
}
