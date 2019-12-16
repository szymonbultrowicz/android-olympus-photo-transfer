function maskShift(i: number, upperMaskBitPos: number, lowerMaskBitPos: number): number {
    // tslint:disable-next-line:no-bitwise
    return (i % (2 << upperMaskBitPos)) >> lowerMaskBitPos;
}

function binaryToDate(binaryDate: number) {
    // ..yyyyyyyymmmmddddd
    //   65432109876543210
    const days = maskShift(binaryDate, 4, 0)
    const months = maskShift(binaryDate, 8, 5)
    const years = maskShift(binaryDate, 16, 9) + 1980
    return { years, months, days };
}

function binaryToTime(binaryTime: number) {
    // ...hhhhhhmmmmmmsssss
    //    65432109876543210
    const seconds = maskShift(binaryTime, 4, 0)
    const minutes = maskShift(binaryTime, 10, 5)
    const hours = maskShift(binaryTime, 16, 11)
    return { hours, minutes, seconds };
}

export function binaryToDateTime(binaryDate: number, binaryTime: number): Date {
    const { years, months, days } = binaryToDate(binaryDate);
    const { hours, minutes, seconds } = binaryToTime(binaryTime);
    return new Date(years, months, days, hours, minutes, seconds);
}
