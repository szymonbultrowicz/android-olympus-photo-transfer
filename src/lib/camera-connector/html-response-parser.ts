import { PhotoFileInfo } from './photo-file-info';

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
            console.log(thumbnailUrl);
            result.push(
                new PhotoFileInfo(name, new Date(), thumbnailUrl, dir, parseInt(size, 10))
            );
        }
    } while (match !== null);

    return result;
}
