import {Component, inject, OnInit} from '@angular/core';
import {ArcService} from '../core/services/arc.service';
import {AuthService} from '../core/services/auth.service';
import {LoginRequestDto} from '../models/auth.model';

@Component({
    selector: 'app-dashboard',
    imports: [],
    templateUrl: './dashboard.html',
    styleUrl: './dashboard.css',
})
export class Dashboard implements OnInit {
    private arcService = inject(ArcService);
    private authService = inject(AuthService);

    ngOnInit() {
        console.log("this.arcService");
        const dto: LoginRequestDto = {
            email: 'alice@example.com',
            password: 'password123',
        }

        this.authService.login(dto).subscribe(response => {
            localStorage.setItem('token', response.accessToken);
        });

        this.arcService.getAll().subscribe(response => {
            console.log(response);
        })
    }
}
