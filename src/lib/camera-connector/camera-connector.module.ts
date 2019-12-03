import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { NativeScriptHttpClientModule } from 'nativescript-angular/http-client';
import { NativeScriptCommonModule } from 'nativescript-angular/common';

@NgModule({
    declarations: [],
    imports: [NativeScriptCommonModule, NativeScriptHttpClientModule],
    schemas: [NO_ERRORS_SCHEMA],
})
export class CameraConnectorModule {}
