import { NgModule } from '@angular/core';
import { NativeScriptRouterModule } from 'nativescript-angular/router';
import { Routes } from '@angular/router';

import { PhotosComponent } from './photos/photos.component';
import { SettingsComponent } from './settings/settings.component';
import {SettingsCameraUrlComponent} from "~/app/settings/settings-camera-url/settings-camera-url.component";

const routes: Routes = [
    { path: 'photos', component: PhotosComponent },
    { path: 'settings', children: [{
        path: '',
        component: SettingsComponent,
    }, {
        path: 'camera-url',
        component: SettingsCameraUrlComponent,
    }]},
    { path: '', redirectTo: '/photos', pathMatch: 'full' },
];

@NgModule({
    imports: [NativeScriptRouterModule.forRoot(routes)],
    exports: [NativeScriptRouterModule],
})
export class AppRoutingModule {}
