import { AfterViewInit, Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { AuthService } from 'src/app/services/auth.service';
import { ProfileService } from 'src/app/services/profile.service';

@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, AfterViewInit {
  updateProfileForm!: FormGroup
  userProfile!: User

  constructor(
    private fb: FormBuilder,
    private authSvc: AuthService,
    private profileSvc: ProfileService,
    private router: Router,
    private activatedRoute: ActivatedRoute) { }

  ngOnInit(): void {
    this.updateProfileForm = this.fb.group({
      email: this.fb.control<string>("", [Validators.required]),
      givenname: this.fb.control<string>("", [Validators.required]),
      familyname: this.fb.control<string>("", [Validators.required]),
      schedule: this.fb.control<number | null>(null, [Validators.required]),
    })
  }

  ngAfterViewInit(): void {
    // Get user details
    if (this.authSvc.isLoggedIn) {
      this.profileSvc.getProfile()
        .then((response) => {
          this.userProfile = response;
          console.info(this.userProfile)
          this.updateProfileForm.patchValue({
            email: this.userProfile.email
          });
        })
        .catch((error) => {
          console.error('Error retrieving user profile:', error);
        });
    } else {
      const fullPath = this.activatedRoute.snapshot.url.toString();
      let queryParams = { queryParams: { fullPath } }
      this.router.navigate(['/login'], queryParams)
    }
  }

  update() {
    // TODO:
  }

}
