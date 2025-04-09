import { getValue } from './get-value.ts';
import { setValue } from './set-value.ts';

export const getArrayProperty = <T>(model: object, path: string): T[] => {
    let stepsArray: T[] | undefined = getValue(model, path);

    if (!Array.isArray(stepsArray)) {
        setValue(model, path, []);
        stepsArray = getValue(model, path) as T[];
    }

    return stepsArray;
};
