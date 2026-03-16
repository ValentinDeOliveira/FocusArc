import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardTaskTimer } from './dashboard-task-timer';

describe('DashboardTaskTimer', () => {
    let component: DashboardTaskTimer;
    let fixture: ComponentFixture<DashboardTaskTimer>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [DashboardTaskTimer],
        }).compileComponents();

        fixture = TestBed.createComponent(DashboardTaskTimer);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
