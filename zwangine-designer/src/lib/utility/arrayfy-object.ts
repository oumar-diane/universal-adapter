

export function toArray(obj:any): any[] {
    if (Array.isArray(obj)) {
        return obj;
    }
    if (obj === null || obj === undefined) {
        return [];
    }
    return [obj];
}
