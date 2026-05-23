import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcCompletion } from './arc-completion';

describe('ArcCompletion', () => {
    let component: ArcCompletion;
    let fixture: ComponentFixture<ArcCompletion>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcCompletion],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcCompletion);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
