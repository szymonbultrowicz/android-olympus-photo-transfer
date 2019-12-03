import {Component, Input, OnInit} from '@angular/core';
import {CameraConfigService} from "~/lib/camera-config/camera-config.service";
import { RouterExtensions } from '@nativescript/angular/router';

@Component({
    selector: 'ns-settings-camera-url',
    templateUrl: './settings-camera-url.component.html',
    styleUrls: ['./settings-camera-url.component.css']
})
export class SettingsCameraUrlComponent {


    readonly selectedProto: number;

    constructor(
        readonly cameraConfigService: CameraConfigService,
        private readonly routerExtensions: RouterExtensions,
    ) {
        this.selectedProto = cameraConfigService.availableProtocols.indexOf(cameraConfigService.cameraProto);
    }

    goBack() {
        this.routerExtensions.back();
    }

    set proto(value: string) {
        console.log('proto', value);
        this.cameraConfigService.cameraProto = value;
    }

    get proto() {
        return this.cameraConfigService.cameraProto;
    }

    set hostname(value: string) {
        this.cameraConfigService.cameraHost = value;
    }

    get hostname() {
        return this.cameraConfigService.cameraHost;
    }

    get port(): string {
        return `${this.cameraConfigService.cameraPort}` || '';
    }

    set port(value: string) {
        this.cameraConfigService.cameraPort = value !== '' ? parseInt(value) : 0;
    }

    get path() {
        return this.cameraConfigService.cameraPath;
    }

    set path(value: string) {
        this.cameraConfigService.cameraPath = value;
    }
}
