import { Component, OnInit } from '@angular/core';
import { Frame } from '@nativescript/core';
import { CameraConfigService } from '~/lib/camera-config/camera-config.service';
import { RouterExtensions } from '@nativescript/angular/router';

@Component({
    selector: 'ns-settings',
    templateUrl: './settings.component.html',
    styleUrls: ['./settings.component.css'],
})
export class SettingsComponent implements OnInit {
    constructor(
        private readonly cameraConfigService: CameraConfigService,
        private routerExtensions: RouterExtensions,
    ) {}

    ngOnInit() {}

    goBack() {
        Frame.topmost().goBack();
    }

    onCameraUrl() {
        this.routerExtensions.navigate(['settings', 'camera-url']);
    }


}
