// imageCompression.js
// 의존성 설치: npm i browser-image-compression
import imageCompression from 'browser-image-compression';

/**
 * 이미지 파일을 압축합니다.
 * @param {File} file - 원본 이미지 파일
 * @param {Object} rule
 * @param {number} [rule.maxWidth=1920]   - 최대 가로
 * @param {number} [rule.maxHeight=1920]  - 최대 세로
 * @param {number} [rule.maxSizeMB=0.6]   - 목표 용량(MB)
 * @param {number} [rule.quality=0.7]     - 0~1 (JPEG/WEBP 품질)
 * @param {boolean} [rule.forceJpegFromPng=true] - PNG를 JPG로 전환해 용량 절감
 * @returns {Promise<File>} 압축된 파일
 */
export async function compressImageFile(file, rule = {}) {
    const {
        maxWidth = 1920,
        maxHeight = 1920,
        maxSizeMB = 0.6,
        quality = 0.7,
        forceJpegFromPng = true,
    } = rule;

    const maxSide = Math.max(maxWidth || 0, maxHeight || 0) || 1920;
    const opts = {
        maxWidthOrHeight: maxSide,
        maxSizeMB,
        initialQuality: quality,
        useWebWorker: true,
        fileType: forceJpegFromPng && file.type === 'image/png' ? 'image/jpeg' : undefined,
        alwaysKeepResolution: false,
    };

    const blob = await imageCompression(file, opts);
    const extFromType = (blob.type.split('/')[1] || 'jpeg').replace('jpeg', 'jpg');
    const base = file.name.replace(/\.[^/.]+$/, '');
    return new File([blob], `${base}-compressed.${extFromType}`, {
        type: blob.type,
        lastModified: Date.now(),
    });
}

/** 보기좋게 바이트 표기 */
export function formatBytes(bytes) {
    if (bytes < 1024) return `${bytes} B`;
    const kb = bytes / 1024;
    if (kb < 1024) return `${kb.toFixed(1)} KB`;
    return `${(kb / 1024).toFixed(2)} MB`;
}
