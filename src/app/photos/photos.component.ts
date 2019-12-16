import {Component, OnDestroy, OnInit} from '@angular/core';
import { CameraClientService } from '~/lib/camera-connector/camera-client.service';
import { Subject } from 'rxjs';
import {takeUntil} from 'rxjs/internal/operators';
import {PhotoFileInfo} from '~/lib/camera-connector/photo-file-info';

enum State {
    INITIAL_LOADING,
    LOADED,
    CONNECTION_ERROR,
}

@Component({
    selector: 'ns-photos',
    templateUrl: './photos.component.html',
    styleUrls: ['./photos.component.css'],
})
export class PhotosComponent implements OnInit, OnDestroy {

    states = State;
    state: State = State.INITIAL_LOADING;
    files: PhotoFileInfo[] = [];

    private onDestroy$ = new Subject<void>();

    constructor(private readonly camera: CameraClientService) {}

    ngOnInit() {
        this.refresh();
    }

    refresh(pullRefresh?) {
        this.camera
            .listFiles$()
            .pipe(
                takeUntil(this.onDestroy$),
            )
            .subscribe(
                files => {
                    console.log('FILES ' + new Date());
                    this.files = files;
                    this.state = State.LOADED;
                    if (pullRefresh) {
                        pullRefresh.refreshing = false;
                    }
                },
                (err) => {
                    console.error(err);
                    this.state = State.CONNECTION_ERROR;
                    if (pullRefresh) {
                        pullRefresh.refreshing = false;
                    }
                }
            );
    }

    ngOnDestroy(): void {
        this.onDestroy$.next();
        this.onDestroy$.complete();
    }

    refreshList($event) {
        this.refresh($event.object);
    }
}
