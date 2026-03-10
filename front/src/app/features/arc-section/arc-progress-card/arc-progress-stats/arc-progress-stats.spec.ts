import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcProgressStats } from './arc-progress-stats';

describe('ArcProgressStats', () => {
    let component: ArcProgressStats;
    let fixture: ComponentFixture<ArcProgressStats>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcProgressStats],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcProgressStats);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
