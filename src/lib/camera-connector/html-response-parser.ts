import { PhotoFileInfo } from './photo-file-info';
import {binaryToDateTime} from '~/lib/camera-connector/binary-dates-converters';

const fileRegex = /wlan.*=.*,(.*),(\d+),(\d+),(\d+),(\d+)/g;

export function retrieveDirectories(html: string): string[] {
    const result: string[] = [];
    let match: RegExpExecArray | null;
    do {
        match = fileRegex.exec(html);
        if (match !== null) {
            result.push(match[1]);
        }
    } while (match !== null);
    return result;
}

export function retrieveFiles(dir: string, html: string, thumbnailUrlBuilder: (name: string) => string): PhotoFileInfo[] {
    const result: PhotoFileInfo[] = [];
    let match: RegExpExecArray | null;
    do {
        match = fileRegex.exec(html);
        if (match !== null) {
            const [, name, size, , dateBinary, timeBinary] = match;
            const thumbnailUrl = thumbnailUrlBuilder(name);
            result.push(
                new PhotoFileInfo(
                    name,
                    binaryToDateTime(parseInt(dateBinary, 10), parseInt(timeBinary, 10)),
                    thumbnailUrl,
                    dir,
                    parseInt(size, 10)
                ),
            );
        }
    } while (match !== null);

    return result;
}