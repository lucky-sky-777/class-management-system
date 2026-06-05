import { MainLayout } from "@shared/components/layout/MainLayout";
import { Outlet } from "react-router-dom";
import { GlobalToast } from "@shared/components/ui/GlobalToast";
import { usePageTitle } from "@shared/hooks/usePageTitle";

function App() {
    usePageTitle();

    return (
        <MainLayout>
            <Outlet />
            <GlobalToast />
        </MainLayout>
    );
}

export default App;
