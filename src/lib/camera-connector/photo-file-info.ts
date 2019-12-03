import { BasePhotoInfo } from './base-photo-info';

const extToMime = new Map([
    ['jpg', 'image/jpeg'],
    ['orf', 'image/x-olympus-orf'],
]);

export class PhotoFileInfo implements BasePhotoInfo {
    constructor(
        readonly name: string,
        readonly dateTaken: Date,
        readonly thumbnailUrl: string,
        readonly directory: string,
        readonly size: number
    ) {}

    get baseFileName() {
        return this.name
            .split('.')
            .slice(0, -1)
            .join('.');
    }

    get extension() {
        return this.name.split('.').pop();
    }

    get mediaType() {
        return extToMime.get(this.extension) || 'application/octet-stream';
    }
}
