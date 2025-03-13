import {Route, Routes} from "react-router";
import {Designer} from "@/pages/Designer.tsx";
import {Manager} from "@/pages/Manager.tsx";
import {Dashboard} from "@/pages/Dashboard.tsx";


export function Router() {
    return (
        <Routes>
            <Route index
            element={<Designer/>}
            />
            <Route path="manager"
            element={<Manager/>}
            />
            <Route path="dashboard"
            element={<Dashboard/>}
            />
        </Routes>

    )
}