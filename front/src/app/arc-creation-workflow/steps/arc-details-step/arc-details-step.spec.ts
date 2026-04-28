import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcDetailsStep } from './arc-details-step';

describe('ArcDetailsStep', () => {
    let component: ArcDetailsStep;
    let fixture: ComponentFixture<ArcDetailsStep>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcDetailsStep],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcDetailsStep);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
