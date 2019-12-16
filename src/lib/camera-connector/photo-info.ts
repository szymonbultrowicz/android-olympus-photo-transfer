import { BasePhotoInfo } from './base-photo-info';
import { PhotoFileInfo } from './photo-file-info';
import { isEmpty } from 'lodash-es';

export class PhotoInfo implements BasePhotoInfo {
    constructor(readonly files: PhotoFileInfo[]) {
        if (isEmpty(files)) {
            throw new Error('Photo files list cannot be empty');
        }
    }

    private get firstFile(): PhotoFileInfo {
        return this.files[0];
    }

    get name() {
        return this.firstFile.baseFileName;
    }

    get dateTaken() {
        return this.firstFile.dateTaken;
    }

    get thumbnailUrl() {
        return this.firstFile.thumbnailUrl;
    }
}
