import {Component, OnDestroy, OnInit} from '@angular/core';
import { CameraClientService } from '~/lib/camera-connector/camera-client.service';
import { Subject } from 'rxjs';
import {takeUntil} from 'rxjs/internal/operators';
import {PhotoInfo} from '~/lib/camera-connector/photo-info';
import {groupBy} from 'lodash';

enum State {
    INITIAL_LOADING,
    LOADED,
    CONNECTION_ERROR,
}

interface PhotoInfoGroup {
    date: Date;
    files: PhotoInfo[];
}

function ensure2Digits(n: number) {
    return n < 10 ? `0${n}` : `${n}`;
}

@Component({
    selector: 'ns-photos',
    templateUrl: './photos.component.html',
    styleUrls: ['./photos.component.css'],
})
export class PhotosComponent implements OnInit, OnDestroy {

    states = State;
    state: State = State.INITIAL_LOADING;
    fileGroups: PhotoInfoGroup[] = [];

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
                    this.fileGroups = this.groupPhotos(files);
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

    groupPhotos(photos: PhotoInfo[]) {
       return Object.entries(groupBy(photos, (photo): number =>
            new Date(
                photo.dateTaken.getFullYear(),
                photo.dateTaken.getMonth(),
                photo.dateTaken.getDay(),
            ).getTime(),
        )).sort((o1, o2) =>
            parseInt(o2[0], 10) - parseInt(o2[0], 10)
        ).map(entry => ({
           date: new Date(parseInt(entry[0], 10)),
           files: entry[1],
       } as PhotoInfoGroup));
    }

    formatDate(date: Date) {
        return `${date.getFullYear()}/${date.getMonth()}/${date.getDay()}`;
    }

    formatTime(date: Date) {
        const h = ensure2Digits(date.getHours());
        const m = ensure2Digits(date.getMinutes());
        const s = ensure2Digits(date.getSeconds());
        return `${h}:${m}:${s}`;
    }
}
