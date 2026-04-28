import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcTaskStep } from './arc-task-step';

describe('ArcTaskStep', () => {
    let component: ArcTaskStep;
    let fixture: ComponentFixture<ArcTaskStep>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcTaskStep],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcTaskStep);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
