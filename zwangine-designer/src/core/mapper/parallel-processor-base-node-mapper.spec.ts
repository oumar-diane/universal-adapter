import { describe, expect, it, beforeEach} from "vitest"
import { MulticastNodeMapper } from "./multicast-node-mapper";
import { LoadBalanceNodeMapper } from "./loadbalance-node-mapper";
import {RootNodeMapper} from "./root-node-mapper.ts";
import {IElementLookupResult} from "@/core/model/component-type.ts";

let mapper: MulticastNodeMapper | LoadBalanceNodeMapper;
let path: string;
let routeDefinition: unknown;

describe.each([
    ['multicast', MulticastNodeMapper],
    ['loadBalance', LoadBalanceNodeMapper],
])("with '%s'", (processorName, Mapper) => {
    beforeEach(() => {
        const rootNodeMapper = new RootNodeMapper();
        rootNodeMapper.registerDefaultMapper(mapper);

        mapper = new Mapper(rootNodeMapper);

        path = `from.steps.0.${processorName}`;
    });

    describe('getVizNodeFromProcessor', () => {
        it('should return a VisualizationNode', () => {
            routeDefinition = {
                from: {
                    uri: 'timer',
                    parameters: {
                        timerName: 'timerName',
                    },
                    steps: [
                        {
                            [processorName]: {
                                id: `${processorName}-123`,
                            },
                        },
                    ],
                },
            };
            const vizNode = mapper.getVizNodeFromProcessor(path, {} as IElementLookupResult, routeDefinition);

            expect(vizNode).toBeDefined();
            expect(vizNode.data).toMatchObject({
                path,
                icon: expect.any(String),
                processorName,
                isGroup: true,
            });
        });

        it('should return a VisualizationNode with children', () => {
            routeDefinition = {
                from: {
                    uri: 'timer',
                    parameters: {
                        timerName: 'timerName',
                    },
                    steps: [
                        {
                            [processorName]: {
                                id: `${processorName}-123`,
                                steps: [
                                    {
                                        log: {
                                            id: 'log-123',
                                            message: 'test',
                                        },
                                    },
                                    {
                                        log: {
                                            id: 'log-456',
                                            message: 'test',
                                        },
                                    },
                                ],
                            },
                        },
                    ],
                },
            };

            const vizNode = mapper.getVizNodeFromProcessor(path, {} as IElementLookupResult, routeDefinition);
            expect(vizNode.getChildren()).toHaveLength(2);
            expect(vizNode.getChildren()?.[0].getNextNode()).toBeUndefined();
            expect(vizNode.getChildren()?.[1].getPreviousNode()).toBeUndefined();
        });
    });
});