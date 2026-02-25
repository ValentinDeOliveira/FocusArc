package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.exception.ArcDoesNotExistException;
import com.valentin_d.focusarc.model.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.BaseService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArcLoader extends BaseService {
    private final ArcRepository arcRepository;

    public Arc getArcIfExists(@NotNull final ArcId arcId) {
        return fetchOrThrow(arcRepository, arcId, () -> new ArcDoesNotExistException(arcId));
    }

    public void assertArcExists(final ArcId arcId) {
        existsOrThrow(arcRepository, arcId, () -> new ArcDoesNotExistException(arcId));
    }
}