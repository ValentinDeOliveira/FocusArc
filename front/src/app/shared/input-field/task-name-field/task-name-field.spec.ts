import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TaskNameField} from './task-name-field';

describe('TaskNameField', () => {
    let component: TaskNameField;
    let fixture: ComponentFixture<TaskNameField>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [TaskNameField],
        }).compileComponents();

        fixture = TestBed.createComponent(TaskNameField);
        component = fixture.componentInstance;
        await fixture.whenStable();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
