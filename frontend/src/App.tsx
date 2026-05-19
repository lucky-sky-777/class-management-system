import { MainLayout } from '@shared/components/layout/MainLayout';
import { Outlet } from 'react-router-dom';
import { GlobalToast } from '@shared/components/ui/GlobalToast';

function App() {
  return (
    // <div className="app-container">
    //   <Outlet />
    // </div>
    <MainLayout>
      <Outlet/>
      <GlobalToast />
    </MainLayout>
  );
}

export default App;

