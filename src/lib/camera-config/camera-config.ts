export interface CameraConfig {
    port: number | undefined;
    protocol: string;
    host: string;
    path: string;
}

export const defaultCameraConfig: CameraConfig = {
    port: 8000,
    protocol: 'http',
    host: '192.168.1.100',
    path: 'DCIM',
};
