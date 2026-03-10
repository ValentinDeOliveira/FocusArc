import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcProgressBar } from './arc-progress-bar';

describe('ArcProgressBar', () => {
    let component: ArcProgressBar;
    let fixture: ComponentFixture<ArcProgressBar>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcProgressBar],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcProgressBar);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
