import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcCreation } from './arc-creation';

describe('ArcCreation', () => {
    let component: ArcCreation;
    let fixture: ComponentFixture<ArcCreation>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcCreation],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcCreation);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
