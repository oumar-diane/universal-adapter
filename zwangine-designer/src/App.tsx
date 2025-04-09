import './App.css'
import {MainContent, MainHeader, MainLayout} from "@/layouts/layout-wrappers.tsx";
import {BrowserRouter} from "react-router";
import {Router} from "@/route/router.tsx";
import {MainHeaderActiveProvider} from "@/providers/main-header-active-provider.tsx";
import {
    CatalogLoaderProvider,
    CatalogTilesProvider,
    EntitiesProvider,
    SchemasLoaderProvider,
    SourceCodeProvider, VisibleFlowsProvider
} from "@/providers";
import {useReload} from "@/hooks";
import {VisualizationProvider} from "@patternfly/react-topology";
import {RenderingProvider} from "@/components/renderingAnchor";
import {RegisterComponents} from "@/components/registers";
import {useMemo} from "react";
import {ControllerService} from "@/components/visualization";
import {StompSessionProvider} from "@/providers/stomp-session-provider.tsx";

function App() {

    const ReloadProvider = useReload();
    const controller = useMemo(() => ControllerService.createController(), []);

    return (
      <ReloadProvider>
          <StompSessionProvider url={'ws://localhost:8080/ws'}>
              <SourceCodeProvider >
                  <EntitiesProvider>
                      <MainHeaderActiveProvider>
                          <SchemasLoaderProvider>
                              <CatalogLoaderProvider>
                                  <CatalogTilesProvider>
                                      <VisualizationProvider controller={controller}>
                                          <VisibleFlowsProvider>
                                              <RenderingProvider>
                                                  <RegisterComponents>
                                                      <MainLayout>
                                                          <BrowserRouter>
                                                              <MainHeader/>
                                                              <MainContent>
                                                                  <Router/>
                                                              </MainContent>
                                                          </BrowserRouter>
                                                      </MainLayout>
                                                  </RegisterComponents>
                                              </RenderingProvider>
                                          </VisibleFlowsProvider>
                                      </VisualizationProvider>
                                  </CatalogTilesProvider>
                              </CatalogLoaderProvider>
                          </SchemasLoaderProvider>
                      </MainHeaderActiveProvider>
                  </EntitiesProvider>
              </SourceCodeProvider>
          </StompSessionProvider>
      </ReloadProvider>
  )
}



export default App
