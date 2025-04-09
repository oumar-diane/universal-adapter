/** Universal Adapter catalog kinds */
export const enum CatalogKind {
    /** Universal Adapter components catalog, f.i. amqp, log, timer */
    Component = 'component',

    /** Universal Adapter model catalog, f.i. route, from, eips, routeTemplate, languages, dataformats, loadbalancer */
    Processor = 'processor',

    /** Universal Adapter processors (EIPs) definitions, f.i. log, to, toD, transform, filter */
    Pattern = 'pattern',

    /** Universal Adapter entities catalog, f.i. from, route, routeTemplate */
    Entity = 'entity',

    /** Universal Adapter languages catalog, f.i. simple, groovy, kotlin */
    Language = 'language',

    /** Universal Adapter dataformats catalog, f.i. json, xml, csv */
    Dataformat = 'dataformat',

    /** Universal Adapter loadbalancer catalog, f.i. round robin, failover, random */
    Loadbalancer = 'loadbalancer',
}
