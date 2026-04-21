import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcStatsBase } from './arc-stats-base';

describe('ArcStatsBase', () => {
    let component: ArcStatsBase;
    let fixture: ComponentFixture<ArcStatsBase>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcStatsBase],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcStatsBase);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
