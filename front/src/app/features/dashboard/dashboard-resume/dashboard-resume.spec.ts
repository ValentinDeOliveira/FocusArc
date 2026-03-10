import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardResume } from './dashboard-resume';

describe('DashboardResume', () => {
    let component: DashboardResume;
    let fixture: ComponentFixture<DashboardResume>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [DashboardResume],
        }).compileComponents();

        fixture = TestBed.createComponent(DashboardResume);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
