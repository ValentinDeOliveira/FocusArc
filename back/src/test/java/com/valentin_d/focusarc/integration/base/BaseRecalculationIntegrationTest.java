package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.service.ArcService;
import com.valentin_d.focusarc.service.ChapterService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseRecalculationIntegrationTest extends BaseTaskControllerIntegrationTest {
    @Autowired
    protected ChapterService chapterService;
    @Autowired
    protected ArcRepository arcRepository;
    @Autowired
    protected ArcService arcService;
    protected final String URL = "/tasks";

    @BeforeEach
    public void setUp() {
        arcRepository.deleteAll();
    }
}