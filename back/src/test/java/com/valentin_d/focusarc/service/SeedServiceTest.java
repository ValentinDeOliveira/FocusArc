package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.repository.*;
import com.valentin_d.focusarc.service.seed.SeedService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerId;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUserWithEmailAndPassword;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeedServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private ArcRepository arcRepository;
    @Mock
    private ChapterRepository chapterRepository;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TagRepository tagRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private SeedService seedService;

    @BeforeEach
    void setup() {
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(tagRepository.save(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void shouldSkipCleanup_whenSeededUserDoesNotExist() {
        when(userRepository.findByEmail(SeedService.SEED_EMAIL)).thenReturn(Optional.empty());
        when(chapterRepository.findAllByArc(any())).thenReturn(List.of());

        seedService.doSeed();

        verify(taskRepository, never()).deleteAllByChapter(any());
        verify(chapterRepository, never()).deleteAllByArc(any());
        verify(arcRepository, never()).deleteAllByOwner(any());
        verify(tagRepository, never()).deleteAllByOwner(any());
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void shouldDeleteInCascadeOrder_whenSeededUserExists() {
        final var seededUser = aUserWithEmailAndPassword(SeedService.SEED_EMAIL, SeedService.SEED_PASSWORD);
        final var arc = anArcWithOwnerId(seededUser.getId());
        final var chapter = aChapterWithArcId(arc.getId());

        when(userRepository.findByEmail(SeedService.SEED_EMAIL)).thenReturn(Optional.of(seededUser));
        when(arcRepository.findAllByOwner(seededUser.getId())).thenReturn(List.of(arc));
        // first call: cleanup fetches chapters under the old arc
        // second call: recalculateArc at end of doSeed (new arc, returns empty)
        when(chapterRepository.findAllByArc(any()))
                .thenReturn(List.of(chapter))
                .thenReturn(List.of());

        final InOrder order = inOrder(taskRepository, chapterRepository, arcRepository, tagRepository, userRepository);

        seedService.doSeed();

        order.verify(taskRepository).deleteAllByChapter(chapter.getId());
        order.verify(chapterRepository).deleteAllByArc(arc.getId());
        order.verify(arcRepository).deleteAllByOwner(seededUser.getId());
        order.verify(tagRepository).deleteAllByOwner(seededUser.getId());
        order.verify(userRepository).deleteById(seededUser.getId());
    }
}