import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcTaskRecurrence } from './arc-task-recurrence';

describe('ArcTaskRecurrence', () => {
    let component: ArcTaskRecurrence;
    let fixture: ComponentFixture<ArcTaskRecurrence>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcTaskRecurrence],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcTaskRecurrence);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
