package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.User;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.util.ResponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
@Validated
public class ChapterController {

    private final ChapterService service;

    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getById(@PathVariable final ChapterId chapterId) {
        final var chapter = service.findById(chapterId);
        return ResponseUtil.wrapOrNotFound(chapter);
    }

    @GetMapping("/arcs/{arcId}")
    public ResponseEntity<List<Chapter>> getAllForArc(@PathVariable final ArcId arcId) {
        final var arcChapters = service.findAllForArc(arcId);
        return ResponseUtil.wrapOrNoContent(arcChapters);
    }

    @PostMapping
    public ResponseEntity<Chapter> create(@Valid @RequestBody final ChapterCreationDto chapterCreationDto) {
        final var chapter = service.create(chapterCreationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(chapter);
    }

    @PutMapping("/{chapterId}")
    public ResponseEntity<Chapter> update(@PathVariable final ChapterId chapterId,
                                       @Valid @RequestBody final ChapterUpdateDto chapterUpdateDto) {
        final var chapter = service.update(chapterId, chapterUpdateDto);
        return ResponseEntity.ok(chapter);
    }

    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> delete(@PathVariable final ChapterId chapterId) {
        service.delete(chapterId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/arcs/{arcId}")
    public ResponseEntity<Void> deleteAllForArc(@PathVariable final ArcId arcId) {
        service.deleteAllForArc(arcId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/summary")
    public ResponseEntity<ChapterSummaryResponseDto> getChapterSummary(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(service.getChapterSummary(user.getId()));
    }
}
