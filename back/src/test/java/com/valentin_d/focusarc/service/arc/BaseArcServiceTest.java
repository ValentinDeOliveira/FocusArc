package com.valentin_d.focusarc.service.arc;

import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.ContextLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
abstract class BaseArcServiceTest {

    @Mock protected ArcRepository arcRepository;
    @Mock protected ArcLoader arcLoader;
    @Mock protected UserLoader userLoader;
    @Mock protected ChapterService chapterService;
    @Mock protected ChapterLoader chapterLoader;
    @Mock protected TaskLoader taskLoader;
    @Mock protected ContextLoader contextLoader;
    @Mock protected TaskService taskService;
    @InjectMocks protected ArcService arcService;

    protected void doThrowArcDoesNotExistForUser(final ArcId arcId, final UserId userId) {
        doThrow(new ArcDoesNotExistForUserException(arcId, userId))
                .when(arcLoader)
                .getArcIfExistsForUser(arcId, userId);
    }
}