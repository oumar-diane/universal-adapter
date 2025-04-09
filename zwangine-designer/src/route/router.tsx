import {Route, Routes} from "react-router";
import {DesignerPage} from "@/pages/designer/designer-page.tsx";
import {Manager} from "@/pages/manager/Manager.tsx";
import {Dashboard} from "@/pages/dashboard/Dashboard.tsx";
import {CatalogPage} from "@/pages/catalog";
import {CanvasPage} from "@/pages/canvas";


export function Router() {
    return (
        <Routes>
            <Route path={"/"} element={<DesignerPage/>}>
                <Route index element={<CanvasPage/>} />
                <Route path={"catalog"} element={<CatalogPage/>} />
            </Route>
            <Route path="manager" element={<Manager/>} />
            <Route path="dashboard" element={<Dashboard/>}
            />
        </Routes>

    )
}