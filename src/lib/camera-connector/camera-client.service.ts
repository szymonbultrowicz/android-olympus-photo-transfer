import {cloneDeep, flatten} from 'lodash';
import {Injectable, ComponentFactoryResolver} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {map, catchError, timeout, switchMap, tap} from 'rxjs/operators';
import {retrieveDirectories, retrieveFiles} from './html-response-parser';
import {CameraConnectionException} from './exceptions/camera-connection-exception';
import {zip, forkJoin} from 'rxjs';
import {CameraConfigService} from '../camera-config/camera-config.service';

@Injectable({
    providedIn: 'root',
})
export class CameraClientService {

    constructor(
        private readonly http: HttpClient,
        private readonly cameraConfigService: CameraConfigService
    ) {
    }

    listFiles$() {
        return this.queryCamera().pipe(
            map(html => retrieveDirectories(html)),
            switchMap(dirs =>
                forkJoin(
                    dirs.map(dir =>
                        this.queryCamera(`${dir}/`).pipe(
                            map(html => retrieveFiles(dir, html, (name: string) =>
                                this.cameraConfigService.buildThumbnailUrl(dir, name)
                            ))
                        )
                    )
                )
            ),
            map(htmls => flatten(htmls))
        );
    }

    private queryCamera(path: string = '') {
        return this.http
            .get(`${this.cameraConfigService.cameraUrl}/${path}`, {
                responseType: 'text',
            })
            .pipe(
                timeout(5000),
                catchError(e => {
                    console.error('Error querying the camera', e);
                    throw new CameraConnectionException();
                })
            );
    }
}
