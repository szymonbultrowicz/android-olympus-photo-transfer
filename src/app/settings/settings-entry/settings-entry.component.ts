import { Component, OnInit, Input } from '@angular/core';

@Component({
    selector: 'ns-settings-entry',
    templateUrl: './settings-entry.component.html',
    styleUrls: ['./settings-entry.component.css'],
})
export class SettingsEntryComponent {
    @Input()
    label: string | undefined;

    @Input()
    currentValue: string | undefined;

    @Input()
    tap: () => void = () => {};
}
