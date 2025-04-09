import { Content, Timestamp, TimestampFormat, TimestampTooltipVariant } from '@patternfly/react-core';
import { FunctionComponent, PropsWithChildren, useMemo } from 'react';
import { RELEASE_DATE, RELEASE_HASH, UNIVERSAL_ADAPTER_VERSION } from '@/version.ts';

const TOOLTIP_PROPS = { variant: TimestampTooltipVariant.default } as const;

export const About: FunctionComponent<PropsWithChildren> = ({ children }) => {
    const buildDate = useMemo(() => new Date(RELEASE_DATE), []);

    return (
        <Content>
            <Content component="dl">
                {children}

                <Content component="dt">
                    <strong>Universal Adapter UI</strong>
                </Content>
                <Content component="dt">
                    <strong>Version</strong>
                </Content>
                <Content component="dd" data-testid="about-version">
                    {UNIVERSAL_ADAPTER_VERSION}
                </Content>
                <Content component="dt">
                    <strong>Release hash</strong>
                </Content>
                <Content component="dd" data-testid="about-git-commit-hash">
                    {RELEASE_HASH}
                </Content>
                <Content component="dt">
                    <strong>Release date</strong>
                </Content>
                <Content component="dd" data-testid="about-git-last-commit-date">
                    <Timestamp
                        date={buildDate}
                        dateFormat={TimestampFormat.full}
                        timeFormat={TimestampFormat.long}
                        tooltip={TOOLTIP_PROPS}
                    />
                </Content>
            </Content>
        </Content>
    );
};
