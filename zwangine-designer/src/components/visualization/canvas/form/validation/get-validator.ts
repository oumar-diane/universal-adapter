import Ajv, { ValidateFunction } from 'ajv';
import {SchemaDefinition} from "@/core";

export const getValidator = (schema: SchemaDefinition['schema']) => {
    const ajv = new Ajv({ strict: false, allErrors: true, useDefaults: true });

    let validator: ValidateFunction | undefined;
    try {
        validator = ajv.compile(schema);
    } catch (error) {
        console.error('[form Validator]: Could not compile schema', error);
    }

    return validator;
};
