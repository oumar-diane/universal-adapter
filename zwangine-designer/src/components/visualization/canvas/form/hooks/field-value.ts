import { useContext } from 'react';
import {safeGetValue} from "@/lib/utility";
import {ModelContext} from "@/components/visualization";

export const useFieldValue = <T = unknown>(propertyPath: string) => {
    const { model, errors, onPropertyChange } = useContext(ModelContext);
    const propertyName = propertyPath.replace('#.', '');
    const value = safeGetValue(model, propertyName) as T | undefined;

    const propertyErrors = errors?.[propertyPath];

    const onChange = (value: T) => {
        onPropertyChange(propertyName, value);
    };

    return {
        value,
        errors: propertyErrors,
        onChange,
    };
};
