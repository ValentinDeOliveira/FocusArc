import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcCardContainer } from './arc-card-container';

describe('ArcCard', () => {
    let component: ArcCardContainer;
    let fixture: ComponentFixture<ArcCardContainer>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcCardContainer],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcCardContainer);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
