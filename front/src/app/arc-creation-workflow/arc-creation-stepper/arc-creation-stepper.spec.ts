import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcCreationStepper } from './arc-creation-stepper';

describe('ArcCreationStepper', () => {
    let component: ArcCreationStepper;
    let fixture: ComponentFixture<ArcCreationStepper>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcCreationStepper],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcCreationStepper);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
