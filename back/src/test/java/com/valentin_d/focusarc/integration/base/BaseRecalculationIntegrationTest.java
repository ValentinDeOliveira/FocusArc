package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseRecalculationIntegrationTest extends BaseTaskControllerIntegrationTest {
    @Autowired
    protected ArcRepository arcRepository;
    @Autowired
    protected TaskRepository taskRepository;
    @Autowired
    protected ChapterRepository chapterRepository;
    protected final String URL = "/tasks";
}