import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { NativeScriptModule } from 'nativescript-angular/nativescript.module';
import { NativeScriptMaterialCardViewModule } from 'nativescript-material-cardview/angular';
import { DropDownModule } from 'nativescript-drop-down/angular';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { PhotosComponent } from './photos/photos.component';
import { CameraConnectorModule } from '~/lib/camera-connector/camera-connector.module';
import { SettingsComponent } from './settings/settings.component';
import { SettingsEntryComponent } from './settings/settings-entry/settings-entry.component';
import { SettingsCameraUrlComponent } from './settings/settings-camera-url/settings-camera-url.component';

// Uncomment and add to NgModule imports if you need to use two-way binding
// import { NativeScriptFormsModule } from "nativescript-angular/forms";

// Uncomment and add to NgModule imports if you need to use the HttpClient wrapper
// import { NativeScriptHttpClientModule } from "nativescript-angular/http-client";

@NgModule({
    bootstrap: [AppComponent],
    imports: [
        NativeScriptModule,
        AppRoutingModule,
        CameraConnectorModule,
        NativeScriptMaterialCardViewModule,
        DropDownModule,
    ],
    declarations: [
        AppComponent,
        PhotosComponent,
        SettingsComponent,
        SettingsEntryComponent,
        SettingsCameraUrlComponent,
    ],
    providers: [],
    schemas: [NO_ERRORS_SCHEMA],
})
/*
Pass your application module to the bootstrapModule function located in main.ts to start your app
*/
export class AppModule {}
