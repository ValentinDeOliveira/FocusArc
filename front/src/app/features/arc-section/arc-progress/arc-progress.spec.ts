import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcProgress } from './arc-progress';

describe('ArcProgress', () => {
    let component: ArcProgress;
    let fixture: ComponentFixture<ArcProgress>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcProgress],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcProgress);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
