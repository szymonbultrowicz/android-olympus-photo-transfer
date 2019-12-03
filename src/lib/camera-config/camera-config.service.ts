import { Injectable } from '@angular/core';
import {
    getString,
    setString,
    getNumber,
    setNumber,
} from 'tns-core-modules/application-settings';

const enum SettingsKeys {
    CAMERA_PROTO = 'CAMERA_PROTO',
    CAMERA_HOST = 'CAMERA_HOST',
    CAMERA_PORT = 'CAMERA_PORT',
    CAMERA_PATH = 'CAMERA_PATH',
}

@Injectable({
    providedIn: 'root',
})
export class CameraConfigService {

    readonly availableProtocols = ['http', 'https'];

    constructor() {}

    get cameraUrl() {
        return `${this.cameraProto}://${this.cameraHost}${
            this.cameraPort ? `:${this.cameraPort}` : ''
        }/${this.cameraPath.replace(/^\/+/, '')}`;
    }

    get cameraHost() {
        return getString(SettingsKeys.CAMERA_HOST, '192.168.0.100');
    }

    set cameraHost(host: string) {
        if (this.cameraHost !== host) {
            setString(SettingsKeys.CAMERA_HOST, host);
        }
    }

    get cameraProto() {
        return getString(SettingsKeys.CAMERA_PROTO, 'http');
    }

    set cameraProto(proto: string) {
        if (this.cameraProto !== proto) {
            if (!['https', 'http'].includes(proto)) {
                throw new Error('The protocol can be either "http" or "https"');
            }
            setString(SettingsKeys.CAMERA_PROTO, proto);
        }
    }

    get cameraPort(): number {
        return getNumber(SettingsKeys.CAMERA_PORT, 0);
    }

    set cameraPort(port: number) {
        if (this.cameraPort !== port) {
            setNumber(SettingsKeys.CAMERA_PORT, port);
        }
    }

    get cameraPath() {
        return getString(SettingsKeys.CAMERA_PATH, '/DCIM');
    }

    set cameraPath(path: string) {
        if (this.cameraPath !== path) {
            setString(SettingsKeys.CAMERA_PATH, path);
        }
    }
}
