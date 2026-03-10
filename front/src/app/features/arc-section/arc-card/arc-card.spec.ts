import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcCard } from './arc-card';

describe('ArcCard', () => {
    let component: ArcCard;
    let fixture: ComponentFixture<ArcCard>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcCard],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcCard);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
