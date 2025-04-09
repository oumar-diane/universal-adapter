import {getHexaDecimalRandomId, getRandomId} from '@/lib/ua-utils';
import { parse } from 'yaml';
import {XSLT_COMPONENT_NAME} from "@/lib/utility";
import { ProcessorDefinition } from '@/@types';
import {DefinedComponent} from "./model/catalog/catalog-index.ts";
import {CatalogKind} from "./model/catalog/catalog-kind.ts";

/**
 * ComponentDefaultService
 *
 * This class is meant to provide working default values for  components.
 */
export class ComponentDefaultService {
    /**
     * Get the default definition for the `from` component
     */
    static getDefaultFromDefinitionValue(definedComponent: DefinedComponent): ProcessorDefinition {
        const uri = definedComponent.name;
        return parse(`
          id: ${getRandomId('from')}
          uri: "${uri}"
          parameters: {}
        `);
    }

    /**
     * Get the default value for a given component and property
     */
    static getDefaultNodeDefinitionValue(definedComponent: DefinedComponent): ProcessorDefinition {
        switch (definedComponent.type) {
            case CatalogKind.Component:
                return this.getDefaultValueFromComponent(definedComponent.name);
            case CatalogKind.Processor:
            case CatalogKind.Entity:
                return this.getDefaultValueFromProcessor(definedComponent.name as keyof ProcessorDefinition);
            default:
                return {};
        }
    }

    private static getDefaultValueFromComponent(componentName: string): object {
        switch (componentName) {
            case 'log':
                return parse(`
          to:
            id: ${getRandomId('to')}
            uri: log:InfoLogger
            parameters: {}
        `);

            default:
                return parse(`
          to:
            id: ${getRandomId('to')}
            uri: "${componentName}"
            parameters: {}
        `);
        }
    }

    private static getDefaultValueFromProcessor(processorName: keyof ProcessorDefinition): ProcessorDefinition {
        switch (processorName) {
            case 'circuitBreaker':
                return parse(`
        circuitBreaker:
          id: ${getRandomId('circuitBreaker')}
          steps: []
          onFallback:
            id: ${getRandomId('onFallback')}
            steps:
            - log:
                id: ${getRandomId('log')}
                message: "\${body}"
        `);

            case 'choice':
                return parse(`
        choice:
          id: ${getRandomId('choice')}
          when:
          - id: ${getRandomId('when')}
            expression:
              simple:
                expression: "\${header.foo} == 1"
            steps:
            - log:
                id: ${getRandomId('log')}
                message: "\${body}"
          otherwise:
            id: ${getRandomId('otherwise')}
            steps:
            - log:
                id: ${getRandomId('log')}
                message: "\${body}"
        `);

            case 'when':
                return parse(`
        id: ${getRandomId('when')}
        expression:
          simple:
            expression: "\${header.foo} == 1"
        steps:
        - log:
            id: ${getRandomId('log')}
            message: "\${body}"
      `);

            case 'doTry':
                return parse(`
        doTry:
          id: ${getRandomId('doTry')}
          doCatch:
            - id: ${getRandomId('doCatch')}
              exception:
                - java.lang.NullPointerException
              steps: []
          doFinally:
            id: ${getRandomId('doFinally')}
            steps: []
          steps:
            - log:
                id: ${getRandomId('log')}
                message: "\${body}"
        `);

            case 'otherwise':
            case 'doFinally':
                return parse(`
        id: ${getRandomId(processorName)}
        steps:
          - log:
              id: ${getRandomId('log')}
              message: "\${body}"
      `);

            case 'log':
                return parse(`
        log:
          id: ${getRandomId('log')}
          message: "\${body}"
        `);

            case 'removeHeaders':
                return parse(`
        removeHeaders:
          id: ${getRandomId('removeHeaders')}
          pattern: "*"
        `);

            case 'doCatch':
                return parse(`
          id: ${getRandomId('doCatch')}
          exception:
            - java.lang.NullPointerException
          steps: []
        `);

            case 'setHeader':
            case 'setProperty':
            case 'setVariable':
            case 'setBody':
            case 'filter':
                return parse(`
        ${processorName}:
          id: ${getRandomId(processorName)}
          expression:
            simple: {}
        `);

            case 'ua-datamapper' as keyof ProcessorDefinition:
                return parse(`
          step:
            id: ${getHexaDecimalRandomId('ua-datamapper')}
            steps:
              - to:
                  id: ${getRandomId('ua-datamapper-xslt')}
                  uri: ${XSLT_COMPONENT_NAME}
                  parameters: {}
          `);

            case 'delete' as keyof ProcessorDefinition:
            case 'get' as keyof ProcessorDefinition:
            case 'head' as keyof ProcessorDefinition:
            case 'patch' as keyof ProcessorDefinition:
            case 'post' as keyof ProcessorDefinition:
            case 'put' as keyof ProcessorDefinition:
                return { id: getRandomId(processorName) } as ProcessorDefinition;

            default:
                return {
                    [processorName]: {
                        id: getRandomId(processorName),
                    },
                };
        }
    }

}
