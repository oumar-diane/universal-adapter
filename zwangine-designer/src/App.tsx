import './App.css'
import {MainContent, MainHeader, MainLayout} from "@/components/layout/main.tsx";
import {BrowserRouter} from "react-router";
import {Router} from "@/components/route/router.tsx";

function App() {

  return (
      <MainLayout>
          <BrowserRouter>
              <MainHeader/>
              <MainContent>
                  <Router/>
              </MainContent>
          </BrowserRouter>
      </MainLayout>
  )
}

export default App
