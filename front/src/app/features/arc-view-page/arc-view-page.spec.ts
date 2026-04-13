import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArcViewPage } from './arc-view-page';

describe('ArcViewPage', () => {
    let component: ArcViewPage;
    let fixture: ComponentFixture<ArcViewPage>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ArcViewPage],
        }).compileComponents();

        fixture = TestBed.createComponent(ArcViewPage);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
