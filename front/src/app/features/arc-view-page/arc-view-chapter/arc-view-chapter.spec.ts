import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcViewChapter } from './arc-view-chapter';

describe('ArcViewChapter', () => {
    let component: ArcViewChapter;
    let fixture: ComponentFixture<ArcViewChapter>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcViewChapter],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcViewChapter);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
