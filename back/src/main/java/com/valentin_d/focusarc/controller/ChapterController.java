package com.valentin_d.focusarc.controller;

import com.valentin_d.focusarc.dto.chapter.ChapterCreationDto;
import com.valentin_d.focusarc.dto.chapter.ChapterSummaryResponseDto;
import com.valentin_d.focusarc.dto.chapter.ChapterUpdateDto;
import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.service.chapter.ChapterService;
import com.valentin_d.focusarc.util.ResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Chapters", description = "One chapter per arc per calendar day")
@SecurityRequirement(name = "Bearer")
@RestController
@RequestMapping("/chapters")
@RequiredArgsConstructor
@Validated
public class ChapterController {
    private final ChapterService service;

    @Operation(summary = "Get a chapter by ID")
    @GetMapping("/{chapterId}")
    public ResponseEntity<Chapter> getById(@AuthenticationPrincipal final User user,
                                           @PathVariable final ChapterId chapterId) {
        final var chapter = service.findById(chapterId, user.getId());
        return ResponseUtil.wrapOrNotFound(chapter);
    }

    @Operation(summary = "Get all chapters for an arc")
    @ApiResponse(responseCode = "204", description = "No chapters found")
    @GetMapping("/arcs/{arcId}")
    public ResponseEntity<List<Chapter>> getAllForArc(@AuthenticationPrincipal final User user,
                                                      @PathVariable final ArcId arcId) {
        final var arcChapters = service.findAllForArc(arcId, user.getId());
        return ResponseEntity.ok(arcChapters);
    }

    @Operation(summary = "Create a chapter for an arc")
    @ApiResponse(responseCode = "400", description = "A chapter already exists for this arc + date")
    @PostMapping
    public ResponseEntity<Chapter> create(@AuthenticationPrincipal final User user,
                                          @Valid @RequestBody final ChapterCreationDto chapterCreationDto) {
        final var chapter = service.create(chapterCreationDto, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(chapter);
    }

    @Operation(summary = "Update a chapter")
    @PutMapping("/{chapterId}")
    public ResponseEntity<Chapter> update(@AuthenticationPrincipal final User user,
                                          @PathVariable final ChapterId chapterId,
                                          @Valid @RequestBody final ChapterUpdateDto chapterUpdateDto) {
        final var chapter = service.update(chapterId, user.getId(), chapterUpdateDto);
        return ResponseEntity.ok(chapter);
    }

    @Operation(summary = "Delete a chapter by ID")
    @DeleteMapping("/{chapterId}")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal final User user,
                                       @PathVariable final ChapterId chapterId) {
        service.delete(chapterId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete all chapters for an arc")
    @DeleteMapping("/arcs/{arcId}")
    public ResponseEntity<Void> deleteAllForArc(@AuthenticationPrincipal final User user,
                                                @PathVariable final ArcId arcId) {
        service.deleteAllForArc(arcId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get today's chapter summary",
            description = "Returns the chapter for today in the user's active arc, with pending tasks and remaining minutes."
    )
    @ApiResponse(responseCode = "400", description = "User has no active arc")
    @GetMapping("/summary")
    public ResponseEntity<ChapterSummaryResponseDto> getChapterSummary(@AuthenticationPrincipal final User user) {
        final var summary = service.getChapterSummary(user.getId());
        return ResponseEntity.ok(summary);
    }
}